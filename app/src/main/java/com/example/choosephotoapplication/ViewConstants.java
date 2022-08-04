package com.example.choosephotoapplication;

public class ViewConstants {

    // TAG
    public static final String TAG  = "JM";


    // 对external。db的操作常量
    public static final String RELATIVE_PATH  = "${Environment.DIRECTORY_IMAGE}/image";
    public static final String DISPLAY_NAME  = "image.jpg";
    public static final String TITLE  = "image";
    public static final String MIME_TYPE  = "image/jpg";

    // 数字限制
    public static final int PICTURE_UPDATE_LIMIT  = 8;
    public static final int EACH_PAGE_PICTURE_UPDATE_LIMIT  = 32;

    // handlerThread线程名称
    public static final String DOWNLOAD_PICTURE_HANDLER_THREAD  = "downloadPictureHandlerThread";

    // handler内部区分标识
    public static final int DOWNLOAD_HANDLER  = 1;

    // sharedPreference 标识
    public static final String REMEMBER_IMAGE_FILE = "RememberImageFile";




}
