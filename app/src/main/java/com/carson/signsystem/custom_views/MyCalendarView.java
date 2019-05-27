package com.carson.signsystem.custom_views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.carson.signsystem.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MyCalendarView extends View {

    private static final String TAG = "MyCalendarView";

    private static final String[] months = new String[]{"Jan", "February", "March", "April", "May", "June", "July",
            "August", "September", "October", "November", "December"};

    private static final String[] weeks = new String[]{"SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"};

    private static final List<Integer> selectedDates = new ArrayList<>();

    private Paint mBgPaint;
    private Paint mTextPaint;
    //    private Paint mWeekTextPaint;
    private Paint mPaint;
    private Paint mBorderPaint;

    private String currentMonth;
    private int currentDate;

    private Date mDate;

    private int mTitleSize;
    private int mWeekSize;
    private int mDateSize;

    private int mColorText;
    private int mSelectedColorText;
    private int mSelectedColor;

    private Shader mSignedShader;
    private Shader mShader;

    // 标题高度
    private float mTitleHeight;
    // 星期几的高度
    private float mWeekHeight;
    // 日期行的高度
    private float mDayHeight;

    private float mTitlePaddingTop;
    private float mWeekPaddingTop;

    // 日期计算的相关参数
    private int rowCount;
    private boolean isCurMonth;
    private int firstIndex;
    private int dayOfMonth;
    private int remains;

    private int perWidth;

    public MyCalendarView(Context context) {
        this(context, null);
    }

    public MyCalendarView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyCalendarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mTitleSize = (int) dp2px(30);
        mWeekSize = (int) dp2px(10);
        mDateSize = (int) dp2px(12);

        mColorText = Color.BLACK;
        mSelectedColorText = Color.WHITE;
        mSelectedColor = getResources().getColor(R.color.colorAccent);

        mBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

//        mTextPaint.setColor(Color.BLACK);

        mTextPaint.setTextSize(mTitleSize);
        mTitleHeight = mTextPaint.getFontMetrics().descent - mTextPaint.getFontMetrics().ascent;

        mTextPaint.setTextSize(mWeekSize);
        mWeekHeight = mTextPaint.getFontMetrics().descent - mTextPaint.getFontMetrics().ascent;

        mTextPaint.setTextSize(mDateSize);
        mDayHeight = mTextPaint.getFontMetrics().descent - mTextPaint.getFontMetrics().ascent;

        mBgPaint.setColor(Color.parseColor("#DDDDDD"));
        mBgPaint.setStrokeWidth(1);

        mTitlePaddingTop = dp2px(10);
        mWeekPaddingTop = dp2px(15);

        setMonth(new Date());
//        setDate(new Date());

    }

    public void setMonth(Date date) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
        String mCurDate = sf.format(date);
        Log.i(TAG, "mCurDate = " + mCurDate);
        String month = mCurDate.substring(4, 6);
        Log.i(TAG, "month = " + month);
        if (month.startsWith("0")) {
            month = month.substring(1);
        }
        Log.i(TAG, "month = " + month);
        int monthInt = Integer.parseInt(month);
        this.currentMonth = months[monthInt - 1];
        Log.i(TAG, "monthInt = " + monthInt);

        String mDate = mCurDate.substring(6, 8);
        if (mDate.startsWith("0")) {
            mDate = mDate.substring(1);
        }
        // 获取到当前日期
        this.currentDate = Integer.parseInt(mDate);

        Calendar c = Calendar.getInstance();
        c.setTime(date);
        // 当前月份第一天开始是星期几
        firstIndex = c.get(Calendar.DAY_OF_WEEK_IN_MONTH) - 1;
//        Log.i(TAG, "day of week in month = " + c.get(Calendar.DAY_OF_WEEK_IN_MONTH));
//        Log.i(TAG, "firstIndex = " + firstIndex);

        // 当前月份的天数
        dayOfMonth = c.getActualMaximum(Calendar.DAY_OF_MONTH);
        int days = dayOfMonth;
        Log.i(TAG, "dayOfMonth = " + dayOfMonth);

        days -= (firstIndex + 1);
        rowCount = 1;
        if (days % 7 != 0) rowCount++;
        rowCount += (days / 7);

        // 最后一行剩余的天数
        remains = days;
        for (int i = 1; i < rowCount - 1; i ++) {
            // 一开始days已经减去firstIndex了
            for (int j = 0; j < 7; j++) {
                remains --;
            }
        }
        Log.i(TAG, "remains = " + remains);

        selectedDates.add(4);
        selectedDates.add(9);
        selectedDates.add(16);
        selectedDates.add(20);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        Log.i(TAG, "on measure");
        int width = MeasureSpec.getSize(widthMeasureSpec); //布满父布局
        perWidth = width / 7;

        setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec), heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 设置关闭硬件加速 否则不显示阴影
        setLayerType(LAYER_TYPE_SOFTWARE, null);

//        drawBackground(canvas);

        drawMonth(canvas);

        drawWeek(canvas);

        drawDate(canvas);

//        setLayerType(LAYER_TYPE_HARDWARE, null);
    }

    private void drawMonth(Canvas canvas) {
//        Log.i(TAG, "canvas x = " + canvas);
        mTextPaint.setTextSize(mTitleSize);
        mTextPaint.setColor(Color.BLACK);
//        mTitleHeight = mTextPaint.getFontMetrics().bottom - mTextPaint.getFontMetrics().top;
        int x = (int) mTextPaint.measureText(currentMonth);
//        Log.i(TAG, "month X = " + x);
        canvas.drawText(currentMonth, x >> 1, mTitleHeight, mTextPaint);
    }

    private void drawWeek(Canvas canvas) {
        mTextPaint.setTextSize(mWeekSize);
//        float currentX = mWeekTextPaint.measureText();
        mTextPaint.setColor(Color.parseColor("#BBBBBB"));
//        mTextPaint.setTextAlign(Paint.Align.CENTER);
//        float measuredWidth = getMeasuredWidth();
//        Log.i(TAG, "measureWidth = " + measuredWidth);

//        float perWidth = measuredWidth / 7;
//        Log.i(TAG, "perWidth = " + perWidth);
//        mWeekHeight = mTextPaint.getFontMetrics().bottom - mTextPaint.getFontMetrics().top;

        for (int i = 0; i < 7; i++) {
            int len = (int) mTextPaint.measureText(weeks[i]);
            int currentX = (int) (i * perWidth + (perWidth - len) / 2);
            canvas.drawText(weeks[i], currentX, mTitleHeight
                    + mWeekPaddingTop + mWeekHeight, mTextPaint);
//            currentX += perWidth;
        }

        canvas.drawLine(0, mTitleHeight
                        + mWeekPaddingTop + mWeekHeight + dp2px(10), getMeasuredWidth(),
                mTitleHeight + mWeekPaddingTop + mWeekHeight + dp2px(10), mBgPaint);

    }

    private void drawDate(Canvas canvas) {
        mTextPaint.setColor(Color.BLACK);
        mTextPaint.setTextSize(mDateSize);
//        mTextPaint.setTextAlign(Paint.Align.CENTER);

        mPaint.setColor(Color.BLUE);
        mPaint.setStrokeWidth(1);

//        float perWidth = (float) getMeasuredWidth() / 7;

//        int date = 1;
        float rowHeight = mDayHeight + dp2px(30);
        int passDays = 0;
        for (int row = 0; row < rowCount; row++) {
            if (row == 0) {
                // 第一行
                drawDay(canvas, 7, firstIndex, row, rowHeight, passDays);
                passDays += firstIndex + 1;
            } else if (row == rowCount - 1) {
                // 最后一行
                drawDay(canvas, remains, 0, row, rowHeight, dayOfMonth - remains);
            } else {
                drawDay(canvas, 7, 0, row, rowHeight, passDays);
                passDays += 7;
            }
        }

    }

    private void drawDay(Canvas canvas, int endIndex, int startIndex, int rowCount, float rowHeight, int passDay) {
        int date = passDay + 1;
        float originTop = mTitleHeight + mWeekPaddingTop + mWeekHeight;
        for (int column = startIndex; column < endIndex; column ++) {
            float len = mTextPaint.measureText(String.valueOf(date));
            float currentX = column * perWidth + (perWidth - len) / 2;

            if (date == this.currentDate) {
                mBgPaint.setColor(getResources().getColor(R.color.colorAccent));
                float cx = column * perWidth + (float) perWidth / 2;
                float cy = originTop + rowHeight * (rowCount + 1) - 12;
                mBgPaint.setStyle(Paint.Style.STROKE);
                canvas.drawCircle(cx, cy, 40, mBgPaint);
                mTextPaint.setColor(mSelectedColor);
            }

            if (selectedDates.contains(date)) {
                mPaint.setColor(getResources().getColor(R.color.colorAccent));
                float cx = column * perWidth + (float) perWidth / 2;
                float cy = originTop + rowHeight * (rowCount + 1) - 12;
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setShadowLayer(10, 0, 5, mSelectedColor);
                canvas.drawCircle(cx, cy, 40, mPaint);
                mTextPaint.setColor(mSelectedColorText);
                mPaint.setShadowLayer(0, 0, 0, Color.GRAY);
            } else {
                // 不包含
                if (date < currentDate) {
                    mPaint.setColor(Color.argb(100, 228, 0, 0));
                    float cx = column * perWidth + (float) perWidth / 2;
                    float cy = originTop + rowHeight * (rowCount + 1) - 12;
                    mPaint.setStyle(Paint.Style.FILL);
                    mBorderPaint.setStyle(Paint.Style.STROKE);
                    mBorderPaint.setColor(Color.GRAY);
                    mPaint.setShadowLayer(10, 0, 5, Color.GRAY);
                    canvas.drawCircle(cx, cy, 40, mPaint);
                    canvas.drawCircle(cx, cy, 40, mBorderPaint);
                    mTextPaint.setColor(mSelectedColorText);
                    mPaint.setShadowLayer(0, 0, 0, Color.GRAY);
                }
            }

            canvas.drawText(String.valueOf(date), currentX, originTop + (rowCount + 1) * rowHeight, mTextPaint);
            date ++;
            mBgPaint.setStyle(Paint.Style.FILL);
            mBgPaint.setColor(Color.parseColor("#DDDDDD"));
            mTextPaint.setColor(Color.BLACK);
            mPaint.setShader(null);
        }
    }

    @Override
    public void invalidate() {
        requestLayout();
        super.invalidate();
    }

    private float dp2px(float dp) {
        float density = getResources().getDisplayMetrics().density;
        return (dp * density) + 0.5f;
    }
}
