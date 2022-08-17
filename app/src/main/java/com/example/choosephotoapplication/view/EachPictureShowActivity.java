package com.example.choosephotoapplication.view;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Window;

import com.example.choosephotoapplication.R;
import com.example.choosephotoapplication.adapter.EachPictureShowPagerAdapter;
import com.example.choosephotoapplication.databinding.ActivityEachPictureShowBinding;
import com.example.choosephotoapplication.databinding.ActivityPictureDisplayBinding;
import com.example.choosephotoapplication.entiy.FileImgBean;
import com.example.picassotest.Constants;
import com.example.picassotest.PicassoTest;

import java.util.List;

public class EachPictureShowActivity extends AppCompatActivity {

    private int position;
    private List<FileImgBean> fileImgBeans;
    private ActivityEachPictureShowBinding binding;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEachPictureShowBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent intent = getIntent();
        if (intent != null) {
            position = intent.getIntExtra(Constants.LIST_POSITION, 1);
//            fileImgBeans = intent.getParcelableExtra(Constants.PARCELABLE);
            fileImgBeans = intent.getParcelableArrayListExtra(Constants.PARCELABLE);

        }

        //修改状态栏
        Window window = this.getWindow();
        window.setStatusBarColor(Color.GRAY);

        //标题栏初始化
        initToolBar();

        binding.tvEachPosition.setText(position+"/"+fileImgBeans.size());

        EachPictureShowPagerAdapter adapter = new EachPictureShowPagerAdapter(this,fileImgBeans);
        binding.vpDisplayPicture.setCurrentItem(position-1);
        binding.vpDisplayPicture.setAdapter(adapter);


        // 实现vp的滑动监听
        initListener();


    }

    private void initListener() {
        binding.vpDisplayPicture.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                binding.tvEachPosition.setText(position+1+"/"+fileImgBeans.size());
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initToolBar() {
        Toolbar toolbar = findViewById(R.id.tb_each_position);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_return);
        }
    }
}