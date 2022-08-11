package com.example.choosephotoapplication.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.choosephotoapplication.entiy.FileImgBean;
import com.example.choosephotoapplication.widget.SlideImageView;
import com.example.choosephotoapplication.widget.SquareView;
import com.example.picassotest.PicassoTest;

import java.util.List;

public class EachPictureShowPagerAdapter extends PagerAdapter {
    private final Context context;
    private final List<FileImgBean> list;

    public EachPictureShowPagerAdapter(Context context, List<FileImgBean> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        if (list.size() != 0) {
            return list.size();
        }
        return 0;
    }


    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        Uri uri = (Uri) ((ImageView)object).getTag();
        if(uri != null){
            for (int i = 0; i < list.size(); i++) {
                if(uri.equals(list.get(i).getUri())){
                    return i;
                }
            }
        }
        return super.getItemPosition(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        Uri uri = list.get(position).getUri();
        SlideImageView imageView = new  SlideImageView(context);
        PicassoTest.with(context).load(uri).into(imageView);
        imageView.setTag(uri);
        container.addView(imageView);
        return imageView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        int count = container.getChildCount();
        for (int i = 0; i < count; i++) {
            View childView = container.getChildAt(i);
            if(childView == object){
                container.removeView(childView);
                break;
            }
        }
    }
}
