package com.example.choosephotoapplication.model;

import android.content.Context;

import com.example.choosephotoapplication.entiy.FileImgBean;
import com.example.choosephotoapplication.util.ImgUtils;

import java.util.List;

public class PictureDisplayModel {
    public static List<FileImgBean> getPicture(Context context){

        List<FileImgBean> list  = ImgUtils.getImgList(context);
        return list;
    }
}
