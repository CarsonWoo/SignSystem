package com.carson.signsystem.home.view;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.carson.signsystem.R;
import com.carson.signsystem.custom_views.MyCalendarView;
import com.carson.signsystem.home.HomeService;
import com.carson.signsystem.home.adapter.MyPagerAdapter;
import com.carson.signsystem.home.model.StaffData;
import com.carson.signsystem.utils.Constants;
import com.carson.signsystem.utils.HttpUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

@Route(path = Constants.ACTIVITY_STAFF)
public class StaffActivity extends AppCompatActivity implements MyCalendarView.OnChangeMonthListener,
Callback<StaffData>{

    @BindView(R.id.btn_sign)
    Button btn_sign;
    @BindView(R.id.view_pager)
    ViewPager view_pager;
    @BindView(R.id.tv_username)
    TextView tv_username;
    @BindView(R.id.tv_staff_number)
    TextView tv_staff_number;
    @BindView(R.id.tv_staff_apartment)
    TextView tv_staff_apartment;

    private List<MyCalendarView> mCacheCalendars;
    private Map<DateFormat, List<Integer>> mSignDates = new HashMap<>();

    @Autowired(name = "job_number")
    long job_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        setContentView(R.layout.activity_staff);

        ARouter.getInstance().inject(this);
        ButterKnife.bind(this);

//        initViewPager();

        loadNetwork();

    }

    private void loadNetwork() {
        Retrofit retrofit = HttpUtil.getRetrofit();
        HomeService homeService = retrofit.create(HomeService.class);
        homeService.getInfo(String.valueOf(job_number)).enqueue(this);
    }

    private void initViewPager(int curYear, int curMonth) {
        mCacheCalendars = new ArrayList<>();
        for (int i = 11, year = curYear, month = curMonth; i >= 0; i --, month --) {
            if (month == -1) {
                month += 12;
                year --;
            }
            MyCalendarView myCalendarView = new MyCalendarView(this);
            myCalendarView.changeMonth(i - 11);
            for (DateFormat df : mSignDates.keySet()) {
                if (df.year == year && df.month == month && mSignDates.get(df).size() > 0) {
                    myCalendarView.setSelectedDates(mSignDates.get(df));
                }
            }

            mCacheCalendars.add(0, myCalendarView);
        }

        MyPagerAdapter myPagerAdapter = new MyPagerAdapter(this, mCacheCalendars);
        myPagerAdapter.setListener(this);
        view_pager.setAdapter(myPagerAdapter);
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

    /**
     * 联网获取用户信息
     * @param call 请求对象
     * @param response 响应对象
     */

    @Override
    public void onResponse(@NonNull Call<StaffData> call, @NonNull Response<StaffData> response) {
        StaffData data = response.body();
        doUIChange(data);

    }

    private void doUIChange(@NonNull StaffData data) {
        tv_username.setText(data.getName());
        tv_staff_number.setText(data.getJob_number());
        tv_staff_apartment.setText(data.getDepartment());
        String status = data.getStatus();

        setCalendarInfo(status);
    }

    private void setCalendarInfo(String status) {
        List<String> mDates = Arrays.asList(status.split("/"));
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        int curYear = c.get(Calendar.YEAR);
        int curMonth = c.get(Calendar.MONTH);
        for (int count = 11, month = curMonth, year = curYear; count >= 0; count --, month --) {
            if (month == -1) {
                month += 12;
                year --;
            }
            mSignDates.put(new DateFormat(year, month), new ArrayList<>());
        }
        for (String date : mDates) {
            int year = Integer.parseInt(date.split("-")[0]);
            int month = Integer.parseInt(date.split("-")[1]) - 1;
            int day = Integer.parseInt(date.split("-")[2]);
            for (DateFormat key : mSignDates.keySet()) {
                if ((key.year == year) && (key.month == month)) {
                    mSignDates.get(key).add(day);
                }
            }
        }

        initViewPager(curYear, curMonth);
    }

    @Override
    public void onFailure(@NonNull Call<StaffData> call, @NonNull Throwable t) {

    }


    private class DateFormat {
        private int year;
        private int month;

        public DateFormat(int year, int month) {
            this.year = year;
            this.month = month;
        }

        public int getYear() {return year;}
        public int getMonth() {
            return month;
        }
    }


}
