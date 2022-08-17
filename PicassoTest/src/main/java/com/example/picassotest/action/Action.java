package com.example.picassotest.action;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.example.picassotest.PicassoTest;
import com.example.picassotest.requestHandler.Request;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

public abstract class Action {
    static class RequestWeakReference<M> extends WeakReference<M> {
        final Action action;

        public RequestWeakReference(Action action, M referent, ReferenceQueue<? super M> q) {
            super(referent, q);
            this.action = action;
        }
    }

    public final PicassoTest picasso;
    public final Request request;
    public final WeakReference<ImageView> target;
    public final int errorResId;
    public final String key;
    final Object tag;

    boolean cancelled;


    public Action(PicassoTest picasso, Request request,
                  ImageView target, int errorResId, String key,Object tag) {
        this.picasso = picasso;
        this.request = request;
        this.target = target == null ? null : new RequestWeakReference<>(this, target, picasso.referenceQueue);
        this.errorResId = errorResId;
        this.key = key;
        this.tag = (tag != null ? tag : this);
    }

    public abstract void complete(Bitmap result, PicassoTest.LoadedFrom from);

    public abstract void error();

    public void cancel() {
        cancelled = true;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public Request getRequest() {
        return request;
    }

    public ImageView getTarget() {
        return target == null ? null : target.get();
    }

    public PicassoTest.Priority getPriority() {
        return request.priority;
    }

    public String getKey() {
        return key;
    }

    public PicassoTest getPicasso() {
        return picasso;
    }

    public Object getTag() {
        return tag;
    }

}
