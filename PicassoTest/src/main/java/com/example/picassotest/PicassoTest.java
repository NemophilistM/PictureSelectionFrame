package com.example.picassotest;

import static com.example.picassotest.Constants.REQUEST_BATCH_RESUME;
import static com.example.picassotest.PicassoTest.LoadedFrom.MEMORY;
import static com.example.picassotest.Utils.checkMain;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.example.picassotest.Action.Action;
import com.example.picassotest.RequestHandler.DownLoad;
import com.example.picassotest.RequestHandler.MediaStoreRequestHandler;
import com.example.picassotest.RequestHandler.RequestCreator;
import com.example.picassotest.RequestHandler.RequestHandler;

import java.lang.ref.ReferenceQueue;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public class PicassoTest {
    @SuppressLint("StaticFieldLeak")
    static volatile PicassoTest singleton = null;
    public final Context context;
    public final Cache cache;

    public final ReferenceQueue<Object> referenceQueue;

    /**
     * 用于判断Picasso是否被关闭
     */
    public boolean shutdown;

    /**
     * bitmap的配置
     */
    public final Bitmap.Config defaultBitmapConfig;

    private final List<RequestHandler> requestHandlers;

    /**
     * 存储action的map表
     */
    final Map<Object, Action> targetToAction;

    /**
     * 分发器
     */
    final Dispatcher dispatcher;

    private PicassoTest(Context context, Dispatcher dispatcher, List<RequestHandler> extraRequestHandlers,
                        Cache cache, Bitmap.Config defaultBitmapConfig) {
        this.context = context;
        this.cache = cache;
        this.defaultBitmapConfig = defaultBitmapConfig;
        //  存储action 的 map列表
        this.targetToAction = new WeakHashMap<>();
        this.dispatcher = dispatcher;
        // 创建队列用于存储弱引用的iv控件控件
        this.referenceQueue = new ReferenceQueue<>();
        //允许创建的调度者数量
        int builtInHandlers = 7;

        int extraCount = (extraRequestHandlers != null ? extraRequestHandlers.size() : 0);
        // 存储requestHandler的列表
        List<RequestHandler> allRequestHandlers =
                new ArrayList<>(builtInHandlers + extraCount);
        if (extraRequestHandlers != null) {
            allRequestHandlers.addAll(extraRequestHandlers);
        }
        allRequestHandlers.add(new MediaStoreRequestHandler(context));
        this.requestHandlers = Collections.unmodifiableList(allRequestHandlers);


    }

//    /**
//     * 阻止此实例接受进一步的请求。
//     */
//    public void shutdown() {
//        if (this == singleton) {
//            throw new UnsupportedOperationException("Default singleton instance cannot be shutdown.");
//        }
//        if (shutdown) {
//            return;
//        }
//        cache.clear();
//        cleanupThread.shutdown();
//        stats.shutdown();
//        dispatcher.shutdown();
//        for (DeferredRequestCreator deferredRequestCreator : targetToDeferredRequestCreator.values()) {
//            deferredRequestCreator.cancel();
//        }
//        targetToDeferredRequestCreator.clear();
//        shutdown = true;
//    }

    /**
     * 从缓存中拿取图片
     */
    public Bitmap quickMemoryCacheCheck(String key) {
        return cache.get(key);
    }


    /**
     * 获取requestHandler列表
     */
    public List<RequestHandler> getRequestHandlers() {
        return requestHandlers;
    }

    public void enqueueAndSubmit(Action action) {
        ImageView target = action.getTarget();
        if (target != null && targetToAction.get(target) != action) {
            // This will also check we are on the main thread.
            cancelExistingRequest(target);
            targetToAction.put(target, action);
        }
        submit(action);
    }

    /**
     * 提交任务给线程
     */
    void submit(Action action) {
        dispatcher.dispatchSubmit(action);
    }

    public static PicassoTest with(Context context) {
        if (singleton == null) {
            synchronized (PicassoTest.class) {
                if (singleton == null) {
                    singleton = new Builder(context).build();
                    Log.d(Constants.TAG, "PicassoTest.with: 单例实现创建PicassoTest");
                }
            }
        }
        return singleton;
    }

    public RequestCreator load(Uri uri) {
        return new RequestCreator(this, uri);
    }

    /**
     * @param tag 根据传入的tag对相应的活动进行暂停
     */
    public void pauseTag(Object tag) {
        dispatcher.dispatchPauseTag(tag);
    }


    /**
     * 使用给定的标签恢复暂停的请求。
     */
    public void resumeTag(Object tag) {
        dispatcher.dispatchResumeTag(tag);
    }


    /**
     * 取消掉存活的请求
     */
    public void cancelExistingRequest(ImageView target) {
        checkMain();
        Action action = targetToAction.remove(target);
        if (action != null) {
            action.cancel();
            dispatcher.dispatchCancel(action);
        }
        //原本如果有延迟请求，还需要将延迟请求也给remove掉，但是我没有采用fit方法，所以不打算写那个了


        Log.d(Constants.TAG, "PicassoTest.cancelExistingRequest:取消action，但是其下应该还有一个取消延迟请求的东西，但是不晓得用不用得着，留个日志方便记录查看是否需要 ");
    }

    /**
     * 下载成功，返回主线程进行ui更新
     */
    void complete(BitmapHunter hunter) {
        Action single = hunter.getAction();
        List<Action> joined = hunter.getActions();

        boolean hasMultiple = joined != null && !joined.isEmpty();
        boolean shouldDeliver = single != null || hasMultiple;

        if (!shouldDeliver) {
            return;
        }

        Uri uri = hunter.getData().uri;
        Bitmap result = hunter.getResult();
        LoadedFrom from = hunter.getLoadedFrom();

        if (single != null) {
            deliverAction(result, from, single);
        }

        if (hasMultiple) {

            for (int i = 0, n = joined.size(); i < n; i++) {
                Action join = joined.get(i);
                deliverAction(result, from, join);
            }
        }

    }

    /**
     * 在此处理被暂停后恢复的活动
     */
    void resumeAction(Action action) {
        Bitmap bitmap = quickMemoryCacheCheck(action.getKey());
        if (bitmap != null) {
            // 被暂停的活动已经缓存了
            deliverAction(bitmap, MEMORY, action);
        } else {
            // 重新提交活动
            enqueueAndSubmit(action);

        }
    }

    /**
     * 处理BitmapHunter中的图片
     */
    private void deliverAction(Bitmap result, LoadedFrom from, Action action) {
        if (action.isCancelled()) {
            return;
        }

        if (result != null) {
            if (from == null) {
                throw new AssertionError("LoadedFrom cannot be null.");
            }
            action.complete(result, from);
        } else {
            action.error();
        }
    }


    static final Handler HANDLER = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == Constants.HUNTER_BATCH_COMPLETE) {
                @SuppressWarnings("unchecked") List<BitmapHunter> batch = (List<BitmapHunter>) msg.obj;
                for (int i = 0, n = batch.size(); i < n; i++) {
                    BitmapHunter hunter = batch.get(i);
                    hunter.picasso.complete(hunter);
                }
            }else if(msg.what == REQUEST_BATCH_RESUME){
                @SuppressWarnings("unchecked") List<Action> batch = (List<Action>) msg.obj;
                for (int i = 0, n = batch.size(); i < n; i++) {
                    Action action = batch.get(i);
                    action.picasso.resumeAction(action);
                }
            }
        }
    };

    /**
     * 创建Picasso实例的内部类
     */
    public static class Builder {
        private final Context context;
        private Cache cache;
        private DownLoad downLoad;
        private List<RequestHandler> requestHandlers;
        private Bitmap.Config defaultBitmapConfig;
        private PicassoExecutorService service;

        public Builder(Context context) {
            if (context == null) {
                throw new IllegalArgumentException("Context must not be null.");
            }
            this.context = context.getApplicationContext();
        }

        public Builder defaultBitmapConfig(Bitmap.Config bitmapConfig) {
            if (bitmapConfig == null) {
                throw new IllegalArgumentException("Bitmap config must not be null.");
            }
            this.defaultBitmapConfig = bitmapConfig;
            return this;
        }

        /**
         * 添加requestHandler
         */
        public Builder addRequestHandler(RequestHandler requestHandler) {
            if (requestHandler == null) {
                throw new IllegalArgumentException("RequestHandler must not be null.");
            }
            if (requestHandlers == null) {
                requestHandlers = new ArrayList<>();
            }
            if (requestHandlers.contains(requestHandler)) {
                throw new IllegalStateException("RequestHandler already registered.");
            }
            requestHandlers.add(requestHandler);
            return this;
        }

        public PicassoTest build() {
            Context context = this.context;

            if (cache == null) {
                cache = new LruCache(context);
            }
            if (downLoad == null) {
                downLoad = Utils.createDownLoadFromUri(context);
            }

            if (service == null) {
                service = new PicassoExecutorService();
            }

            Dispatcher dispatcher = new Dispatcher(context, service, HANDLER, downLoad, cache);
            Log.d(Constants.TAG, "PicassoTest.Build(一个内部类).build: 返回一个PicassoTest实例");
            return new PicassoTest(context, dispatcher, requestHandlers, cache, defaultBitmapConfig);

        }
    }


    /**
     * 描述图像的加载位置
     */
    public enum LoadedFrom {
        MEMORY(Color.GREEN),
        DISK(Color.BLUE),
        NETWORK(Color.RED);

        final int debugColor;

        LoadedFrom(int debugColor) {
            this.debugColor = debugColor;
        }
    }

    /**
     * 请求的优先级
     */
    public enum Priority {
        LOW,
        NORMAL,
        HIGH
    }
}
