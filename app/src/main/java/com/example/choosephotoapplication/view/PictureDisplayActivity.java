package com.example.choosephotoapplication.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.HandlerThread;
import android.os.Parcelable;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.example.choosephotoapplication.R;
import com.example.choosephotoapplication.ViewConstants;
import com.example.choosephotoapplication.adapter.PictureDisplayAdapter;
import com.example.choosephotoapplication.adapter.PictureDisplayByPicassoTestAdapter;
import com.example.choosephotoapplication.databinding.ActivityPictureDisplayBinding;
import com.example.choosephotoapplication.entiy.FileImgBean;
import com.example.choosephotoapplication.util.ThreadPoolUtil;
import com.example.choosephotoapplication.vm.PictureDisplayViewModel;
import com.example.picassotest.Constants;

import java.util.ArrayList;
import java.util.List;

public class PictureDisplayActivity extends AppCompatActivity {

    private ActivityPictureDisplayBinding binding;
    private PictureDisplayByPicassoTestAdapter adapter;
    private PictureDisplayViewModel viewModel;
    private List<FileImgBean> list;
    private int version = 0;
    private boolean isFirstInsert = true;
    private List<FileImgBean> pastList = new ArrayList<>();
    /**
     * 用于执行加载图片任务的内存
     */
    public static HandlerThread handlerThread = new HandlerThread(ViewConstants.DOWNLOAD_PICTURE_HANDLER_THREAD);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityPictureDisplayBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 初始化数据库
        ThreadPoolUtil.buildThreadPool();

        // vm层完成初始化
        viewModel = new ViewModelProvider(this).get(PictureDisplayViewModel.class);

        //修改状态栏
        Window window = this.getWindow();
        window.setStatusBarColor(Color.GRAY);

        //标题栏初始化
        initToolBar();

        // 开启线程
        handlerThread.start();

        // 先将rv隐藏
        binding.rvPictureDisplay.setVisibility(View.GONE);

        viewModel.requestPicture(this);

        RecyclerView recyclerView = binding.rvPictureDisplay;



        // 对数据更新的观察
        viewModel.fileImg.observe(this,pictureList->{
            binding.rvPictureDisplay.setVisibility(View.VISIBLE);
            binding.pbPictureDisplay.setVisibility(View.GONE);
            if(pictureList.size() == 0){
                Toast.makeText(this,"暂无相片",Toast.LENGTH_SHORT).show();
                binding.tvTotalSize.setText(String.valueOf(pictureList.size()));
            }else {
                binding.tvTotalSize.setText(String.valueOf(pictureList.size()));
                adapter = new PictureDisplayByPicassoTestAdapter(this,pictureList,recyclerView,position -> {
                    Intent intent = new Intent(this,EachPictureShowActivity.class);
//                    intent.putExtra(Constants.PARCELABLE, (Parcelable) pictureList);
                    intent.putParcelableArrayListExtra(Constants.PARCELABLE, pictureList);
                    intent.putExtra(Constants.LIST_POSITION, position);
                    startActivity(intent);
                });
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
            }



        });
    }

    private void initToolBar() {
        Toolbar toolbar = findViewById(R.id.tb_main);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_hamburger_button);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handlerThread.quitSafely();
    }
}