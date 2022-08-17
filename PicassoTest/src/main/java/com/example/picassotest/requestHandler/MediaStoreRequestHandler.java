package com.example.picassotest.requestHandler;

import static android.content.ContentResolver.SCHEME_CONTENT;
import static android.content.ContentUris.parseId;
import static android.provider.MediaStore.Images.Thumbnails.FULL_SCREEN_KIND;
import static android.provider.MediaStore.Images.Thumbnails.MICRO_KIND;
import static android.provider.MediaStore.Images.Thumbnails.MINI_KIND;

import static com.example.picassotest.PicassoTest.LoadedFrom.DISK;
import static com.example.picassotest.requestHandler.MediaStoreRequestHandler.PicassoKind.FULL;
import static com.example.picassotest.requestHandler.MediaStoreRequestHandler.PicassoKind.MICRO;
import static com.example.picassotest.requestHandler.MediaStoreRequestHandler.PicassoKind.MINI;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.IOException;

public class MediaStoreRequestHandler extends ContentStreamRequestHandler {
    public MediaStoreRequestHandler(Context context) {
        super(context);
    }

    @Override
    public boolean canHandleRequest(Request data) {
        final Uri uri = data.uri;
        return (SCHEME_CONTENT.equals(uri.getScheme())
                && MediaStore.AUTHORITY.equals(uri.getAuthority()));
    }

    @Override
    public Result load(Request request) throws IOException {
        ContentResolver contentResolver = context.getContentResolver();
        if (request.hasSize()) {
            PicassoKind picassoKind = getPicassoKind(request.targetWidth, request.targetHeight);

            long id = parseId(request.uri);

            BitmapFactory.Options options = createBitmapOptions(request);
            options.inJustDecodeBounds = true;

            calculateInSampleSize(request.targetWidth, request.targetHeight, picassoKind.width,
                    picassoKind.height, options, request);

            Bitmap bitmap;

            bitmap = MediaStore.Images.Thumbnails.getThumbnail(contentResolver, id, picassoKind.androidKind, options);

            if (bitmap != null) {
                return new Result(bitmap, null, DISK);
            }
        }
        return new Result(null, getInputStream(request), DISK);
    }


    static PicassoKind getPicassoKind(int targetWidth, int targetHeight) {
        if (targetWidth <= MICRO.width && targetHeight <= MICRO.height) {
            return MICRO;
        } else if (targetWidth <= MINI.width && targetHeight <= MINI.height) {
            return MINI;
        }
        return FULL;

    }

    enum PicassoKind {
        MICRO(MICRO_KIND, 96, 96),
        MINI(MINI_KIND, 512, 384),
        FULL(FULL_SCREEN_KIND, -1, -1);

        final int androidKind;
        final int width;
        final int height;

        PicassoKind(int androidKind, int width, int height) {
            this.androidKind = androidKind;
            this.width = width;
            this.height = height;
        }
    }
}
