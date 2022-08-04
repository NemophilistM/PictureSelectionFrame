package com.example.choosephotoapplication.util.cache;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.LruCache;

/**
 * @author 86159
 */
public class MemoryCacheUtils {

    // 用这个LruCache是为了防止内存不够，防止出现oom

    private final LruCache<String, Bitmap> mMemoryCache;

    public MemoryCacheUtils() {
        long maxMemory = Runtime.getRuntime().maxMemory() / 8;
        //设置峰值，防止内存申请过多了
        mMemoryCache = new LruCache<String, Bitmap>((int) maxMemory) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };

    }

    public Bitmap getBitmapFromMemory(Uri uri) {
        return mMemoryCache.get(String.valueOf(uri));

    }


    public void setBitmapToMemory(Uri uri, Bitmap bitmap) {
        mMemoryCache.put(String.valueOf(uri), bitmap);
    }

}
