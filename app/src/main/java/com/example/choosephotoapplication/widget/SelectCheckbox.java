package com.example.choosephotoapplication.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.choosephotoapplication.R;

public class SelectCheckbox extends View {
    public SelectCheckbox(Context context) {
        super(context);
    }

    public SelectCheckbox(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SelectCheckbox);
        initParameter(typedArray);
        init();
    }

    public SelectCheckbox(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SelectCheckbox,defStyleAttr,0);
        initParameter(typedArray);
        init();
    }


    private OnOnStateChangeListener listener;
    private int mBackgroundColorNormal;
    private int mBackgroundColorSelect;
    private int mTextColor;
    private int mStrokeColor;
    private float mStrokeWidth;
    /**
     * 实心圆半径
     */
    private float mSolidRadius;
    /**
     * 圆环半径
     */
    private float mRingRadius;
    private float mTextSize;

    /**
     * 圆心x坐标
     */
    private int mCenterX;
    /**
     * 圆心y坐标
     */
    private int mCenterY;

    /**
     * 圆环画笔
     */
    private Paint mStrokePaint;
    /**
     * 背景填充画笔
     */
    private Paint mSolidPaint;
    /**
     * 文字画笔
     */
    private Paint mTextPaint ;

    /**
     * 要画的数字
     */
    private String text;
    /**
     * 是否已经被选上
     */
    private boolean isSelected = false;

    /**
     * 标识
     */
    private int tag;

    private void initParameter(TypedArray typedArray){
        mBackgroundColorNormal = typedArray.getColor(R.styleable.SelectCheckbox_backgroundColorNormal, Color.WHITE);
        mBackgroundColorSelect = typedArray.getColor(R.styleable.SelectCheckbox_getBackgroundColorSelect, Color.GREEN);
        mTextColor = typedArray.getColor(R.styleable.SelectCheckbox_textColor, Color.BLACK);
        mStrokeColor = typedArray.getColor(R.styleable.SelectCheckbox_strokeColor, Color.BLACK);
        mStrokeWidth = typedArray.getDimension(R.styleable.SelectCheckbox_strokeWidth, 3);
        mSolidRadius = typedArray.getDimension(R.styleable.SelectCheckbox_solidRadius, 17);
        mTextSize = typedArray.getDimension(R.styleable.SelectCheckbox_textSize, 14);
        text = typedArray.getString(R.styleable.SelectCheckbox_textShow);
        typedArray.recycle();//回收很重要
    }

    /**
     * 初始化操作
     */
    private void init() {
        if(TextUtils.isEmpty(text)){
            text = "1";
        }

        mRingRadius = mSolidRadius + mStrokeWidth / 2;
        setClickable(true);
//        setOnClickListener(v -> {
//                if(listener != null){
//                    listener.onClick(isSelected);
//                }
//                isSelected = !isSelected;
//                invalidate();
//        });

        mStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mStrokePaint.setColor(mStrokeColor);
        mStrokePaint.setStyle(Paint.Style.STROKE);
        mStrokePaint.setStrokeWidth(mStrokeWidth);

        mSolidPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSolidPaint.setColor(mBackgroundColorNormal);
        mSolidPaint.setStyle(Paint.Style.FILL);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mTextSize);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int exceptWidth = (int) ((mSolidRadius + mStrokeWidth) * 2) + getPaddingLeft() + getPaddingRight();
        int exceptHeight = (int) ((mSolidRadius + mStrokeWidth) * 2) + getPaddingTop() + getPaddingBottom();
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(exceptWidth, MeasureSpec.EXACTLY);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(exceptHeight,MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mCenterX = w / 2;//获取圆心x坐标
        mCenterY = h / 2;//获取圆心y坐标
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawCircle(canvas);//画实心圆
        drawRing(canvas);//画圆环
        drawText(canvas);//画文本
    }


    private void drawText(Canvas canvas) {
        Rect bounds = new Rect();
        mTextPaint.getTextBounds(text, 0, text.length(), bounds);
        float x = (getMeasuredWidth() - bounds.width()) / 2;
        float y = (getMeasuredHeight() + bounds.height()) /2;
        if (isSelected) {
            canvas.drawText(text, x, y, mTextPaint);
        } else {
            canvas.drawText("", x, y, mTextPaint);
        }
    }

    private void drawRing(Canvas canvas) {
        RectF rectF = new RectF();
        rectF.top = mCenterY - mRingRadius;
        rectF.bottom = mCenterY + mRingRadius;
        rectF.left = mCenterX - mRingRadius;
        rectF.right = mCenterX + mRingRadius;
        canvas.drawArc(rectF, 0, 360, false, mStrokePaint);
    }

    private void drawCircle(Canvas canvas) {
        if (!isSelected) {
            canvas.drawCircle(mCenterX, mCenterY, mSolidRadius, mSolidPaint);
        } else {
            mSolidPaint.setColor(mBackgroundColorSelect);
            canvas.drawCircle(mCenterX, mCenterY, mSolidRadius, mSolidPaint);
            mSolidPaint.setColor(mBackgroundColorNormal);
        }

    }

    /**
     * 设置监听
     */
    public void setOnStateChangeListener(OnOnStateChangeListener listener) {
        this.listener = listener;
    }


    public void setViewText(String text,boolean isViewClick) {
        this.text = text;
//        if(!isViewClick){
//            isSelected = !TextUtils.isEmpty(text);
//        }
        isSelected = isViewClick;
        invalidate();
    }
    public String getViewText(){
        return text;
    }
    public boolean isViewSelected() {
        return isSelected;
    }

    public void setViewTag (int tag ){
        this.tag = tag;
    }

    private SelectCheckbox getView (int tag){
        if(tag == this.tag){
            return this;
        }else {
            return null;
        }
    }

    public interface OnOnStateChangeListener {
        void onClick(boolean isSelected);
    }
}
