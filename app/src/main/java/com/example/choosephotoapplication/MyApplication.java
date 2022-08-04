package com.example.choosephotoapplication;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

/**
 * 用于获取全局context
 *
 * @author 86159
 */
public class MyApplication extends Application {
    @SuppressLint("StaticFieldLeak")
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }
}
