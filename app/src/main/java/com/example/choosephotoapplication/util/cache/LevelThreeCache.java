package com.example.choosephotoapplication.util.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.HandlerThread;

/**
 * 实现图片的三级缓存
 *
 * @author 86159
 */
public class LevelThreeCache {

    private final RestartCacheUtils mRestartCacheUtils;
    private final LocalCacheUtils mLocalCacheUtils;
    private final MemoryCacheUtils mMemoryCacheUtils;

    public LevelThreeCache() {
        mMemoryCacheUtils = new MemoryCacheUtils();
        mLocalCacheUtils = new LocalCacheUtils();
        mRestartCacheUtils = new RestartCacheUtils(mLocalCacheUtils, mMemoryCacheUtils);
    }

    Bitmap bitmap = null;

    public Bitmap getBitmap(Uri uri,Context context,RestartCacheUtils.CallBack callBack) {
        bitmap = mMemoryCacheUtils.getBitmapFromMemory(uri);
        if (bitmap != null) {
            return bitmap;
        }
        //本地缓存
        bitmap = mLocalCacheUtils.getBitmapFromLocal(uri,context);
        if (bitmap != null) {
            // 这里保存是为了不用重新从网络下载
            mMemoryCacheUtils.setBitmapToMemory(uri, bitmap);
            return bitmap;
        }
        //网络缓存
        if(callBack!=null){
            mRestartCacheUtils.getBitmapRestart(uri,context, callBack);

        }
        return null;

    }

}
