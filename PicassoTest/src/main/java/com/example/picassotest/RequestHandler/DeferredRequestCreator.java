package com.example.picassotest.RequestHandler;

import android.view.ViewTreeObserver;

@Deprecated
public class DeferredRequestCreator implements ViewTreeObserver.OnPreDrawListener{
    @Override
    public boolean onPreDraw() {
        return false;
    }
}
