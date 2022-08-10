package com.example.picassotest;

import android.graphics.Bitmap;

public interface Cache {
    /**
     * 检索指定的图像并返回
     *
     * @param key 关键词
     * @return 返回图像
     */
    Bitmap get(String  key);

    /**
     * 设置图像
     *
     * @param key 关键词
     * @param bitmap 设置的图像
     */
    void set(String key, Bitmap bitmap);

    /**
     * 返回缓存的当前大小
     *
     * @return 返回当前大小
     */
    int size();

    /**
     * @return 返回缓存可以容纳的最大字节数
     */
    int maxSize();

    /**
     * 清理缓存
     */
    void clear();

    /**
     * 清除指定的缓存
     *
     * @param keyPrefix 关键词
     */
    void clearKeyUri(String keyPrefix);

    Cache NONE = new Cache() {
        @Override
        public Bitmap get(String key) {
            return null;
        }

        @Override
        public void set(String key, Bitmap bitmap) {
            // ignore
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public int maxSize() {
            return 0;
        }

        @Override
        public void clear() {

        }

        @Override
        public void clearKeyUri(String keyPrefix) {

        }
    };
}
