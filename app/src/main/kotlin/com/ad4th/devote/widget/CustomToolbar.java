package com.ad4th.devote.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ad4th.devote.R;

public class CustomToolbar extends Toolbar {

    private String title;
    private int type, bgColor,titleColor, leftImg, rightImg, leftImgTint, rightImgTint;

    public ImageButton imageButtonLeft, imageButtonRight;
    public TextView textViewCenter;


    public CustomToolbar(Context context) {
        this(context, null);
        init();
    }

    public CustomToolbar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
        init();
    }

    public CustomToolbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CustomToolbar, defStyleAttr, 0);
        initByAttributes(attributes);
        init();
        attributes.recycle();
    }

    /**
     * 초기 설정값
     */
    protected void initByAttributes(TypedArray attributes) {
        type = attributes.getResourceId(R.styleable.CustomToolbar_type, R.integer.custom_toolbar_left_title_right);
        bgColor = attributes.getResourceId(R.styleable.CustomToolbar_bgColor, R.color.main_toolbar_bg_color);
        titleColor = attributes.getResourceId(R.styleable.CustomToolbar_titleColor, R.color.block);
        leftImgTint = attributes.getResourceId(R.styleable.CustomToolbar_leftImgTint, -1);
        rightImgTint = attributes.getResourceId(R.styleable.CustomToolbar_rightImgTint, -1);
        leftImg = attributes.getResourceId(R.styleable.CustomToolbar_leftImg, -1);
        rightImg = attributes.getResourceId(R.styleable.CustomToolbar_rightImg, -1);
        title = attributes.getString(R.styleable.CustomToolbar_title);
    }


    /**
     * 초기화
     */
    public void init() {
        // View
        inflate(getContext(), R.layout.layout_toolbar, this);

        textViewCenter=findViewById(R.id.textViewCenter);
        imageButtonLeft=findViewById(R.id.imageButtonLeft);
        imageButtonRight=findViewById(R.id.imageButtonRight);

        textViewCenter.setTextColor(getResources().getColor(titleColor));
        setContentInsetsAbsolute(0, 0);
        setBackgroundColor(getResources().getColor(bgColor));
        if (!TextUtils.isEmpty(title)) {textViewCenter.setText(title);}

        if(leftImgTint > 0) imageButtonLeft.setColorFilter(getResources().getColor(leftImgTint));
        if(rightImgTint > 0) imageButtonRight.setColorFilter(getResources().getColor(rightImgTint));
        if(rightImg > 0) imageButtonRight.setImageResource(rightImg);
        if(leftImg > 0) imageButtonLeft.setImageResource(leftImg);

        setViewtype(type);
    }


    /**
     * Toolbar Type 설정
     */
    public void setViewtype(int type) {

        switch (type) {
            case R.integer.custom_toolbar_left:
                imageButtonLeft.setVisibility(VISIBLE);
                textViewCenter.setVisibility(GONE);
                imageButtonRight.setVisibility(GONE);
                break;
            case R.integer.custom_toolbar_left_title:
                imageButtonLeft.setVisibility(VISIBLE);
                textViewCenter.setVisibility(VISIBLE);
                imageButtonRight.setVisibility(GONE);
                break;
            case R.integer.custom_toolbar_left_title_right:
                imageButtonLeft.setVisibility(VISIBLE);
                textViewCenter.setVisibility(VISIBLE);
                imageButtonRight.setVisibility(VISIBLE);
                break;
            case R.integer.custom_toolbar_title_right:
                imageButtonLeft.setVisibility(GONE);
                textViewCenter.setVisibility(VISIBLE);
                imageButtonRight.setVisibility(VISIBLE);
                break;
            case R.integer.custom_toolbar_right:
                imageButtonLeft.setVisibility(GONE);
                textViewCenter.setVisibility(GONE);
                imageButtonRight.setVisibility(VISIBLE);
                break;
            case R.integer.custom_toolbar_title:
                imageButtonLeft.setVisibility(GONE);
                textViewCenter.setVisibility(VISIBLE);
                imageButtonRight.setVisibility(GONE);
                break;
        }
    }
}
