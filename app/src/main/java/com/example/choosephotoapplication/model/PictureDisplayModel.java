package com.example.choosephotoapplication.model;

import android.content.Context;

import com.example.choosephotoapplication.entiy.FileImgBean;
import com.example.choosephotoapplication.util.ThreadPoolUtil;
import com.example.choosephotoapplication.util.ImgUtils;

import java.util.ArrayList;
import java.util.List;

public class PictureDisplayModel {
    public static void getPicture(Context context,CallBack callBack){

        ThreadPoolUtil.getThreadPoolExecutor().execute(()->{
            ArrayList<FileImgBean> list  = ImgUtils.getImgList(context);
            callBack.callBackWhetherTure(list);
        });

    }

    /**
     * 回调给vm层设置数据
     */
    public interface CallBack {
        void callBackWhetherTure(ArrayList<FileImgBean> list);
    }
}
