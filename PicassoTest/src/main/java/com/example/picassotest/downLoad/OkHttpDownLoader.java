package com.example.picassotest.downLoad;

import android.net.Uri;

import com.example.picassotest.Utils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.CacheControl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * 下载器，从网络获取图片
 */

public class OkHttpDownLoader implements DownLoader {

    private static OkHttpClient.Builder defaultOkHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(Utils.DEFAULT_CONNECT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        builder.readTimeout(Utils.DEFAULT_READ_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        builder.writeTimeout(Utils.DEFAULT_WRITE_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        return builder;
    }

    private static OkHttpClient.Builder builder;



//    public OkHttpDownLoader() {
//        client = getOkHttpClient();
//    }
//
//    public static  OkHttpClient getOkHttpClient (){
//        if(client==null){
//            OkHttpClient.Builder builder = new OkHttpClient.Builder();
//            client =  builder.build();
//        }
//        return client;
//
//    }

//
//    public OkHttpDownloader(Context context) {
//        this(Utils.createDefaultCacheDir(context));
//    }
//    public OkHttpDownloader(final File cacheDir) {
//        this(cacheDir, Utils.calculateDiskCacheSize(cacheDir));
//    }
//
//    public OkHttpDownloader(OkHttpClient.Builder builder) {
//        this.builder = builder;
//    }
//
//    public OkHttpDownloader(File cacheDir, long maxSize) {
//        this(defaultOkHttpClient());
//
//        builder.build().cache();
//        this.builder.cache(new Cache(cacheDir,maxSize));
//
//    }
    @Override
    public ResponseLocal load(Uri uri) throws IOException {
        CacheControl cacheControl = null;
        CacheControl.Builder builder = new CacheControl.Builder();
        cacheControl = builder.build();
        Request.Builder builder1 = new Request.Builder().url(uri.toString());
        if (cacheControl != null) {
            builder1.cacheControl(cacheControl);
        }
        Response response = this.builder.build().newCall(builder1.build()).execute();

        boolean fromCache = response.cacheResponse() != null;

        ResponseBody responseBody = response.body();
        return new ResponseLocal(responseBody.byteStream(), fromCache, responseBody.contentLength());
    }

    @Override
    public void shutdown() {

    }
}
