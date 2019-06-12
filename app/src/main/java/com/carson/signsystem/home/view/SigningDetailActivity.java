package com.carson.signsystem.home.view;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.carson.signsystem.R;
import com.carson.signsystem.custom_views.MyCalendarView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


@Route(path = "/app/SigningDetailActivity")
public class SigningDetailActivity extends AppCompatActivity {

    @BindView(R.id.signing_detail_view_pager)
    ViewPager view_pager;

    private List<MyCalendarView> mCacheCalendars;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        setContentView(R.layout.activity_signing_detail);

        ARouter.getInstance().inject(this);
        ButterKnife.bind(this);

        initViewPager();
    }


    private void initViewPager() {
        mCacheCalendars = new ArrayList<>();
        for (int i = 11; i >= 0; i --) {
            MyCalendarView myCalendarView = new MyCalendarView(this);
            myCalendarView.changeMonth(i - 11);
            mCacheCalendars.add(0, myCalendarView);
        }
        view_pager.setAdapter(new SigningDetailActivity.MyPagerAdapter());
        view_pager.setCurrentItem(11);
    }

    private class MyPagerAdapter extends PagerAdapter {

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            MyCalendarView mCalendarView = mCacheCalendars.get(position);
//            mCalendarView.setMonthChangeListener(SigningDetailActivity.this);
            container.addView(mCalendarView);
            return mCalendarView;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return 12;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
            return o == view;
        }
    }
}
