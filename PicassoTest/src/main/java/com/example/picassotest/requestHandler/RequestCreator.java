package com.example.picassotest.requestHandler;


import static com.example.picassotest.PicassoDrawable.setBitmap;
import static com.example.picassotest.PicassoDrawable.setPlaceholder;
import static com.example.picassotest.PicassoTest.LoadedFrom.MEMORY;
import static com.example.picassotest.Utils.checkMain;
import static com.example.picassotest.Utils.createKey;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import com.example.picassotest.action.Action;
import com.example.picassotest.action.ImageViewAction;
import com.example.picassotest.Constants;
import com.example.picassotest.PicassoTest;

import java.util.concurrent.atomic.AtomicInteger;

public class RequestCreator {
    /**
     * picasso的实例
     */
    private final PicassoTest picassoTest;
    /**
     * 数据请求构建
     */
    private final Request.Builder data;
    /**
     * 占位图片资源地址
     */
    private int placeholderResId;

    /**
     * 加载失败占位图
     */
    private int errorResId;

    /**
     * 用于判断是否延期
     */
    private boolean deferred;

    /**
     * 这个参数会在调用一次getAndIncrement方法后自行加一
     */
    private static final AtomicInteger nextId = new AtomicInteger();

    /**
     * 判断是否有占位图
     */
    private boolean setPlaceholder = true;

    /**
     * 用于辨识的标签
     */
    private Object tag;

    public RequestCreator(PicassoTest picassoTest, Uri uri) {
        if (picassoTest.shutdown) {
            throw new IllegalStateException(
                    "Picasso instance already shut down. Cannot submit new requests.");
        }
        this.picassoTest = picassoTest;
        this.data = new Request.Builder(uri);
    }

    /**
     * 为请求添加一个标签，方便在相同逻辑下进行管理，比如我后面要进行的暂停和开启活动
     */
    public RequestCreator tag(Object tag) {
        if (tag == null) {
            throw new IllegalArgumentException("Tag invalid.");
        }
        if (this.tag != null) {
            throw new IllegalStateException("Tag already set.");
        }
        this.tag = tag;
        return this;
    }

    /**
     * 清空占位资源
     */
    public RequestCreator noPlaceholder() {
        if (placeholderResId != 0) {
            throw new IllegalStateException("Placeholder resource already set.");
        }
        setPlaceholder = false;
        Log.d(Constants.TAG, "RequestCreator: 改变setPlaceholder参数，清空占位资源");
        return this;
    }

    /**
     * 设置占位资源
     */
    public RequestCreator placeholder(int placeholderResId) {
        if (!setPlaceholder) {
            throw new IllegalStateException("Already explicitly declared as no placeholder.");
        }
        if (placeholderResId == 0) {
            throw new IllegalArgumentException("Placeholder image resource invalid.");
        }
        this.placeholderResId = placeholderResId;
        return this;
    }

    /**
     * 设置加载失败的占位图片
     */
    public RequestCreator error(int errorResId) {
        if (errorResId == 0) {
            throw new IllegalArgumentException("Error image resource invalid.");
        }
        this.errorResId = errorResId;
        Log.d(Constants.TAG, "RequestCreator: 填充errorResId参数，设置加载失败的占位资源");
        return this;
    }

    /**
     * 尝试调整图像大小以完全适合目标ImageView的边界。这将导致请求的延迟执行，直到ImageView被布置好。
     * 注意：此方法仅在您的目标是ImageView时有效。
     */
    public RequestCreator fit() {
        deferred = true;
        Log.d(Constants.TAG, "RequestCreator: 调用了requestCreator的fit方法");
        return this;
    }

    /**
     * 将图像调整为指定的尺寸大小。
     */
    public RequestCreator resizeDimen(int targetWidthResId, int targetHeightResId) {
        Resources resources = picassoTest.context.getResources();
        int targetWidth = resources.getDimensionPixelSize(targetWidthResId);
        int targetHeight = resources.getDimensionPixelSize(targetHeightResId);
        Log.d(Constants.TAG, "RequestCreator: 调用了requestCreator的resizeDimen方法");
        return resize(targetWidth, targetHeight);
    }

    /**
     * 同上，但不需要单独写一个dimen文件
     */
    public RequestCreator resize(int targetWidth, int targetHeight) {
        data.resize(targetWidth, targetHeight);
        return this;
    }

    /**
     * 在resize(int, int)指定的范围内裁剪图像，而不是扭曲纵横比。这种裁剪技术会缩放图像，使其填充请求的边界，然后裁剪多余的边界
     */
    public RequestCreator centerCrop() {
        data.centerCrop();
        return this;
    }

    /**
     * 在resize(int, int)指定的范围内将图像居中。这会缩放图像，使两个维度都等于或小于请求的边界。
     */
    public RequestCreator centerInside() {
        data.centerInside();
        return this;
    }

    /**
     * 仅当原始图像大小大于resize(int, int)指定的目标大小时才调整图像大小。
     */
    public RequestCreator onlyScaleDown() {
        data.onlyScaleDown();
        return this;
    }

    /**
     * 将图像旋转指定的度数。
     */
    public RequestCreator rotate(float degrees) {
        data.rotate(degrees);
        return this;
    }

    /**
     * 尝试使用指定的配置解码图像。
     * 注意：该值可能会被BitmapFactory忽略。有关更多详细信息，请参阅its documentation 。
     * (如果使用默认的话，可能并不需要设置，后面再看看)
     */
    public RequestCreator config(Bitmap.Config config) {
        data.config(config);
        return this;
    }

    /**
     * 设置此请求的稳定密钥，而不是缓存时使用的 URI 或资源 ID。具有相同值的两个请求被认为是针对相同资源的
     */
    public RequestCreator stableKey(String stableKey) {
        data.stableKey(stableKey);
        return this;
    }

    /**
     * 设置此请求的优先级。
     * 这将影响请求执行的顺序，但不能保证。默认情况下，所有请求都具有Picasso.Priority.NORMAL优先级，
     * 但fetch()请求除外，它默认具有Picasso.Priority.LOW优先级。
     */
    public RequestCreator priority(PicassoTest.Priority priority) {
        data.priority(priority);
        return this;
    }

//    /**
//     * 同步完成此请求。不得从主线程调用。
//     * 注意：此操作的结果不会缓存在内存中，因为不能保证底层Cache实现是线程安全的。
//     */
//    public Bitmap get() throws IOException {
//        long started = System.nanoTime();
//        checkNotMain();
//
//        if (deferred) {
//            throw new IllegalStateException("Fit cannot be used with get.");
//        }
//        if (!data.hasImage()) {
//            return null;
//        }
//
//        Request finalData = createRequest(started);
//        String key = createKey(finalData, new StringBuilder());
//
//        Action action = new GetAction(picasso, finalData, memoryPolicy, networkPolicy, tag, key);
//        return forRequest(picasso, picasso.dispatcher, picasso.cache, picasso.stats, action).hunt();
//    }

    public void into(ImageView imageView) {
        long started = System.nanoTime();
        checkMain();
        if (imageView == null) {
            throw new IllegalArgumentException("ImageView must not be null.");
        }
        if (deferred) {
            throw new IllegalStateException("Fit cannot be used with a Target.");
        }

        // 这个判断，之所以取消下载，是因为recycleView是复用view的，每次滑动开启新的view的时候，需要将这个view之前所对应的下载任务给取消掉，使其只能加载一个图片
        if (!data.hasImage()) {
            picassoTest.cancelExistingRequest(imageView);
            if (setPlaceholder) {
                setPlaceholder(imageView, getPlaceholderDrawable());
            }
            return;
        }

        Request request = createRequest(started);
        String requestKey = createKey(request);

        // 原本是需要进行一个策略判断的，判断是否需要进行缓存，对于部分一次性请求能够节省资源，
        // 但是没有研究太懂，但是我发现大部分都是需要进行这个缓存读取的，所以现在采取直接读取的方式
        Bitmap bitmap = picassoTest.quickMemoryCacheCheck(requestKey);
        if (bitmap != null) {
            Log.d(Constants.TAG, "into:在requestCreator里面的第一次缓存读取，读取到了照片 ");
            picassoTest.cancelExistingRequest(imageView);
            setBitmap(imageView, picassoTest.context, bitmap, MEMORY);
            return;
        }
        Log.d(Constants.TAG, "into:在requestCreator里面的第一次缓存读取，没有读取到了照片 ");

        if (setPlaceholder) {
            setPlaceholder(imageView, getPlaceholderDrawable());
        }


        Action action =
                new ImageViewAction(picassoTest, request, imageView, errorResId,
                        requestKey,tag);
        picassoTest.enqueueAndSubmit(action);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private Drawable getPlaceholderDrawable() {
        if (placeholderResId != 0) {
            return picassoTest.context.getResources().getDrawable(placeholderResId);
        }
        Log.d(Constants.TAG, "getPlaceholderDrawable: 这个会返回空，因为并没有调用placeholderResId去设置资源id（没有直接为drawable单独设置方法来区别，只能是id）");
        return null;
    }

    /**
     * 创建请求，可选择通过请求转换器传递它
     */
    private Request createRequest(long started) {
        int id = nextId.getAndIncrement();
        Request request = data.build();
        request.id = id;
        request.started = started;
        Log.d(Constants.TAG, "createRequest: " + request.plainId() + request.toString());
        return request;
    }

}
