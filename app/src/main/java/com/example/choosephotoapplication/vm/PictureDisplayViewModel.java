package com.example.choosephotoapplication.vm;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.choosephotoapplication.entiy.FileImgBean;
import com.example.choosephotoapplication.model.PictureDisplayModel;

import java.util.ArrayList;
import java.util.List;

public class PictureDisplayViewModel extends ViewModel {
    private final MutableLiveData<ArrayList<FileImgBean>> _fileImg = new MutableLiveData<>();
    public final LiveData<ArrayList<FileImgBean>> fileImg = _fileImg;

    public void requestPicture(Context context){
       PictureDisplayModel.getPicture(context, _fileImg::postValue);

    }

}
