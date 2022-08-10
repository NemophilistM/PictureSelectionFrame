package com.example.picassotest.Action;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import com.example.picassotest.Constants;
import com.example.picassotest.PicassoDrawable;
import com.example.picassotest.PicassoTest;
import com.example.picassotest.RequestHandler.Request;

public class ImageViewAction extends Action {
    public ImageViewAction(PicassoTest picasso, Request request, ImageView target, int errorResId, String key,Object tag) {
        super(picasso, request, target, errorResId, key,tag);
    }

    @Override
    public void complete(Bitmap result, PicassoTest.LoadedFrom from) {
        if(result == null){
            Log.d(Constants.TAG, "complete: 无法获取到结果，拿到的图片为空");
            throw new AssertionError(
                    String.format("Attempted to complete action with no result!\n%s", this));
        }
        ImageView target = this.target.get();
        if(target == null){
            return;
        }
        Context context = picasso.context;
        PicassoDrawable.setBitmap(target, context, result, from);
    }

    @Override
    public void error() {
        ImageView target = this.target.get();
        if (target == null) {
            return;
        }
        if (errorResId != 0) {
            target.setImageResource(errorResId);
        }
    }
}
