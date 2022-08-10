package com.example.picassotest;

public class Constants {
    public static final String ACTIVITY_SERVICE = "activity";
    public static final int FLAG_LARGE_HEAP = 1048576;
    public static final String TAG = "JM";


    // mainHandler的操作代码
    /**
     * 子线程完成任务通知主线程更新数据
     */
    static final int HUNTER_BATCH_COMPLETE = 8;

    // 线程名称
    static  final String THREAD_PREFIX = "Picasso-";
    static final String  DISPATCHER_THREAD_NAME = "Dispatcher";


    // dispatcherHandler的操作代码
    static final int REQUEST_SUBMIT = 1;
    static final int REQUEST_CANCEL = 2;
    static final int HUNTER_COMPLETE = 4;
    static final int HUNTER_DECODE_FAILED = 6;
    static final int HUNTER_DELAY_NEXT_BATCH = 7;
    static final int TAG_PAUSE = 11;
    static final int TAG_RESUME = 12;
    static final int REQUEST_BATCH_RESUME = 13;

    //Parcelable

    public static  final String PARCELABLE = "Parcelable";
    public static  final String LIST_POSITION = "Position";


}
