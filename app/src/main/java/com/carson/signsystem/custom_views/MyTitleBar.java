package com.carson.signsystem.custom_views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.carson.signsystem.R;

/**
 * Created by 84594 on 2019/5/6.
 */

public class MyTitleBar extends RelativeLayout {

    private View root;
    private ImageView ivLeft;
    private ImageView ivRight;
    private TextView title;
    private boolean shouldShowRightIcon;
    private boolean shouldShowLeftIcon;

    private String titleText;
    private int leftIconRes;
    private int rightIconRes;
    private float textSize;
    private int titleColor;

    public MyTitleBar(Context context) {
        this(context, null);
    }

    public MyTitleBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyTitleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        root = LayoutInflater.from(context).inflate(R.layout.my_title_bar, this, false);
        ivLeft = root.findViewById(R.id.iv_title_bar_left);
        ivRight = root.findViewById(R.id.iv_title_bar_right);
        title = root.findViewById(R.id.title_bar_title);
//        shouldShowRightIcon = false;


        TypedArray a = context.obtainStyledAttributes(R.styleable.MyTitleBar);

        titleText = a.getString(R.styleable.MyTitleBar_title);

        shouldShowLeftIcon = a.getBoolean(R.styleable.MyTitleBar_showLeftIcon, true);
        if (shouldShowLeftIcon) {
            leftIconRes = a.getResourceId(R.styleable.MyTitleBar_leftIcon, R.drawable.ic_back_arrow);
        }

        shouldShowRightIcon = a.getBoolean(R.styleable.MyTitleBar_showRightIcon, false);
        if (shouldShowRightIcon) {
            ivRight.setVisibility(VISIBLE);
            if (a.hasValue(R.styleable.MyTitleBar_rightIcon)) {
                rightIconRes = a.getResourceId(R.styleable.MyTitleBar_rightIcon, R.drawable.ic_menu);
                ivRight.setImageResource(rightIconRes);
            }
        }

        this.titleColor = a.getColor(R.styleable.MyTitleBar_titleColor, Color.GRAY);

        textSize = a.getDimension(R.styleable.MyTitleBar_titleSize, 18);


        a.recycle();

        title.setText(titleText);
        ivLeft.setImageResource(leftIconRes);
        title.setTextColor(titleColor);
        title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSize);

    }

    public void setShouldShowRightIcon(boolean shouldShowRightIcon) {
        if (shouldShowRightIcon) {
            ivRight.setVisibility(VISIBLE);
        } else {
            ivRight.setVisibility(GONE);
        }
        this.shouldShowRightIcon = shouldShowRightIcon;
    }

    public void setTitleText(String titleText) {
        this.titleText = titleText;
        this.title.setText(titleText);
    }

    public String getTitleText() {
        return titleText;
    }


    /**
     * 可以返回两个对象以实现动画效果
     *
     * @return
     */
    public ImageView getIvLeft() {
        return ivLeft;
    }

    public ImageView getIvRight() {
        return ivRight;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        if (heightMeasureSpec == MeasureSpec.AT_MOST) {
//            float density = getResources().getDisplayMetrics().density;
//            int height = (int) (50 * density + 0.5f);
//            setMeasuredDimension(widthMeasureSpec, height);
////            return;
//        }

    }
}
