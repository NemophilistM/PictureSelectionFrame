package com.example.picassotest;

import static com.example.picassotest.Utils.KEY_SEPARATOR;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class LruCache implements Cache{
    final LinkedHashMap<String, Bitmap> map;
    private final int maxSize;
    /**
     * 负责监听重复调用调取到的次数
     */
    private int hitCount;
    /**
     * 负责监听重复调用没有调用到的次数
     */
    private int missCount;

    /**
     * 存储加入位图后把每张位图加起来所需的最小字节数
     */
    private int size;

    public LruCache(Context context) {
        this(Utils.calculateMemoryCacheSize(context));
    }

    public LruCache(int maxSize){
        if(maxSize <= 0){
            throw new IllegalArgumentException("Max size must be positive.");
        }
        this.maxSize =maxSize;
        this.map = new LinkedHashMap<String ,Bitmap>(0,0.75f,true);
    }

    @Override
    public Bitmap get(String key) {
        if (key == null) {
            throw new NullPointerException("key == null");
        }
        Bitmap mapValue;
        synchronized (this) {
            mapValue = map.get(key);
            if (mapValue != null) {
                Log.d(Constants.TAG, "LruCache.get: 从内存中获取图片成功并返回 ");
                return mapValue;
            }
            Log.d(Constants.TAG, "LruCache.get: 从内存中获取图片失败并返回空 ");

        }
        return null;
    }

    @Override
    public void set(String key, Bitmap bitmap) {
        if (key == null || bitmap == null) {
            throw new NullPointerException("key == null || bitmap == null");
        }
        Bitmap previous;
        synchronized (this) {
            size += Utils.getBitmapBytes(bitmap);
            previous = map.put(key, bitmap);
            if (previous != null) {
                size -= Utils.getBitmapBytes(previous);
            }
        }

        trimToSize(maxSize);
    }

    /**
     * 测量大小，当存储的东西大于maxSize的时候进行清理
     *
     * @param maxSize 内存的1/7大小
     */
    private void trimToSize(int maxSize) {
        while (true) {
            String key;
            Bitmap value;
            synchronized (this) {
                if (size < 0 || (map.isEmpty() && size != 0)) {
                    throw new IllegalStateException(
                            getClass().getName() + ".sizeOf() is reporting inconsistent results!");
                }
                if (size <= maxSize || map.isEmpty()) {
                    break;
                }
                // 获取map里面每个键值对的set集合
                // 其中iterator是迭代器，负责从键值对集合中取值
                Map.Entry<String, Bitmap> toEvict = map.entrySet().iterator().next();
                key = toEvict.getKey();
                value = toEvict.getValue();
                map.remove(key);
                size -= Utils.getBitmapBytes(value);
            }

        }
    }

    /**
     * 该方法负责清除缓存
     */
    public final void evictAll() {
        trimToSize(-1); // -1 will evict 0-sized elements
    }
    @Override
    public final synchronized int size() {
        return size;
    }

    @Override
    public final synchronized int maxSize() {
        return maxSize;
    }

    @Override
    public final synchronized void clear() {
        evictAll();
    }


    // 这个方法的重写还没研究太懂
    @Override
    public final synchronized void clearKeyUri(String keyPrefix) {
        boolean sizeChanged = false;
        int uriLength = keyPrefix.length();
        for (Iterator<Map.Entry<String, Bitmap>> i = map.entrySet().iterator(); i.hasNext();) {
            Map.Entry<String, Bitmap> entry = i.next();
            String key = entry.getKey();
            Bitmap value = entry.getValue();
            int newlineIndex = key.indexOf(KEY_SEPARATOR);
            if (newlineIndex == uriLength && key.substring(0, newlineIndex).equals(keyPrefix)) {
                i.remove();
                size -= Utils.getBitmapBytes(value);
                sizeChanged = true;
            }
        }
        if (sizeChanged) {
            trimToSize(maxSize);
        }
    }
}
