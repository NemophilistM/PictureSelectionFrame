package com.example.picassotest.downLoad;

import android.graphics.Bitmap;
import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;

public interface DownLoader {
    ResponseLocal load(Uri uri) throws IOException;
    void shutdown();

    class ResponseLocal {
        final InputStream stream;
        final Bitmap bitmap;
        final boolean cached;
        final long contentLength;




        public ResponseLocal(InputStream stream, boolean loadedFromCache, long contentLength) {
            if (stream == null) {
                throw new IllegalArgumentException("Stream may not be null.");
            }
            this.stream = stream;
            this.bitmap = null;
            this.cached = loadedFromCache;
            this.contentLength = contentLength;
        }


        public InputStream getInputStream() {
            return stream;
        }


        /** Content length of the response. Only valid when used with {@link #getInputStream()}. */
        public long getContentLength() {
            return contentLength;
        }
    }
}
