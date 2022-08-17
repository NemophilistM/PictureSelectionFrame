package com.example.picassotest.requestHandler;

import com.example.picassotest.downLoad.OkHttpDownLoader;

import java.io.IOException;

public class NetworkRequestHandler extends RequestHandler{

    private static final String SCHEME_HTTP = "http";
    private static final String SCHEME_HTTPS = "https";

    private final OkHttpDownLoader downloader;

    public NetworkRequestHandler(OkHttpDownLoader downloader) {
        this.downloader = downloader;
    }

    @Override
    public boolean canHandleRequest(Request data) {
        String scheme = data.uri.getScheme();
        return (SCHEME_HTTP.equals(scheme) || SCHEME_HTTPS.equals(scheme));
    }

    @Override
    public Result load(Request request) throws IOException {
//        Response response  = downloader.load(request.uri);
//
//        if(response == null ){
//            return  null;
//        }
//
//        Bitmap bitmap = response.
        return null;
    }
}
