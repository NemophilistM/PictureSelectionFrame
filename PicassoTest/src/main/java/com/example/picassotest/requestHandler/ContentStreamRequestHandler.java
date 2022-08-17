package com.example.picassotest.requestHandler;

import static android.content.ContentResolver.SCHEME_CONTENT;

import static com.example.picassotest.PicassoTest.LoadedFrom.DISK;

import android.content.ContentResolver;
import android.content.Context;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class ContentStreamRequestHandler extends RequestHandler{

    final Context context;

    ContentStreamRequestHandler(Context context) {
        this.context = context;
    }

    @Override
    public boolean canHandleRequest(Request data) {
        return SCHEME_CONTENT.equals(data.uri.getScheme());
    }

    @Override
    public Result load(Request request) throws IOException {
        return new Result(getInputStream(request), DISK);
    }


    InputStream getInputStream(Request request) throws FileNotFoundException {
        ContentResolver contentResolver = context.getContentResolver();
        return contentResolver.openInputStream(request.uri);
    }
}
