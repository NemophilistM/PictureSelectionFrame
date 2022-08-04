package com.example.choosephotoapplication.util.cache;

import static com.example.choosephotoapplication.PictureDisplayActivity.handlerThread;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.example.choosephotoapplication.MyApplication;
import com.example.choosephotoapplication.PictureDisplayActivity;
import com.example.choosephotoapplication.ViewConstants;
import com.example.choosephotoapplication.adapter.PictureDisplayAdapter;

import java.io.FileNotFoundException;

/**
 * 网络缓存
 *
 * @author 86159
 */
public class RestartCacheUtils {
    private final LocalCacheUtils mLocalCacheUtils;
    private final MemoryCacheUtils mMemoryCacheUtils;


    public RestartCacheUtils(LocalCacheUtils mLocalCacheUtils, MemoryCacheUtils mMemoryCacheUtils) {
        this.mLocalCacheUtils = mLocalCacheUtils;
        this.mMemoryCacheUtils = mMemoryCacheUtils;

    }

    public void getBitmapRestart(Uri uri,Context context,CallBack callBack) {
       BitmapWorkerTask bitmapWorkerTask = new BitmapWorkerTask();
       bitmapWorkerTask.downLoad(handlerThread,context,uri, callBack);
    }

    /**
     * 内部类，负责图片的异步加载
     */
    private class BitmapWorkerTask {
        /**
         * 获取图片的uri
         */
        public void downLoad(HandlerThread handlerThread,Context context,Uri uri,CallBack callBack){
            if(PictureDisplayAdapter.handler == null){
                PictureDisplayAdapter.handler = new Handler(handlerThread.getLooper()){
                    @Override
                    public void handleMessage(@NonNull Message msg) {
                        super.handleMessage(msg);
                        switch (msg.what){
                            case ViewConstants.DOWNLOAD_HANDLER:
                                Uri uri = (Uri) msg.obj;
                                Bitmap bitmap = null;
                                try {
                                    bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri));
//                                    mLocalCacheUtils.setBitmapToLocal(uri, bitmap);
                                    mMemoryCacheUtils.setBitmapToMemory(uri, bitmap);
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                                callBack.returnBitmap(bitmap,uri);
                                break;

                        }
                    }
                };
            }

            Message message = Message.obtain();
            message.what = ViewConstants.DOWNLOAD_HANDLER;
            message.obj = uri;
            PictureDisplayAdapter.handler.sendMessage(message);
        }
    }

    /**
     * 回调方法，负责返回bitmap
     */
    public interface CallBack {
        void returnBitmap(Bitmap bitmap,Uri uri);
    }


}
