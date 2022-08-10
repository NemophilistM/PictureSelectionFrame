package com.example.picassotest;

import static android.os.Process.THREAD_PRIORITY_BACKGROUND;

import static com.example.picassotest.BitmapHunter.forRequest;
import static com.example.picassotest.Constants.HUNTER_COMPLETE;
import static com.example.picassotest.Constants.REQUEST_BATCH_RESUME;
import static com.example.picassotest.Constants.TAG_PAUSE;
import static com.example.picassotest.Constants.TAG_RESUME;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.picassotest.Action.Action;
import com.example.picassotest.DownLoad.DownLoader;
import com.example.picassotest.DownLoad.OkHttpDownLoader;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;

/**
 * 分发器，负责调度各个任务
 */
public class Dispatcher {
    final Context context;
    final ExecutorService service;
    final DownLoader downLoader;
    final Map<String, BitmapHunter> hunterMap;
    final Handler handler;
    final Handler mainThreadHandler;
    final Cache cache;
    final List<BitmapHunter> batch;
    final DispatcherThread dispatcherThread;
    /**
     * 保存需要暂停的tag标记，用于当Picasso打算开启活动时停止活动
     */
    final Set<Object> pausedTags;

    /**
     * 存储被暂停了的action；
     */
    final Map<Object, Action> pausedActions;

    public Dispatcher(Context context, PicassoExecutorService service, Handler mainThreadHandler, DownLoader downLoader, Cache cache) {
        this.context = context;
        this.service = service;

        // 创建一个handlerThread线程，并开启它
        this.dispatcherThread = new DispatcherThread();
        this.dispatcherThread.start();

        this.downLoader = downLoader;

        //专门负责存储hunter的，对于同一个action不重复创建hunter节省资源
        this.hunterMap = new LinkedHashMap<String, BitmapHunter>();

        // 创建工作线程，利用handler调度
        this.handler = new DispatcherHandler(dispatcherThread.getLooper(), this);

        // 获取主线程实例
        this.mainThreadHandler = mainThreadHandler;

        // 缓存策略
        this.cache = cache;

        //  存储暂停标签的列表
        this.pausedTags = new HashSet<Object>();

        // 存储暂停的活动
        this.pausedActions = new WeakHashMap<Object, Action>();

        this.batch = new ArrayList<BitmapHunter>(4);
    }

    /**
     * 提交任务申请
     */
    void dispatchSubmit(Action action) {
        handler.sendMessage(handler.obtainMessage(Constants.REQUEST_SUBMIT, action));
    }

    /**
     * 解除任务
     */
    void dispatchCancel(Action action) {
        handler.sendMessage(handler.obtainMessage(Constants.REQUEST_CANCEL, action));
    }

    /**
     * 告知任务失败
     */
    void dispatchFailed(BitmapHunter hunter) {
        handler.sendMessage(handler.obtainMessage(Constants.HUNTER_DECODE_FAILED, hunter));
    }

    /**
     * 告知任务成功
     */
    void dispatchComplete(BitmapHunter hunter) {
        handler.sendMessage(handler.obtainMessage(Constants.HUNTER_COMPLETE, hunter));
    }

    /**
     * @param tag 开启异步线程将现在所有活动遍历一遍进而将其停止
     */
    void dispatchPauseTag(Object tag) {
        handler.sendMessage(handler.obtainMessage(TAG_PAUSE, tag));
    }

    /**
     * @param tag 开启异步线程将现在所有活动遍历一遍进而将其开启
     */
    void dispatchResumeTag(Object tag) {
        handler.sendMessage(handler.obtainMessage(TAG_RESUME, tag));
    }


    // 以下是异步下的方法调用

    void performSubmit(Action action) {
        BitmapHunter hunter = hunterMap.get(action.getKey());
        if (hunter != null) {
            hunter.attach(action);
            return;
        }
        if (service.isShutdown()) {
            return;
        }
        hunter = forRequest(action.getPicasso(), this, cache, action);
        hunter.future = service.submit(hunter);
        // 将hunter存储进hunterMap
        hunterMap.put(action.getKey(), hunter);
    }

    void performComplete(BitmapHunter hunter) {
        cache.set(hunter.getKey(), hunter.getResult());
        //将特定的bitmapHunter从map中移除
        hunterMap.remove(hunter.getKey());
        batch(hunter);

    }
    private void batch(BitmapHunter hunter) {
        if (hunter.isCancelled()) {
            return;
        }
        batch.add(hunter);
        if (!handler.hasMessages(Constants.HUNTER_DELAY_NEXT_BATCH)) {
            handler.sendEmptyMessageDelayed(Constants.HUNTER_DELAY_NEXT_BATCH, 200);
        }
    }

    void performBatchComplete() {
        List<BitmapHunter> copy = new ArrayList<BitmapHunter>(batch);
        batch.clear();
        mainThreadHandler.sendMessage(mainThreadHandler.obtainMessage(Constants.HUNTER_BATCH_COMPLETE, copy));

    }

    /**
     * 在此异步线程内将所有活动暂停
     */
    void performPauseTag(Object tag) {
        // 进行判断，当你存储暂停标签的列表里面有对应的标签时就直接返回
        if (!pausedTags.add(tag)) {
            return;
        }
        for (Iterator<BitmapHunter> it = hunterMap.values().iterator(); it.hasNext();) {
            BitmapHunter hunter = it.next();

            Action single = hunter.getAction();
            List<Action> joined = hunter.getActions();
            boolean hasMultiple = joined != null && !joined.isEmpty();

            // Hunter中没有存储请求，直接跳过该hunter
            if (single == null && !hasMultiple) {
                continue;
            }

            if (single != null && single.getTag().equals(tag)) {
                hunter.detach(single);
                pausedActions.put(single.getTarget(), single);
                Log.d(Constants.TAG, "Dispatch.performPauseTag: hunter被暂停");
            }

            if (hasMultiple) {
                for (int i = joined.size() - 1; i >= 0; i--) {
                    Action action = joined.get(i);
                    if (!action.getTag().equals(tag)) {
                        continue;
                    }

                    hunter.detach(action);
                    pausedActions.put(action.getTarget(), action);

                }
            }

            // Check if the hunter can be cancelled in case all its requests
            // had the tag being paused here.
            if (hunter.cancel()) {
                it.remove();
                Log.d(Constants.TAG, "Dispatch.performPauseTag: 所有hunter被暂停");
            }
        }

    }

    /**
     * @param tag 在异步线程内回复之前被暂停的活动
     */
    void performResumeTag(Object tag) {
        // 在此将可能要暂停但结果没有暂停的活动给辨别出来
        if (!pausedTags.remove(tag)) {
            return;
        }
        List<Action> batch = null;
        for (Iterator<Action> i = pausedActions.values().iterator(); i.hasNext();) {
            Action action = i.next();
            if (action.getTag().equals(tag)) {
                if (batch == null) {
                    batch = new ArrayList<Action>();
                }
                batch.add(action);
                i.remove();
            }
        }

        if (batch != null) {
            mainThreadHandler.sendMessage(mainThreadHandler.obtainMessage(REQUEST_BATCH_RESUME, batch));
        }
    }


    /**
     * 取消请求
     */
    void performCancel(Action action) {
        String key = action.getKey();
        BitmapHunter hunter = hunterMap.get(key);
        if (hunter != null) {
            hunter.detach(action);
            if (hunter.cancel()) {
                hunterMap.remove(key);
                Log.d(Constants.TAG, "BitmapHunter.performCancel: 从map列表移除hunter");
            }
        }
    }


    /**
     * 获取图片失败
     */
    void performError(BitmapHunter hunter, boolean willReplay) {
        hunterMap.remove(hunter.getKey());
        batch(hunter);
    }


    /**
     * 调度者的工作线程
     */
    static class DispatcherThread extends HandlerThread {
        DispatcherThread() {
            super(Constants.THREAD_PREFIX + Constants.DISPATCHER_THREAD_NAME, THREAD_PRIORITY_BACKGROUND);
        }
    }

    private static class DispatcherHandler extends Handler {
        private final Dispatcher dispatcher;

        public DispatcherHandler(@NonNull Looper looper, Dispatcher dispatcher) {
            super(looper);
            this.dispatcher = dispatcher;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case Constants.REQUEST_SUBMIT: {
                    Action action = (Action) msg.obj;
                    dispatcher.performSubmit(action);
                    break;
                }
                case HUNTER_COMPLETE: {
                    BitmapHunter hunter = (BitmapHunter) msg.obj;
                    dispatcher.performComplete(hunter);
                    break;
                }
                case Constants.HUNTER_DECODE_FAILED: {
                    BitmapHunter hunter = (BitmapHunter) msg.obj;
                    dispatcher.performError(hunter, false);
                    break;
                }
                case Constants.HUNTER_DELAY_NEXT_BATCH:
                    dispatcher.performBatchComplete();
                    break;
                case Constants.REQUEST_CANCEL:
                    Action action = (Action) msg.obj;
                    dispatcher.performCancel(action);
                    break;
                case TAG_PAUSE:
                    Object tag = msg.obj;
                    dispatcher.performPauseTag(tag);
                    break;
                case TAG_RESUME:
                    Object tag1  = msg.obj;
                    dispatcher.performResumeTag(tag1);
                    break;

            }
        }
    }
}
