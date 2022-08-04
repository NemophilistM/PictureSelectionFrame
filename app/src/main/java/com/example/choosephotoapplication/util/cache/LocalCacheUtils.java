package com.example.choosephotoapplication.util.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import com.example.choosephotoapplication.model.SharedPreferencesAction;
/**
 * 本地缓存
 *
 * @author 86159
 */
public class LocalCacheUtils {
    public Bitmap getBitmapFromLocal(Uri uri, Context context) {
        return SharedPreferencesAction.getSharedPreferencesRememberImage(context,String.valueOf(uri));
    }


    public void setBitmapToLocal(Uri uri,Context context , Bitmap bitmap) {
        SharedPreferencesAction.setSharedPreferencesBitmap(context,bitmap, String.valueOf(uri));
    }


}
