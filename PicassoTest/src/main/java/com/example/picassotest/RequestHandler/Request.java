package com.example.picassotest.RequestHandler;

import static java.util.Collections.unmodifiableList;

import android.graphics.Bitmap;
import android.net.Uri;

import com.example.picassotest.PicassoTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Request {
    private static final long TOO_LONG_LOG = TimeUnit.SECONDS.toNanos(5);
    /**
     * 请求的唯一 ID。
     */
    int id;

    /**
     * 首次提交请求的时间（以纳秒为单位）
     */
    long started;

    /**
     * 用于此请求的NetworkPolicy 。
     */
    int networkPolicy;

    /**
     * 图像 URI。
     * 这与resourceId互斥。
     */
    public final Uri uri;

    /**
     * 缓存时使用此请求的可选稳定密钥，而不是 URI 或资源 ID。具有相同值的两个请求被认为是针对相同资源的。
     */
    public final String stableKey;

    /**
     * 调整大小的目标图像宽度。
     */
    public final int targetWidth;

    /**
     * 调整大小的目标图像高度
     */
    public final int targetHeight;

    /**
     * 内置转换后要应用的自定义转换列表
     */
    public final List<Transformation> transformations;

    /**
     * 用于解码的目标图像配置
     */
    public final Bitmap.Config config;

    /**
     * 请求的优先级
     */
    public final PicassoTest.Priority priority;

    /**
     * 如果最终图像应使用“centerInside”缩放技术，则为真。
     * 这与centerCrop是互斥的
     */
    public final boolean centerInside;

    /**
     * 如果最终图像应使用“centerCrop”缩放技术，则为真。
     * 这与centerInside是互斥的。
     */
    public final boolean centerCrop;

    /**
     * 以度为单位旋转图像的量。
     */
    public final float rotationDegrees;

    /**
     * 告知处理器是否原始图像是否在于设置宽高，如果大于则需要进行裁剪或修改
     */
    public final boolean onlyScaleDown;

    public Request(Uri uri, String stableKey, int targetWidth, int targetHeight,
                   List<Transformation> transformations, Bitmap.Config config, PicassoTest.Priority priority,
                   boolean centerInside, boolean centerCrop, float rotationDegrees, boolean onlyScaleDown) {
        this.uri = uri;
        this.stableKey = stableKey;
        this.targetWidth = targetWidth;
        this.targetHeight = targetHeight;
        this.rotationDegrees = rotationDegrees;
        this.onlyScaleDown = onlyScaleDown;
        if (transformations == null) {
            this.transformations = null;
        } else {
            this.transformations = unmodifiableList(transformations);
        }
        this.config = config;
        this.priority = priority;
        this.centerInside = centerInside;
        this.centerCrop = centerCrop;
    }

    /**
     * @return 返回请求体中包含内容
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Request{");
        if (uri != null){
            sb.append(uri);
        }
        if (transformations != null && !transformations.isEmpty()) {
            for (Transformation transformation : transformations) {
                sb.append(' ').append(transformation.key());
            }
        }
        if (stableKey != null) {
            sb.append(" stableKey(").append(stableKey).append(')');
        }
        if (targetWidth > 0) {
            sb.append(" resize(").append(targetWidth).append(',').append(targetHeight).append(')');
        }
        if (centerCrop) {
            sb.append(" centerCrop");
        }
        if (centerInside) {
            sb.append(" centerInside");
        }
        if (config != null) {
            sb.append(' ').append(config);
        }
        sb.append('}');

        return sb.toString();
    }

    /**
     * @return 如果超时了进行打印
     */
    String logId() {
        long delta = System.nanoTime() - started;
        if (delta > TOO_LONG_LOG) {
            return plainId() + '+' + TimeUnit.NANOSECONDS.toSeconds(delta) + 's';
        }
        return plainId() + '+' + TimeUnit.NANOSECONDS.toMillis(delta) + "ms";
    }
    String plainId() {
        return "[R" + id + ']';
    }

    public String getName() {
        if (uri != null) {
            return String.valueOf(uri.getPath());
        }
        return null;
    }

    /**
     * @return 判断是否有设置图像最高值和最宽值
     */
    public boolean hasSize() {
        return targetWidth != 0 || targetHeight != 0;
    }

    boolean needsTransformation() {
        return needsMatrixTransform() || hasCustomTransformations();
    }

    boolean needsMatrixTransform() {
        return hasSize() || rotationDegrees != 0;
    }

    boolean hasCustomTransformations() {
        return transformations != null;
    }

    public Builder buildUpon() {
        return new Builder(this);
    }

    /**
     * request的构造者
     */
    public static final class Builder {
        private Uri uri;
        private String stableKey;
        private int targetWidth;
        private int targetHeight;
        private boolean centerCrop;
        private boolean centerInside;
        private float rotationDegrees;
        private boolean onlyScaleDown;
        private List<Transformation> transformations;
        private Bitmap.Config config;
        private PicassoTest.Priority priority;

        //构造方法，直接设置uri
        public Builder(Uri uri) {
            setUri(uri);
        }

        private Builder(Request request) {
            uri = request.uri;
            stableKey = request.stableKey;
            targetWidth = request.targetWidth;
            targetHeight = request.targetHeight;
            centerCrop = request.centerCrop;
            centerInside = request.centerInside;
            rotationDegrees = request.rotationDegrees;
            onlyScaleDown = request.onlyScaleDown;
            if (request.transformations != null) {
                transformations = new ArrayList<Transformation>(request.transformations);
            }
            config = request.config;
            priority = request.priority;
        }

        /**
         * 设置uri
         *
         *
         */
        public Builder setUri(Uri uri) {
            if (uri == null) {
                throw new IllegalArgumentException("Image URI may not be null.");
            }
            this.uri = uri;
            return this;
        }

        boolean hasImage() {
            return uri != null;
        }

        boolean hasSize() {
            return targetWidth != 0 || targetHeight != 0;
        }

        boolean hasPriority() {
            return priority != null;
        }

        /**
         * 设置缓存时要使用的稳定密钥，而不是 URI 或资源 ID。具有相同值的两个请求被认为是针对相同资源的。
         */
        public Builder stableKey(String stableKey) {
            this.stableKey = stableKey;
            return this;
        }

        /**
         * 将图像大小调整为以像素为单位的指定大小。使用 0 作为所需的尺寸来调整大小保持纵横比。
         */
        public Builder resize(int targetWidth, int targetHeight) {
            if (targetWidth < 0) {
                throw new IllegalArgumentException("Width must be positive number or 0.");
            }
            if (targetHeight < 0) {
                throw new IllegalArgumentException("Height must be positive number or 0.");
            }
            if (targetHeight == 0 && targetWidth == 0) {
                throw new IllegalArgumentException("At least one dimension has to be positive number.");
            }
            this.targetWidth = targetWidth;
            this.targetHeight = targetHeight;
            return this;
        }

        /**
         * 清除调整大小转换（如果有）。如果设置，这也将清除中心裁剪/内部。
         */
        public Builder clearResize() {
            targetWidth = 0;
            targetHeight = 0;
            centerCrop = false;
            centerInside = false;
            return this;
        }

        /**
         * 在resize(int, int)指定的范围内裁剪图像，而不是扭曲纵横比。
         * 这种裁剪技术会缩放图像，使其填充请求的边界，然后裁剪多余的边界。
         */
        public Builder centerCrop() {
            if (centerInside) {
                throw new IllegalStateException("Center crop can not be used after calling centerInside");
            }
            centerCrop = true;
            return this;
        }

        /**
         * 清除中心裁剪转换标志（如果设置）
         */
        public Builder clearCenterCrop() {
            centerCrop = false;
            return this;
        }

        /**
         * 在resize(int, int)指定的范围内将图像居中。
         * 这会缩放图像，使两个维度都等于或小于请求的边界
         */
        public Builder centerInside() {
            if (centerCrop) {
                throw new IllegalStateException("Center inside can not be used after calling centerCrop");
            }
            centerInside = true;
            return this;
        }

        /**
         * @return 如果设置，则清除中心内部转换标志。
         */
        public Builder clearCenterInside() {
            centerInside = false;
            return this;
        }

        /**
         * 仅当原始图像大小大于resize(int, int)指定的目标大小时才调整图像大小
         */
        public Builder onlyScaleDown() {
            if (targetHeight == 0 && targetWidth == 0) {
                throw new IllegalStateException("onlyScaleDown can not be applied without resize");
            }
            onlyScaleDown = true;
            return this;
        }

        /**
         * @return 清除 onlyScaleUp 标志（如果已设置）。
         */
        public Builder clearOnlyScaleDown() {
            onlyScaleDown = false;
            return this;
        }

        /**
         * 将图像旋转指定的度数。
         */
        public Builder rotate(float degrees) {
            rotationDegrees = degrees;
            return this;
        }

        /**
         * 清除旋转变换（如果有）。
         */
        public Builder clearRotation() {
            rotationDegrees = 0;
            return this;
        }

        /**
         * 使用指定的配置解码图像。
         */
        public Builder config(Bitmap.Config config) {
            this.config = config;
            return this;
        }

        /**
         * 使用指定的优先级执行请求。
         */
        public Builder priority(PicassoTest.Priority priority) {
            if (priority == null) {
                throw new IllegalArgumentException("Priority invalid.");
            }
            if (this.priority != null) {
                throw new IllegalStateException("Priority already set.");
            }
            this.priority = priority;
            return this;
        }

        public Request build() {
            if (centerInside && centerCrop) {
                throw new IllegalStateException("Center crop and center inside can not be used together.");
            }
            if (centerCrop && (targetWidth == 0 && targetHeight == 0)) {
                throw new IllegalStateException(
                        "Center crop requires calling resize with positive width and height.");
            }
            if (centerInside && (targetWidth == 0 && targetHeight == 0)) {
                throw new IllegalStateException(
                        "Center inside requires calling resize with positive width and height.");
            }
            if (priority == null) {
                priority = PicassoTest.Priority.NORMAL;
            }
            return new Request(uri, stableKey, targetWidth, targetHeight,transformations,config,priority,centerInside,
                    centerCrop,rotationDegrees,onlyScaleDown);
        }
    }


}
