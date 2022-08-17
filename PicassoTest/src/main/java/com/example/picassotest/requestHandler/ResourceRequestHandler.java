package com.example.picassotest.requestHandler;

import static android.content.ContentResolver.SCHEME_ANDROID_RESOURCE;

import android.content.Context;

import java.io.IOException;

@Deprecated
public class ResourceRequestHandler extends RequestHandler{
    private final Context context;

    ResourceRequestHandler(Context context) {
        this.context = context;
    }
    @Override
    public boolean canHandleRequest(Request data) {

        return SCHEME_ANDROID_RESOURCE.equals(data.uri.getScheme());
    }

    @Override
    public Result load(Request request) throws IOException {
        return null;
    }
}
