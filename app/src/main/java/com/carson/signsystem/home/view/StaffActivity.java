package com.carson.signsystem.home.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.carson.signsystem.R;
import com.carson.signsystem.custom_views.MyCalendarView;
import com.carson.signsystem.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@Route(path = Constants.ACTIVITY_STAFF)
public class StaffActivity extends AppCompatActivity implements MyCalendarView.OnChangeMonthListener {

    @BindView(R.id.btn_sign)
    Button btn_sign;
    @BindView(R.id.view_pager)
    ViewPager view_pager;

    private List<MyCalendarView> mCacheCalendars;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff);

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
        view_pager.setAdapter(new MyPagerAdapter());
        view_pager.setCurrentItem(11);
    }

    @OnClick({R.id.btn_sign})
    public void onSign() {
        ARouter.getInstance().build(Constants.ACTIVITY_SIGN)
                .withTransition(R.anim.fade_right_in, R.anim.fade_scale_out)
                .navigation(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_scale_in, R.anim.fade_right_out);
    }

    @Override
    public void onChangeMonth(MyCalendarView.TYPE type) {
        if (type == MyCalendarView.TYPE.BEFORE) {
            if (view_pager.getCurrentItem() > 0) {
                view_pager.setCurrentItem(view_pager.getCurrentItem() - 1, true);
            }
        } else {
            if (view_pager.getCurrentItem() < 11) {
                view_pager.setCurrentItem(view_pager.getCurrentItem() + 1, true);
            }
        }
    }

    private class MyPagerAdapter extends PagerAdapter {

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            MyCalendarView mCalendarView = mCacheCalendars.get(position);
            mCalendarView.setMonthChangeListener(StaffActivity.this);
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
