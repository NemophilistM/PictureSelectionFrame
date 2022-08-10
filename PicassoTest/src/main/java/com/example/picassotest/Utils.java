package com.example.picassotest;

import static android.os.Build.VERSION_CODES.HONEYCOMB_MR1;
import static android.os.Process.THREAD_PRIORITY_BACKGROUND;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Looper;
import android.os.Process;

import com.example.picassotest.RequestHandler.DownLoad;
import com.example.picassotest.RequestHandler.Request;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ThreadFactory;

public final class Utils {

    static final char KEY_SEPARATOR = '\n';

    /**
     * 线程仅限于主线程以创建密钥
     */
    static final StringBuilder MAIN_THREAD_KEY_BUILDER = new StringBuilder();

    /**
     * 不知道有什么用，翻译是密钥填充
     */
    private static final int KEY_PADDING = 50;

    /**
     * @param context 上下文
     * @return 内存最大值的1/7
     */
    static int calculateMemoryCacheSize(Context context){
        ActivityManager am = getService(context,Constants.ACTIVITY_SERVICE);
//        boolean largeHeap = (context.getApplicationInfo().flags&Constants.FLAG_LARGE_HEAP)!=0;
        int memoryClass = am.getMemoryClass();
        return 1024 *1024 *memoryClass/7;
    }

    /**
     * @param context 上下问
     * @param service 标识
     * @param <T> 泛型
     * @return 返回活动管理器
     */
    @SuppressWarnings("unchecked")
    static <T> T getService(Context context, String  service){
        return (T) context.getSystemService(service);
    }

    /**
     * @param bitmap 位图
     * @return 返回存储该位图所需的最小字节数
     */
    static int getBitmapBytes(Bitmap bitmap) {
        int result;
            result = BitmapHoneycombMR1.getByteCount(bitmap);
        if (result < 0) {
            throw new IllegalStateException("Negative size: " + bitmap);
        }
        return result;
    }

    /**
     * 线程工程
     */
    static class PicassoThreadFactory implements ThreadFactory {
        public Thread newThread(Runnable r) {
            return new PicassoThread(r);
        }
    }

    /**
     * 返回一个线程
     */
    private static class PicassoThread extends Thread {
        public PicassoThread(Runnable r) {
            super(r);
        }

        @Override public void run() {
            Process.setThreadPriority(THREAD_PRIORITY_BACKGROUND);
            super.run();
        }
    }

    /**
     * 关闭输入流
     */
    public static void closeQuietly(InputStream is) {
        if (is == null) return;
        try {
            is.close();
        } catch (IOException ignored) {
        }
    }

    /**
     * 获取容纳位图的最小字节数
     */
    @TargetApi(HONEYCOMB_MR1)
    private static class BitmapHoneycombMR1 {
        static int getByteCount(Bitmap bitmap) {
            return bitmap.getByteCount();
        }
    }

    /**
     * @param value 检测值
     * @param message 打印的信息
     * @param <T> 传入值的类型
     * @return 返回原本的值
     */
    public static <T> T checkNotNull(T value, String message) {
        if (value == null) {
            throw new NullPointerException(message);
        }
        return value;
    }


    public static String createKey(Request data) {
        String result = createKey(data, MAIN_THREAD_KEY_BUILDER);
        MAIN_THREAD_KEY_BUILDER.setLength(0);
        return result;
    }
    static String createKey(Request data, StringBuilder builder) {
        if (data.stableKey != null) {
            // 该代码确定了这个StringBuilder的容量为指定值
            builder.ensureCapacity(data.stableKey.length() + KEY_PADDING);
            builder.append(data.stableKey);
        } else if (data.uri != null) {
            String path = data.uri.toString();
            builder.ensureCapacity(path.length() + KEY_PADDING);
            builder.append(path);
        }
        builder.append(KEY_SEPARATOR);

        if (data.rotationDegrees != 0) {
            builder.append("rotation:").append(data.rotationDegrees);
            builder.append(KEY_SEPARATOR);
        }
        if (data.hasSize()) {
            builder.append("resize:").append(data.targetWidth).append('x').append(data.targetHeight);
            builder.append(KEY_SEPARATOR);
        }
        if (data.centerCrop) {
            builder.append("centerCrop").append(KEY_SEPARATOR);
        } else if (data.centerInside) {
            builder.append("centerInside").append(KEY_SEPARATOR);
        }

        return builder.toString();
    }

    /**
     * 用于判断是否在主线程
     */
    public static void checkNotMain() {
        if (isMain()) {
            throw new IllegalStateException("Method call should not happen from the main thread.");
        }
    }

    /**
     * 用于判断是否在主线程
     */
    public static void checkMain() {
        if (!isMain()) {
            throw new IllegalStateException("Method call should happen from the main thread.");
        }
    }

    static boolean isMain() {
        return Looper.getMainLooper().getThread() == Thread.currentThread();
    }

    public static DownLoad createDownLoadFromUri(Context context){
        return new DownLoad(context);
    }

}
