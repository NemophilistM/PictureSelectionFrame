package com.example.picassotest.requestHandler;

import android.graphics.Bitmap;

public interface Transformation {
    /**
     * 将源位图转换为新位图。如果您创建一个新的位图实例，您必须在source上调用Bitmap.recycle() 。
     * 如果不需要转换，您可以退回原件。
     */
    Bitmap transform(Bitmap source);

    /**
     * 返回转换的唯一键，用于缓存目的。如果转换有参数（例如大小、比例因子等），那么这些应该是关键的一部分
     */
    String key();
}
