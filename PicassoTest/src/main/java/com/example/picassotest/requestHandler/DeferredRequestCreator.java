package com.example.picassotest.requestHandler;

import android.view.ViewTreeObserver;

@Deprecated
public class DeferredRequestCreator implements ViewTreeObserver.OnPreDrawListener{
    @Override
    public boolean onPreDraw() {
        return false;
    }
}
