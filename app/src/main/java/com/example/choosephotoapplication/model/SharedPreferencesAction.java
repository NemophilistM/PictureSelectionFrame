package com.example.choosephotoapplication.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.example.choosephotoapplication.ViewConstants;
import com.example.choosephotoapplication.util.MyApplication;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class SharedPreferencesAction {
    public static void setSharedPreferencesBitmap(Context context,Bitmap bitmap, String md5ImagePath) {
        android.content.SharedPreferences sharedPreferences = context.getSharedPreferences(ViewConstants.REMEMBER_IMAGE_FILE, Context.MODE_PRIVATE);
        android.content.SharedPreferences.Editor edit = sharedPreferences.edit();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        String heading = new String(Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT));
        edit.putString(md5ImagePath, heading);
        edit.apply();
    }

    public static Bitmap getSharedPreferencesRememberImage(Context context,String uri) {
        android.content.SharedPreferences sharedPreferences = context.getSharedPreferences(ViewConstants.REMEMBER_IMAGE_FILE, Context.MODE_PRIVATE);
        String icon = sharedPreferences.getString(uri, null);
        if (icon != null) {
            byte[] byteArray = Base64.decode(icon, Base64.DEFAULT);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray);
            return BitmapFactory.decodeStream(byteArrayInputStream);
        }
        return null;
    }
}
