package com.example.picassotest;

import static com.example.picassotest.PicassoTest.LoadedFrom.MEMORY;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.widget.ImageView;

public class PicassoDrawable extends BitmapDrawable {
    private final PicassoTest.LoadedFrom loadedFrom;

    long startTimeMillis;

    Drawable placeholder;

    public PicassoDrawable(Context context, Bitmap bitmap, Drawable placeholder, PicassoTest.LoadedFrom loadedFrom) {
    super(context.getResources(),bitmap);
        this.loadedFrom = loadedFrom;
        boolean fade = loadedFrom != MEMORY;
        if(fade){
            this.placeholder = placeholder;
            startTimeMillis = SystemClock.uptimeMillis();
        }
    }

    public static void setPlaceholder(ImageView target, Drawable placeholderDrawable) {
        target.setImageDrawable(placeholderDrawable);
        // 以下是用来判断该任务是否是动画的，如果是则开始，但一般不是
//        if (target.getDrawable() instanceof AnimationDrawable) {
//            ((AnimationDrawable) target.getDrawable()).start();
//        }
    }
    public static void setBitmap(ImageView target, Context context, Bitmap bitmap,
                          PicassoTest.LoadedFrom loadedFrom) {
        Drawable placeholder = target.getDrawable();

        //暂停动画，但是，欸嘿，我不会动画
//        if (placeholder instanceof AnimationDrawable) {
//            ((AnimationDrawable) placeholder).stop();
//        }
        PicassoDrawable drawable =
                new PicassoDrawable(context, bitmap, placeholder, loadedFrom);
        target.setImageDrawable(drawable);
    }

}
