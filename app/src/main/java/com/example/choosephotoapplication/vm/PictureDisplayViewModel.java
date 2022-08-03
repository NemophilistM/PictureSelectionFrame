package com.example.choosephotoapplication.vm;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.choosephotoapplication.entiy.FileImgBean;
import com.example.choosephotoapplication.model.PictureDisplayModel;

import java.util.List;

public class PictureDisplayViewModel extends ViewModel {
    private final MutableLiveData<List<FileImgBean>> _fileImg = new MutableLiveData<>();
    public final LiveData<List<FileImgBean>> fileImg = _fileImg;

    public void requestPicture(Context context){
       List<FileImgBean> list =  PictureDisplayModel.getPicture(context);
       List<FileImgBean> liveDataList = _fileImg.getValue();
       if(liveDataList==null){
           _fileImg.setValue(list);
       }else {
           liveDataList.addAll(list);
           _fileImg.setValue(liveDataList);
       }
    }

}
