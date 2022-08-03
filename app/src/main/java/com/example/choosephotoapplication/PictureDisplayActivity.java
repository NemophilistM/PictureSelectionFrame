package com.example.choosephotoapplication;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Window;

import com.example.choosephotoapplication.adapter.PictureDisplayAdapter;
import com.example.choosephotoapplication.databinding.ActivityPictureDisplayBinding;
import com.example.choosephotoapplication.vm.PictureDisplayViewModel;

public class PictureDisplayActivity extends AppCompatActivity {

    private ActivityPictureDisplayBinding binding;
    private PictureDisplayAdapter adapter;
    private PictureDisplayViewModel viewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityPictureDisplayBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // vm层完成初始化
        viewModel = new ViewModelProvider(this).get(PictureDisplayViewModel.class);

        //修改状态栏
        Window window = this.getWindow();
        window.setStatusBarColor(Color.GRAY);

        //标题栏初始化
        initToolBar();

        viewModel.requestPicture(this);

        RecyclerView recyclerView = binding.rvPictureDisplay;
        adapter = new PictureDisplayAdapter(new PictureDisplayAdapter.requestAndEnter() {
            @Override
            public void load() {

            }

            @Override
            public void enter() {

            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        // 对数据更新的观察
        viewModel.fileImg.observe(this,pictureList->{
            adapter.notify(pictureList);
        });
    }

    private void initToolBar() {
        Toolbar toolbar = findViewById(R.id.tb_main);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_enter);
        }
    }
}