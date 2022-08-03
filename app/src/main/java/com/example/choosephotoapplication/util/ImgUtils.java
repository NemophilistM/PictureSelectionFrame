package com.example.choosephotoapplication.util;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;

import com.example.choosephotoapplication.ViewConstants;
import com.example.choosephotoapplication.entiy.FileImgBean;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class ImgUtils {


    @SuppressLint("Range")
    public static ArrayList<FileImgBean> getImgList(Context context) {
        ArrayList<FileImgBean> ImagesList = new ArrayList<>();
        String[] thumbColumns = {MediaStore.Images.Media.DATA,
                MediaStore.Images.Media._ID};
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        // 视频其他信息的查询条件
        String[] mediaColumns = {MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA, MediaStore.Images.Media.TITLE,
                MediaStore.Images.Media.MIME_TYPE,
                MediaStore.Images.Media.SIZE};
        Cursor cursor = context.getContentResolver().query(uri,
                mediaColumns, null, null, null);

        if (cursor == null) {
            return ImagesList;
        }
        if (cursor.moveToFirst()) {

            do {
                FileImgBean info = new FileImgBean();
                info.setData(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)));
                info.setMimeType(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE)));
                info.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.TITLE)));
                long testId =cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media._ID)) ;
                info.setId(testId);
                info.setUri(ContentUris.withAppendedId(uri,testId));
                int columnIndexOrThrow = cursor.getColumnIndex(MediaStore.Images.Media.SIZE);
                int anInt = cursor.getInt(columnIndexOrThrow);
                info.setSize(anInt + "");
                ImagesList.add(info);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return ImagesList;
    }


    }
