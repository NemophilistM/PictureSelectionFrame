package com.example.choosephotoapplication.widget;

import android.animation.AnimatorSet;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.choosephotoapplication.R;

public class SlideImageView extends androidx.appcompat.widget.AppCompatImageView implements View.OnTouchListener {

    private Matrix matrix;
    private final Context context;

    public SlideImageView(@NonNull Context context) {
        super(context);
        this.context = context;
        init();
    }

    public SlideImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public SlideImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();

    }

    private void init() {
        setScaleType(ScaleType.MATRIX);
        matrix = new Matrix();
        setOnTouchListener(this);
    }

    /**
     * imageView的大小
     */
    private final PointF viewSize = new PointF();

    /**
     * 图片的大小
     */
    private final PointF imageSize = new PointF();

    /**
     * 缩放后图片的大小
     */
    private PointF scaleSize = new PointF();

    /**
     * 最初的宽高的缩放比例
     */
    private PointF originScale = new PointF();

    /**
     * imageview中bitmap的xy实时坐标
     */
    private PointF bitmapOriginPoint = new PointF();


    /**
     * 缩放的三个状态
     */
    public class ZoomMode {

        /**
         * 普通
         */
        public final static int Ordinary = 0;
        /**
         * 双击放大
         */
        public final static int ZoomIn = 1;

        /**
         * 双指缩放
         */
        public final static int TowFingerZoom = 2;
    }

    /**
     * 点击的点
     */
    private PointF clickPoint = new PointF();
    /**
     * 设置的双击检查时间间隔
     */
    private long doubleClickTimeSpan = 250;
    /**
     * 上次点击的时间
     */
    private long lastClickTime = 0;
    /**
     * 双击放大的倍数
     */
    private int doubleClickZoom = 2;
    /**
     * 当前缩放的模式
     */
    private int zoomInMode = ZoomMode.Ordinary;
    /**
     * 临时坐标比例数据
     */
    private PointF tempPoint = new PointF();

    /**
     * 最大缩放比例
     */
    private float maxScale = 4;

    /**
     * 最小缩放比例
     */
    private double minScale = 0.5;
    /**
     * 两点之间的距离
     */
    private float doublePointDistance = 0;
    /**
     * 双指缩放时候的中心点
     */
    private PointF doublePointCenter = new PointF();

    /**
     * 两指缩放的比例
     */
    private float doubleFingerScale = 0;
    /**
     * 上次触碰的手指数量
     */
    private int lastFingerNum = 0;
    /**
     * 滑动速度
     */
    private double follSpeed = 0;

    /**
     * 是否滑动
     */
    private boolean isInertialSliding = false;

    /**
     * 记录滑动屏幕时间
     */
    private double isPhaseTime = 0;
    /**
     * 记录结束滑屏前一段时间
     */
    private double isBeforeEndTime = 0;
    /**
     * 记录结束滑动应该暂停的时间
     */
    private double RememberTime = 200;

    /**
     * 存储移动距离
     */
    float[] moveFloat;

    /**
     * 最低移动距离
     */
    private float minMoveDistance = 50;
    /**
     * 自动滑动时间
     */
    private float autoSlideTime = 200;

    private PointF originClickPoint = new PointF();

    private float clickRange = 50;
    private ScaleAnimation scaleAnimation;



    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        viewSize.set(width, height);

        Drawable drawable = getDrawable();
        if (drawable != null) {
//            imageSize = new PointF(drawable.getMinimumWidth(),drawable.getMinimumHeight());
            imageSize.set(drawable.getMinimumWidth(), drawable.getMinimumHeight());
            showCenter();
        }

    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                //手指按下事件
                //记录被点击的点的坐标
                clickPoint.set(event.getX(), event.getY());
                //判断屏幕上此时被按住的点的个数，当前屏幕只有一个点被点击的时候触发
                if (event.getPointerCount() == 1) {
                    //设置一个点击的间隔时长，来判断是不是双击,,补充：添加对点击范围的判断
                    if (System.currentTimeMillis() - lastClickTime <= doubleClickTimeSpan && Math.abs(originClickPoint.x - clickPoint.x) < clickRange && Math.abs(originClickPoint.y - clickPoint.y) < clickRange) {
                        //如果图片此时缩放模式是普通模式，就触发双击放大
                        if (zoomInMode == ZoomMode.Ordinary) {
//                            //分别记录被点击的点到图片左上角x,y轴的距离与图片x,y轴边长的比例，方便在进行缩放后，算出这个点对应的坐标点
//                            tempPoint.set((clickPoint.x - bitmapOriginPoint.x) / scaleSize.x, (clickPoint.y - bitmapOriginPoint.y) / scaleSize.y);
//                            originClickPoint.x = event.getX();
//                            originClickPoint.y = event.getY();
//                            //进行缩放
//                            scaleImage(new PointF(originScale.x * doubleClickZoom, originScale.y * doubleClickZoom));
//                            //获取缩放后，图片左上角的xy坐标
//                            getBitmapOffset();
//                            //平移图片，使得被点击的点的位置不变。这里是计算缩放后被点击的xy坐标，与原始点击的位置的xy坐标值，计算出差值，然后做平移动作
//                            translationImage(new PointF(clickPoint.x - (bitmapOriginPoint.x + tempPoint.x * scaleSize.x), clickPoint.y - (bitmapOriginPoint.y + tempPoint.y * scaleSize.y)));
//                            zoomInMode = ZoomMode.ZoomIn;
//                            //并在双击放大后记录缩放比例
//                            doubleFingerScale = originScale.x * doubleClickZoom;


                            //分别记录被点击的点到图片左上角x,y轴的距离与图片x,y轴边长的比例，方便在进行缩放后，算出这个点对应的坐标点
                            tempPoint.set((clickPoint.x - bitmapOriginPoint.x) / scaleSize.x, (clickPoint.y - bitmapOriginPoint.y) / scaleSize.y);
                            originClickPoint.x = event.getX();
                            originClickPoint.y = event.getY();
                            //获取缩放后，图片左上角的xy坐标
                            getBitmapOffset();
                            if (scaleAnimation!=null){
                                scaleAnimation.cancel();
                            }
                            zoomInMode = ZoomMode.ZoomIn;
                            //并在双击放大后记录缩放比例
                            doubleFingerScale = originScale.x * doubleClickZoom;
                            scaleAnimation = new ScaleAnimation(1f,originScale.x*doubleClickZoom,1f,originScale.y*doubleClickZoom,clickPoint.x,clickPoint.y);
                            scaleAnimation.setDuration(2000);
                            scaleAnimation.setFillAfter(true);
//                            scaleAnimation.setRepeatMode(ScaleAnimation.);
//                            scaleAnimation.setRepeatCount(ScaleAnimation.INFINITE);
                            this.startAnimation(scaleAnimation);
                            //平移图片，使得被点击的点的位置不变。这里是计算缩放后被点击的xy坐标，与原始点击的位置的xy坐标值，计算出差值，然后做平移动作
                            translationImage(new PointF(clickPoint.x - (bitmapOriginPoint.x + tempPoint.x * scaleSize.x), clickPoint.y - (bitmapOriginPoint.y + tempPoint.y * scaleSize.y)));

                        } else {
                            //双击还原
                            originClickPoint.x = event.getX();
                            originClickPoint.y = event.getY();
                            showCenter();
                            zoomInMode = ZoomMode.Ordinary;
                        }
                    } else {
                        originClickPoint.x = event.getX();
                        originClickPoint.y = event.getY();
                        lastClickTime = System.currentTimeMillis();
                    }
                }


                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                //屏幕上已经有一个点被按住了 第二个点被按下时触发该事件
                //计算最初的两个手指之间的距离
                doublePointDistance = getDoubleFingerDistance(event);
                break;
            case MotionEvent.ACTION_POINTER_UP:
                //屏幕上已经有两个点按住 再松开一个点时触发该事件
                zoomInMode = ZoomMode.ZoomIn;
                //记录此时的双指缩放比例
                doubleFingerScale = scaleSize.x / imageSize.x;
                //记录此时屏幕触碰的点的数量
                lastFingerNum = 1;
                //判断缩放后的比例，如果小于最初的那个比例，就恢复到最初的大小
                if (scaleSize.x < viewSize.x && scaleSize.y < viewSize.y) {
                    zoomInMode = ZoomMode.Ordinary;
                    showCenter();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                //手指移动时触发事件
                // 移动
                if (zoomInMode != ZoomMode.Ordinary) {
                    //如果是多指，计算中心点为假设的点击的点
                    float currentX = 0;
                    float currentY = 0;
                    //获取此时屏幕上被触碰的点有多少个
                    int pointCount = event.getPointerCount();
                    //计算出中间点所在的坐标
                    for (int i = 0; i < pointCount; i++) {
                        currentX += event.getX(i);
                        currentY += event.getY(i);
                    }
                    currentX /= pointCount;
                    currentY /= pointCount;
                    //当屏幕被触碰的点的数量变化时，将最新算出来的中心点看作是被点击的点
                    if (lastFingerNum != event.getPointerCount()) {
                        clickPoint.x = currentX;
                        clickPoint.y = currentY;
                        lastFingerNum = event.getPointerCount();

//                        originClickPoint.x =currentX;
//                        originClickPoint.y =currentY;
                    }
                    //将移动手指时，实时计算出来的中心点坐标，减去被点击点的坐标就得到了需要移动的距离
                    float moveX = currentX - clickPoint.x;
                    float moveY = currentY - clickPoint.y;
                    //计算边界，使得不能已出边界，但是如果是双指缩放时移动，因为存在缩放效果，
                    //所以此时的边界判断无效
                    moveFloat = moveBorderDistance(moveX, moveY);
                    //处理移动图片的事件


                    // 如果是边界就告知父类view可以拦截，如果不是边界，就告知它不要拦截
                    getParent().requestDisallowInterceptTouchEvent(moveFloat[0] != 0);

                    translationImage(new PointF(moveFloat[0], moveFloat[1]));
                    clickPoint.set(currentX, currentY);
                }
                //缩放
                //判断当前是两个手指接触到屏幕才处理缩放事件
                if (event.getPointerCount() == 2) {
                    //如果此时缩放后的大小，大于等于了设置的最大缩放的大小，就不处理
                    if ((scaleSize.x / imageSize.x >= originScale.x * maxScale
                            || scaleSize.y / imageSize.y >= originScale.y * maxScale)
                            && getDoubleFingerDistance(event) - doublePointDistance > 0) {
                        break;
                    }
                    // 当缩放后的大小，小于等于设置的最小缩放大小，就不处理
                    if ((scaleSize.x / imageSize.x <= originScale.x * minScale
                            || scaleSize.y / imageSize.y <= originScale.y * minScale) && doublePointDistance - getDoubleFingerDistance(event) > 0) {
                        break;
                    }
                    //这里设置当双指缩放的的距离变化量大于50，并且当前不是在双指缩放状态下，
                    //就计算中心点，等一些操作
                    if (Math.abs(getDoubleFingerDistance(event) - doublePointDistance) > 50
                            && zoomInMode != ZoomMode.TowFingerZoom) {
                        //计算两个手指之间的中心点，当作放大的中心点
                        doublePointCenter.set((event.getX(0) + event.getX(1)) / 2,
                                (event.getY(0) + event.getY(1)) / 2);
                        // 将双指的中心点就假设为点击的点
                        clickPoint.set(doublePointCenter);
                        //下面就和双击放大基本一样
                        getBitmapOffset();
                        //分别记录被点击的点到图片左上角x,y轴的距离与图片x,y轴边长的比例，
                        //方便在进行缩放后，算出这个点对应的坐标点
                        tempPoint.set((clickPoint.x - bitmapOriginPoint.x) / scaleSize.x,
                                (clickPoint.y - bitmapOriginPoint.y) / scaleSize.y);
                        //设置进入双指缩放状态
                        zoomInMode = ZoomMode.TowFingerZoom;
                    }
                    //如果已经进入双指缩放状态，就直接计算缩放的比例，并进行位移
                    if (zoomInMode == ZoomMode.TowFingerZoom) {
                        //用当前的缩放比例与此时双指间距离的缩放比例相乘，就得到对应的图片应该缩放的比例
                        float scale = doubleFingerScale * getDoubleFingerDistance(event) / doublePointDistance;
                        //这里也是和双击放大时一样的
                        scaleImage(new PointF(scale, scale));
                        getBitmapOffset();
                        translationImage(
                                new PointF(clickPoint.x - (bitmapOriginPoint.x + tempPoint.x * scaleSize.x),
                                        clickPoint.y - (bitmapOriginPoint.y + tempPoint.y * scaleSize.y))
                        );
                    }
                }
                break;
            case MotionEvent.ACTION_UP:

//                double allMoveX =  event.getX()-originClickPoint.x;
//                double allMoveY =  event.getY()-originClickPoint.y;
//                // 获取位移速度
//                follSpeed = Math.sqrt(Math.pow(allMoveX,2)+Math.pow(allMoveY,2))/(200);
//                double follSpeedX = Math.abs(allMoveX)/200;
//                double follSpeedY = Math.abs(allMoveY)/200;
//                if(System.currentTimeMillis()-lastClickTime>=RememberTime&&(allMoveY)>=minMoveDistance||allMoveX>=minMoveDistance&&lastFingerNum == 1){
//                    for (int i = 0; i < autoSlideTime; i++) {
//                        translationImage(new PointF((float) follSpeedX,(float) follSpeedY));
//                    }
////                    translationImage(new PointF((float)follSpeed*autoSlideTime,(float) follSpeed*autoSlideTime));
//                }
//

                //手指松开时触发事件
                lastFingerNum = 0;
                break;
        }
        return true;
    }

    /**
     * 设置图片居中等比显示
     */
    private void showCenter() {
        if(scaleAnimation!=null){
            scaleAnimation.cancel();
        }
        float scaleX = viewSize.x / imageSize.x;
        float scaleY = viewSize.y / imageSize.y;

        float scale = Math.min(scaleX, scaleY);
        scaleImage(new PointF(scale, scale));
//        ScaleAnimation scaleAnimation2 = new ScaleAnimation(originScale.x*doubleClickZoom,imageSize.x,originScale.y*doubleClickZoom,imageSize.y,clickPoint.x,clickPoint.y);
//        scaleAnimation2.setDuration(2000);
//        scaleAnimation2.setFillAfter(true);
//        this.startAnimation(scaleAnimation2);
        //移动图片，并保存最初的图片左上角（即原点）所在坐标
        if (scaleX < scaleY) {
            translationImage(new PointF(0, viewSize.y / 2 - scaleSize.y / 2));
            bitmapOriginPoint.x = 0;
            bitmapOriginPoint.y = viewSize.y / 2 - scaleSize.y / 2;
        } else {
            translationImage(new PointF(viewSize.x / 2 - scaleSize.x / 2, 0));
            bitmapOriginPoint.x = viewSize.x / 2 - scaleSize.x / 2;
            bitmapOriginPoint.y = 0;
        }
        //保存下最初的缩放比例
        originScale.set(scale, scale);
        doubleFingerScale = scale;
    }

    /**
     * 将图片按照比例缩放，这里宽高缩放比例相等，所以PoinF里面的x,y是一样的
     */
    public void scaleImage(PointF scaleXY) {
        matrix.setScale(scaleXY.x, scaleXY.y);
        // 特别注意，这里保存初始图片的缩放比例，要注意传过来的是比例毕竟小的那个x（或者y），因此他不一定会消掉
        scaleSize.set(scaleXY.x * imageSize.x, scaleXY.y * imageSize.y);
        setImageMatrix(matrix);
    }


    /**
     * 获取view中bitmap的坐标点
     */
    public void getBitmapOffset() {
        float[] value = new float[9];
        float[] offset = new float[2];
        Matrix imageMatrix = getImageMatrix();
        imageMatrix.getValues(value);
        offset[0] = value[2];
        offset[1] = value[5];
        bitmapOriginPoint.set(offset[0], offset[1]);
    }

    /**
     * 对图片进行x和y轴方向的平移
     */
    public void translationImage(PointF pointF) {
        matrix.postTranslate(pointF.x, pointF.y);
        setImageMatrix(matrix);
    }

    /**
     * 计算两个手指间的距离
     */
    public static float getDoubleFingerDistance(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * 防止移动图片超过边界，计算边界情况
     */
    public float[] moveBorderDistance(float moveX, float moveY) {
        //计算bitmap的左上角坐标
        getBitmapOffset();
        //计算bitmap的右下角坐标
        float bitmapRightBottomX = bitmapOriginPoint.x + scaleSize.x;
        float bitmapRightBottomY = bitmapOriginPoint.y + scaleSize.y;

        if (moveY > 0) {
            //向下滑
            if (bitmapOriginPoint.y + moveY > 0) {
                if (bitmapOriginPoint.y < 0) {
                    moveY = -bitmapOriginPoint.y;
                } else {
                    moveY = 0;
                }
            }
        } else if (moveY < 0) {
            //向上滑
            if (bitmapRightBottomY + moveY < viewSize.y) {
                if (bitmapRightBottomY > viewSize.y) {
                    moveY = -(bitmapRightBottomY - viewSize.y);
                } else {
                    moveY = 0;
                }
            }
        }

        if (moveX > 0) {
            //向右滑
            if (bitmapOriginPoint.x + moveX > 0) {
                if (bitmapOriginPoint.x < 0) {
                    moveX = -bitmapOriginPoint.x;
                } else {
                    moveX = 0;
                }
            }
        } else if (moveX < 0) {
            //向左滑
            if (bitmapRightBottomX + moveX < viewSize.x) {
                if (bitmapRightBottomX > viewSize.x) {
                    moveX = -(bitmapRightBottomX - viewSize.x);
                } else {
                    moveX = 0;
                }
            }
        }
        return new float[]{moveX, moveY};
    }


}
