package com.example.picassotest.requestHandler;


import static androidx.core.util.Preconditions.checkNotNull;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.picassotest.PicassoTest;

import java.io.IOException;
import java.io.InputStream;

/**
 * 请求调度者，用于分配发出的是什么请求
 */
@SuppressLint("RestrictedApi")
public abstract class RequestHandler {
    public static final class Result {
        private final PicassoTest.LoadedFrom loadedFrom;
        private final Bitmap bitmap;
        private final InputStream stream;

        public Result(Bitmap bitmap, PicassoTest.LoadedFrom loadedFrom) {
            this(checkNotNull(bitmap, "bitmap == null"), null, loadedFrom);
        }

        public Result(InputStream stream, PicassoTest.LoadedFrom loadedFrom) {
            this(null, checkNotNull(stream, "stream == null"), loadedFrom);
        }

        Result(Bitmap bitmap, InputStream stream, PicassoTest.LoadedFrom loadedFrom) {
            if ((bitmap != null) == (stream != null)) {
                throw new AssertionError();
            }
            this.bitmap = bitmap;
            this.stream = stream;
            this.loadedFrom = checkNotNull(loadedFrom, "loadedFrom == null");
        }


        //两个方法只选其一

        /**
         * 加载的Bitmap 。与getStream()互斥。
         */
        public Bitmap getBitmap() {
            return bitmap;
        }

        /**
         * 图像数据流。与getBitmap()互斥。
         */
        public InputStream getStream() {
            return stream;
        }

        /**
         * @return 返回从load(Request, int)调用生成的结果Picasso.LoadedFrom 。
         */
        public PicassoTest.LoadedFrom getLoadedFrom() {
            return loadedFrom;
        }

//        /**
//         * 这个方法暂时还未搞懂
//         *
//         * @return 返回从load(Request, int)调用生成的结果 EXIF 方向。这只能由内置的 RequestHandlers 访问。
//         */
//        int getExifOrientation() {
//            return exifOrientation;
//        }
    }

    /**
     * 此RequestHandler是否可以处理具有给定Request的请求
     */
    public abstract boolean canHandleRequest(Request data);



    public abstract Result load(Request request) throws IOException;


    public static boolean requiresInSampleSize(BitmapFactory.Options options) {
        return options != null && options.inJustDecodeBounds;
    }

    public static BitmapFactory.Options createBitmapOptions(Request data) {
        final boolean justBounds = data.hasSize();
        final boolean hasConfig = data.config != null;
        BitmapFactory.Options options = null;
        if (justBounds || hasConfig) {
            options = new BitmapFactory.Options();
            options.inJustDecodeBounds = justBounds;
            if (hasConfig) {
                options.inPreferredConfig = data.config;
            }
        }
        return options;
    }

    public static void calculateInSampleSize(int reqWidth, int reqHeight, BitmapFactory.Options options,
                                             Request request) {
        calculateInSampleSize(reqWidth, reqHeight, options.outWidth, options.outHeight, options,
                request);
    }

    static void calculateInSampleSize(int reqWidth, int reqHeight, int width, int height,
                                      BitmapFactory.Options options, Request request) {
        int sampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int heightRatio;
            final int widthRatio;
            if (reqHeight == 0) {
                sampleSize = (int) Math.floor((float) width / (float) reqWidth);
            } else if (reqWidth == 0) {
                sampleSize = (int) Math.floor((float) height / (float) reqHeight);
            } else {
                heightRatio = (int) Math.floor((float) height / (float) reqHeight);
                widthRatio = (int) Math.floor((float) width / (float) reqWidth);
                sampleSize = request.centerInside
                        ? Math.max(heightRatio, widthRatio)
                        : Math.min(heightRatio, widthRatio);
            }
        }
        options.inSampleSize = sampleSize;
        options.inJustDecodeBounds = false;
    }
}
