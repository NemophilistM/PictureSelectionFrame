package com.example.choosephotoapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button = findViewById(R.id.bt_choose);
        int count = 0;
        List<Image> imageList = new ArrayList();
        Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,null,null,null,null);
        while (cursor.moveToNext()) {
            //获取图片的名称
            String name = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
            Log.d("ImgActivity: ", "initImages: " + "imageName: " + name);

            //获取图片的路径
            byte[] data = cursor.getBlob(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            String location = new String(data, 0, data.length - 1);
            Log.d("ImgActivity: ", "initImages: " + "imageLocation: " + location);
            //根据路径获取图片
            Bitmap bm = 

            //获取图片的详细信息
            String desc = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DESCRIPTION));
            Log.d("ImgActivity", "initImages: " + "ImageDesc: " + desc);

            Image image = new Image(bm, name, location);
            imageList.add(image);

            count++;
            if(count > 3) break;
        }
        Log.d("ImgActivity: ", "initImage: " + "imageList.size: " + imageList.size());

    }
}