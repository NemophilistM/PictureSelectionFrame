package com.example.choosephotoapplication.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.example.choosephotoapplication.ViewConstants;

import java.util.ArrayList;
import java.util.List;


public class SquareView extends androidx.appcompat.widget.AppCompatImageView {
    public SquareView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }
    @Override
    @SuppressWarnings("ALL")
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        setMeasuredDimension(width,width);
    }
}
