package com.example.choosephotoapplication.widget;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.ScaleGestureDetector;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

public class SlideImageView extends androidx.appcompat.widget.AppCompatImageView implements ViewTreeObserver.OnGlobalLayoutListener {

    private Matrix matrix;

    public SlideImageView(@NonNull Context context) {
        super(context);
        init();
    }

    public SlideImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SlideImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

    }



    private void init() {
        setScaleType(ScaleType.MATRIX);
        matrix = new Matrix();
    }

    //imageView的大小
    private final PointF viewSize = new PointF();
    //图片的大小
    private final PointF imageSize = new PointF();
    //缩放后图片的大小
    private PointF scaleSize = new PointF();
    //最初的宽高的缩放比例
    private PointF originScale = new PointF();
    //imageview中bitmap的xy实时坐标
    private PointF bitmapOriginPoint = new PointF();

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        viewSize.set(width,height);

        Drawable drawable = getDrawable();
        if(drawable != null){
//            imageSize = new PointF(drawable.getMinimumWidth(),drawable.getMinimumHeight());
            imageSize.set(drawable.getMinimumWidth(),drawable.getMinimumHeight());
            showCenter();
        }

    }

    /**
     * 设置图片居中等比显示
     */
    private void showCenter() {
        float scaleX = viewSize.x/imageSize.x;
        float scaleY = viewSize.y/imageSize.y;

        float scale = Math.min(scaleX, scaleY);
        scaleImage(new PointF(scale,scale));

        //移动图片，并保存最初的图片左上角（即原点）所在坐标
        if (scaleX<scaleY){
            translationImage(new PointF(0,viewSize.y/2 - scaleSize.y/2));
            bitmapOriginPoint.x = 0;
            bitmapOriginPoint.y = viewSize.y/2 - scaleSize.y/2;
        }else {
            translationImage(new PointF(viewSize.x/2 - scaleSize.x/2,0));
            bitmapOriginPoint.x = viewSize.x/2 - scaleSize.x/2;
            bitmapOriginPoint.y = 0;
        }
        //保存下最初的缩放比例
        originScale.set(scale,scale);
    }

    /**
     * 将图片按照比例缩放，这里宽高缩放比例相等，所以PoinF里面的x,y是一样的
     */
    public void scaleImage(PointF scaleXY){
        matrix.setScale(scaleXY.x,scaleXY.y);
        // 特别注意，这里保存初始图片的缩放比例，要注意传过来的是比例毕竟小的那个x（或者y），因此他不一定会消掉
        scaleSize.set(scaleXY.x * imageSize.x,scaleXY.y * imageSize.y);
        setImageMatrix(matrix);
    }


    /**
     * 对图片进行x和y轴方向的平移
     */
    public void translationImage(PointF pointF){
        matrix.postTranslate(pointF.x,pointF.y);
        setImageMatrix(matrix);
    }
    @Override
    public void onGlobalLayout() {

    }
}
