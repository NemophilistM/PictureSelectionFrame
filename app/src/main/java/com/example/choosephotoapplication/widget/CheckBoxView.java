package com.example.choosephotoapplication.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.choosephotoapplication.R;

public class CheckBoxView extends View {


    /**
     * view宽的中心点(可以暂时理解为圆心)
     */
    private int mViewCenterX;
    /**
     * view高的中心点(可以暂时理解为圆心)
     */
    private int mViewCenterY;
    /**
     * 圆的颜色
     */
    private int mCircleColor;
    /**
     * 圆的半径
     */
    private float mCircleRadio;
    /**
     * 圆的边界宽度
     */
    private float mCircleSize;

    /**
     * 内圆的颜色
     */
    private int mInsideCircleColor;

    /**
     * 在中央显示的数字
     */
    private int mText;

    /**
     * 圆的中心区域
     */
    private RectF mBound = new RectF();


    private Paint mPaint =new Paint(Paint.ANTI_ALIAS_FLAG);;

    public CheckBoxView(Context context) {
        super(context);
    }

    public CheckBoxView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @SuppressLint("NonConstantResourceId")
    public CheckBoxView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // 获取自定义的属性
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CheckBoxView,defStyleAttr,0);
//        int n = typedArray.getIndexCount();
//        for (int i = 0; i < n; i++) {
//            int attr = typedArray.getIndex(i);
//            switch (attr){
//                case R.styleable.CheckBoxView_circleColor:
//                    mCircleColor = typedArray.getColor(attr, Color.BLACK);
//                    break;
//                case R.styleable.CheckBoxView_circleSize:
//                    mCircleSize = typedArray.getInt(attr,1);
//                    break;
//                case R.styleable.CheckBoxView_circleWidth:
//                    mCircleWidth = typedArray.getInt(attr,50);
//                    break;
//                case R.styleable.CheckBoxView_insideCircleColor:
//                    mInsideCircleColor = typedArray.getColor(attr,Color.BLUE);
//            }
//        }



        mCircleRadio = typedArray.getDimension(R.styleable.CheckBoxView_circle_radio,10);
        mInsideCircleColor = typedArray.getColor(R.styleable.CheckBoxView_inside_circle_color,Color.BLUE);
        mCircleSize = typedArray.getDimension(R.styleable.CheckBoxView_circle_size,1);
        mCircleColor = typedArray.getColor(R.styleable.CheckBoxView_circle_color,Color.BLACK);

        typedArray.recycle();

//        //抗锯齿画笔
//        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //防止边缘锯齿
        mPaint.setAntiAlias(true);
        //需要重写onDraw就得调用此
        this.setWillNotDraw(false);

    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //view的宽和高,相对于父布局(用于确定圆心)
        int viewWidth = getMeasuredWidth();
        int viewHeight = getMeasuredHeight();
        mViewCenterX = viewWidth / 2;
        mViewCenterY = viewHeight / 2;
        //画矩形
//        mBound = new Rect(mViewCenterX - mCircleRadio, mViewCenterY - mCircleRadio, mViewCenterX + mCircleRadio, mViewCenterY + mCircleRadio);
        mBound.set(mViewCenterX - mCircleRadio, mViewCenterY - mCircleRadio, mViewCenterX + mCircleRadio, mViewCenterY + mCircleRadio);
        mPaint.setColor(mInsideCircleColor);
        canvas.drawCircle(mViewCenterX,mViewCenterY,mCircleRadio,mPaint);
        drawCircle(canvas);
    }

    private void drawCircle(Canvas canvas) {
        Paint ringPaint = new Paint(mPaint);
        ringPaint.setStyle(Paint.Style.STROKE);
        ringPaint.setStrokeWidth(mCircleSize);
        ringPaint.setColor(mCircleColor);
        canvas.drawArc(mBound,360,360,false,ringPaint);
    }
}
