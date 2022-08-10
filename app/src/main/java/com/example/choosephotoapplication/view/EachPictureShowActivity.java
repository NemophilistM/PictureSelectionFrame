package com.example.choosephotoapplication.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.choosephotoapplication.R;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEachPictureShowBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent intent = getIntent();
        if (intent != null) {
            position = intent.getIntExtra(Constants.LIST_POSITION, 0);
//            fileImgBeans = intent.getParcelableExtra(Constants.PARCELABLE);
            fileImgBeans = intent.getParcelableArrayListExtra(Constants.PARCELABLE);

        }
        PicassoTest.with(this).load(fileImgBeans.get(position).getUri()).into(binding.ivDisplayPicture);
    }
}