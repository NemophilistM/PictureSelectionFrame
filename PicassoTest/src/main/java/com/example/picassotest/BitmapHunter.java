package com.example.picassotest;

import static com.example.picassotest.PicassoTest.LoadedFrom.MEMORY;
import static com.example.picassotest.PicassoTest.Priority.LOW;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.example.picassotest.action.Action;
import com.example.picassotest.requestHandler.Request;
import com.example.picassotest.requestHandler.RequestHandler;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 一个线程，负责加载图片
 */
public class BitmapHunter implements Runnable {

    /**
     * 位图解码的全局锁定，以确保我们一次只解码一个。由于这只会在后台线程中发生，因此我们有助于避免过度的内存抖动以及潜在的 OOM。无耻地从凌空偷走
     */
    private static final Object DECODE_LOCK = new Object();

    private static final AtomicInteger SEQUENCE_GENERATOR = new AtomicInteger();

    private static final RequestHandler ERROR_HANDLER = new RequestHandler() {
        @Override public boolean canHandleRequest(Request data) {
            return true;
        }

        @Override
        public Result load(Request request) throws IOException {
            Log.d(Constants.TAG, "BitmapHunter.load: 没有对应的RequestHandler来创建bitmapHunter");
            throw new IllegalStateException("Unrecognized type of request: " + request);
        }
    };


    final int sequence;
    final PicassoTest picasso;
    final Dispatcher dispatcher;
    final Cache cache;
    final String key;
    final Request data;
    final RequestHandler requestHandler;
    Action action;
    List<Action> actions;
    Bitmap result;
    PicassoTest.LoadedFrom loadedFrom;
    // 存储线程的返回结果
    Future<?> future;

    PicassoTest.Priority priority;


    public BitmapHunter(PicassoTest picasso, Dispatcher dispatcher, Cache cache, RequestHandler requestHandler, Action action) {
        this.sequence = SEQUENCE_GENERATOR.incrementAndGet();
        this.picasso = picasso;
        this.dispatcher = dispatcher;
        this.cache = cache;
        this.key = action.getKey();
        this.data = action.getRequest();
        this.requestHandler = requestHandler;
        this.action = action;
        this.priority = action.getPriority();
    }

    static Bitmap decodeStream(InputStream stream, Request request) throws IOException {
        MarkableInputStream markStream = new MarkableInputStream(stream);
        stream = markStream;

        long mark = markStream.savePosition(65536); // TODO fix this crap.

        final BitmapFactory.Options options = RequestHandler.createBitmapOptions(request);
        final boolean calculateSize = RequestHandler.requiresInSampleSize(options);

        //由于并不是从网络获取图片，因此不会是网络流，所以可以直接放弃这一段
//        boolean isWebPFile = Utils.isWebPFile(stream);
        markStream.reset(mark);
        // When decode WebP network stream, BitmapFactory throw JNI Exception and make app crash.
        // Decode byte array instead
//        if (isWebPFile) {
//            byte[] bytes = Utils.toByteArray(stream);
//            if (calculateSize) {
//                BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
//                RequestHandler.calculateInSampleSize(request.targetWidth, request.targetHeight, options,
//                        request);
//            }
//            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
//        }

            if (calculateSize) {
                BitmapFactory.decodeStream(stream, null, options);
                RequestHandler.calculateInSampleSize(request.targetWidth, request.targetHeight, options,
                        request);

                markStream.reset(mark);
            }
            Bitmap bitmap = BitmapFactory.decodeStream(stream, null, options);
            if (bitmap == null) {
                // Treat null as an IO exception, we will eventually retry.
                Log.d(Constants.TAG, "BitmapHunter.decodeStream: 从数据流中加载图片,图片为空，抛出异常");
                throw new IOException("Failed to decode stream.");
            }
        return bitmap;
        }



    @Override
    public void run() {
        try {
            Log.d(Constants.TAG, "run: 开始运行线程" + Thread.currentThread());

            result = hunt();

            if (result == null) {
                dispatcher.dispatchFailed(this);
            } else {
                dispatcher.dispatchComplete(this);
            }
        } catch (IOException e) {
            Log.d(Constants.TAG, "BitmapHunter.run: 下载hunt出现异常");
            e.printStackTrace();
        }
    }

    private Bitmap hunt() throws IOException {
        Bitmap bitmap;

        // 先从缓存中拿
        bitmap = cache.get(key);
        if (bitmap != null) {
            loadedFrom = MEMORY;
            Log.d(Constants.TAG, "hunt: 成功从缓存中拿取照片");
            return bitmap;
        }

        // 开始下载图片
        RequestHandler.Result result = requestHandler.load(data);

        if (result != null) {
            loadedFrom = result.getLoadedFrom();
            bitmap = result.getBitmap();

            if (bitmap == null) {
                InputStream inputStream = result.getStream();
                try {
                   bitmap =  decodeStream(inputStream, data);
                }finally {
                    Utils.closeQuietly(inputStream);
                }
            }
        }
        return bitmap;
    }

    String getKey() {
        return key;
    }

    Bitmap getResult() {
        return result;
    }

    Request getData() {
        return data;
    }

    Action getAction() {
        return action;
    }

    PicassoTest getPicasso() {
        return picasso;
    }

    List<Action> getActions() {
        return actions;
    }

    PicassoTest.Priority getPriority() {
        return priority;
    }

    PicassoTest.LoadedFrom getLoadedFrom() {
        return loadedFrom;
    }

    public void attach(Action action) {
        Request request = action.request;
        if (this.action == null) {
            this.action = action;
            return;
        }

        if (actions == null) {
            actions = new ArrayList<>(3);
        }
        actions.add(action);

        PicassoTest.Priority actionPriority = action.getPriority();
        if (actionPriority.ordinal() > priority.ordinal()) {
            priority = actionPriority;
        }
    }

    public void detach(Action action) {
        boolean detached = false;
        if (this.action == action) {
            this.action = null;
            detached = true;
        } else if (actions != null) {
            detached = actions.remove(action);
        }

//         The action being detached had the highest priority. Update this
//         hunter's priority with the remaining actions.
//         被分离的动作具有最高优先级。用剩余的动作更新这个猎人的优先级
        if (detached && action.getPriority() == priority) {
            priority = computeNewPriority();
        }

    }

    private PicassoTest.Priority computeNewPriority() {
        PicassoTest.Priority newPriority = LOW;

        boolean hasMultiple = actions != null && !actions.isEmpty();
        boolean hasAny = action != null || hasMultiple;

        // Hunter has no requests, low priority.
        if (!hasAny) {
            return newPriority;
        }

        if (action != null) {
            newPriority = action.getPriority();
        }

        if (hasMultiple) {
            for (int i = 0, n = actions.size(); i < n; i++) {
                PicassoTest.Priority actionPriority = actions.get(i).getPriority();
                if (actionPriority.ordinal() > newPriority.ordinal()) {
                    newPriority = actionPriority;
                }
            }
        }

        return newPriority;
    }

    boolean cancel() {
        return action == null
                && (actions == null || actions.isEmpty())
                && future != null
                && future.cancel(false);
    }

    /**
     * 创建BitmapHunter,要在这个方法里面遍历PicassoTest里面RequestHandler，来甄别选择哪个哪个处理策略，再看看从哪个里面下载
     */
    public static BitmapHunter forRequest(PicassoTest picasso, Dispatcher dispatcher, Cache cache,
                                          Action action) {
        Request request = action.getRequest();
        List<RequestHandler> requestHandlers = picasso.getRequestHandlers();
        for (int i = 0, count = requestHandlers.size(); i < count; i++) {
            RequestHandler requestHandler = requestHandlers.get(i);
            if (requestHandler.canHandleRequest(request)) {
                return new BitmapHunter(picasso, dispatcher, cache, requestHandler, action);
            }
        }
        return new BitmapHunter(picasso, dispatcher, cache, ERROR_HANDLER, action);
    }

    /**
     * 判断handler里面的值
     */
    boolean isCancelled() {
        return future != null && future.isCancelled();
    }

}
