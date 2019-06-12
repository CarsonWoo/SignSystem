package com.carson.signsystem.home.view;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.carson.signsystem.R;
import com.carson.signsystem.utils.Constants;
import com.carson.signsystem.utils.HttpUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnLongClick;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.Response;
import okhttp3.TlsVersion;

@Route(path = "/app/SigningActivity")
public class SigningActivity extends AppCompatActivity {
    //本活动准备上传服务器的对应信息
    String date_begin = "";
    String date_end = "";
    String time_begin = "";
    String time_end = "";
    int range = 1;

    ArrayList<String> departmentsSet = new ArrayList<String> ();
    //获取Arouter传递来的值
    @Autowired(name = "address")
    String address;

    @Autowired(name = "range")
    String rangeString;

    @BindView(R.id.signing_place_name)
    TextView signing_place_name;

    @BindView(R.id.signing_place_locate)
    ImageView signing_place_locate;

    @BindView(R.id.signing_time_start)
    TextView signing_time_start;

    @BindView(R.id.signing_time_end)
    TextView signing_time_end;

    @BindView(R.id.signing_date_start)
    TextView signing_date_start;

    @BindView(R.id.signing_date_end)
    TextView signing_date_end;

    @BindView(R.id.signing_release_button)
    Button signing_release;

    @BindView(R.id.department_choose_all_check)
    CheckBox choose_all;

    @BindView(R.id.market_department_check)
    CheckBox market_department;

    @BindView(R.id.staffing_department_check)
    CheckBox staffing_department;

    @BindView(R.id.developing_department_check)
    CheckBox developing_department;

    int startHour = 0;
    int startMinute = 0;
    int endHour = 0;
    int endMinute = 0;
    int startYear = 0;
    int startMonth = 0;
    int startDay = 0;
    int endYear = 0;
    int endMonth = 0;
    int endDay = 0;

    Boolean Timelegal = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        setContentView(R.layout.activity_signing);

        ARouter.getInstance().inject(this);
        ButterKnife.bind(this);


        //确认定位地址
        checkAddress();


    }


    //2018年6月1日-2019年5月
    //只要年份小于，后面一定合法  年份等于时，只要月份小于，后面一定合法，以此类推
    //时间与日期的逻辑合法性分别单独考虑，不做合并考虑
    private void checkTime() {
        if (startYear<endYear){
            //开始年份小于结束年份，date部分一定成立，开始判断time部分
            if (startHour < endHour){
                Timelegal = true;
            }
            if (startHour == endHour){
                if (startMinute <= endHour){
                    Timelegal = true;
                }else {
                    Timelegal = false;
                }
            }
            if (startHour > endHour){
                Timelegal = false;
            }

        }
        if (startYear == endYear){
            if (startMonth < endMonth){
                //月份条件达到一定成立时，直接判断time部分
                if (startHour < endHour){
                    Timelegal = true;
                }
                if (startHour == endHour){
                    if (startMinute <= endHour){
                        Timelegal = true;
                    }else {
                        Timelegal = false;
                    }
                }
                if (startHour > endHour){
                    Timelegal = false;
                }
            }
            if (startMonth == endMonth){
                if (startDay <= endDay){
                    //日期满足后date部分结束，开始比较time部分
                    if (startHour < endHour){
                        Timelegal = true;
                    }
                    if (startHour == endHour){
                        if (startMinute <= endHour){
                            Timelegal = true;
                        }else {
                            Timelegal = false;
                        }
                    }
                    if (startHour > endHour){
                        Timelegal = false;
                    }
                }
                else {
                    Timelegal = false;
                }
            }
            if (startMonth > endMonth){
                Timelegal = false;
            }
        }
        if (startYear > endYear){
            Timelegal = false;
        }
    }

    //检测选择的部门
    private void checkDepartmentSelected() {
        if (choose_all.isChecked()){
//            market_department.setChecked(true);
//            developing_department.setChecked(true);
//            staffing_department.setChecked(true);
            departmentsSet.add("人事部");
            departmentsSet.add("市场部");
            departmentsSet.add("研发部");
        }else{
            Boolean market = market_department.isChecked();
            Boolean develop = developing_department.isChecked();
            Boolean staff = staffing_department.isChecked();
            if (market){
                departmentsSet.add("市场部");
            }
            if (develop){
                departmentsSet.add("研发部");
            }
            if (staff){
                departmentsSet.add("人事部");
            }
        }
    }

    private void rangeTransfer() {
        if (rangeString==null){
            range = 0;
        }
        if (rangeString.equals("1km")) {
            range = 1;
        }else if (rangeString.equals("2km")){
            range = 2;
        }else if (rangeString.equals("3km")){
            range = 3;
        }else if (rangeString.equals("4km")){
            range = 4;
        }
        else{
            range = 1;
        }
    }


    private void checkAddress() {
        if (address != null){
            signing_place_name.setText(address + "  (" + rangeString+")");
        }
    }


    @OnClick(R.id.signing_place_locate)
    public void setSigning_place_locate(){
//        //在standard的activity启动模式下，来回跳转会产生不同的本活动类的实例，因此不改变启动模式前提下应把这个实例杀死
//        SigningActivity.this.finish();
        ARouter.getInstance()
                .build("/app/SigningLocateActivity")
                .withTransition(R.anim.fade_scale_in,R.anim.fade_right_out)
                .navigation(SigningActivity.this);
    }


    @OnClick(R.id.signing_time_start)
    public void setSigning_time_start(){
//        Calendar calendar = Calendar.getInstance();
//        TimePickerDialog time = new TimePickerDialog();
//        String hour = String.valueOf(calendar.get(Calendar.HOUR));
//        String minute = String.valueOf(calendar.get(Calendar.MINUTE));

        new TimePickerDialog(this,new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int hour, int minute) {
                 startHour = hour;
                 startMinute = minute;
//                String shour = String.valueOf(hour);
//                String sminute = String.valueOf(minute);
                if (startMinute < 10){
                    signing_time_start.setText(startHour+":"+"0"+startMinute);
                }else {
                    signing_time_start.setText(startHour+":"+startMinute);
                }
                time_begin = String.valueOf(startHour) + ":" + String.valueOf(startMinute);
                Log.e("time",time_begin);
            }
        }, 0, 0, true).show();

    }


    @OnClick(R.id.signing_time_end)
    public void setSigning_time_end(){
//        int mHour = 0;
//        int mMinute = 0;
        new TimePickerDialog(this,new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int hour, int minute) {
                 endHour = hour;
                 endMinute = minute;
//                String shour = String.valueOf(hour);
//                String sminute = String.valueOf(minute);
                if (endMinute < 10){
                    signing_time_end.setText(endHour+":"+"0"+endMinute);
                }else {
                    signing_time_end.setText(endHour+":"+endMinute);
                }
                time_end = String.valueOf(endHour) + ":" + String.valueOf(endMinute);
                Log.e("Endtime",time_end);
            }
        }, 0, 0, true).show();
    }

    @OnClick(R.id.signing_date_start)
    public void setSigning_date_start(){
        new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener(){
            @Override
            public void onDateSet(DatePicker view,int year,int month,int day){
                startYear = year;
                startMonth = month+1;
                startDay = day;
                signing_date_start.setText(startYear + " - " + startMonth + " - " +startDay);
                date_begin = startYear + "-" + startMonth + "-" + startDay;
                Log.e("startDate",date_begin);
            }
        },2019,5,8).show();
    }

    @OnClick(R.id.signing_date_end)
    public void setSigning_date_end(){
        new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener(){
            @Override
            public void onDateSet(DatePicker view,int year,int month,int day){
                endYear = year;
                endMonth = month+1;
                endDay = day;
                signing_date_end.setText(endYear + " - " + endMonth + " - " +endDay);
                date_end = endYear + "-" + endMonth + "-" + endDay;
                Log.e("endDate",date_end);
            }
        },2019,5,8).show();
    }

    @OnClick(R.id.signing_release_button)
    public void setSigning_release(){
        //检测选中的部门
        checkDepartmentSelected();
        //确认时间与日期合法性
        checkTime();

        if (!Timelegal){
            Toast.makeText(SigningActivity.this,"输入的日期或时间非法，请检查后重试",Toast.LENGTH_LONG).show();
            departmentsSet.clear();
        }else {
            new AlertDialog.Builder(SigningActivity.this)
                    .setMessage("确定发布该签到吗？"+ "选中的部门包括"+ departmentsSet + "范围为" + rangeString)
                    .setNegativeButton("取消", (DialogInterface.OnClickListener)cancel)
                    .setPositiveButton("确定", (DialogInterface.OnClickListener) ensure)
                    .show();
        }

    }

    @OnCheckedChanged(R.id.department_choose_all_check)
    public void departmentAllChecked(){
        if(choose_all.isChecked()){
            market_department.setChecked(true);
            staffing_department.setChecked(true);
            developing_department.setChecked(true);
        }else{
            market_department.setChecked(false);
            staffing_department.setChecked(false);
            developing_department.setChecked(false);
        }
    }

    DialogInterface.OnClickListener ensure = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            //将范围字符串转化为post所规定的int类型
            rangeTransfer();
            String url = Constants.ADDRESS+"/release_sign_in";

            FormBody formBody = new FormBody.Builder()
                    .add("location",address)
                    .add("range",String.valueOf(range))
                    .add("date_begin",date_begin)
                    .add("date_end",date_end)
                    .add("time_begin",time_begin)
                    .add("time_end",time_end)
                    .build();
            departmentsSet.clear();
            Log.e("输入的信息",address + String.valueOf(range) + date_begin + date_end + time_begin + time_end);

            HttpUtil.postOkHttpRequest(url,formBody,new okhttp3.Callback(){

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    //返回码1为成功，0为失败
                    String responseData = response.body().string();
                    //json对象解析返回码
                    JSONObject jsonObject = null;
                    int code = 0;
                    try{
                        jsonObject = new JSONObject(responseData);
                        code = jsonObject.getInt("code");
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                    //响应返回码为1即成功的事件
                    if (code == 1){
                        Looper.prepare();
                        Toast.makeText(SigningActivity.this,"发布成功",Toast.LENGTH_LONG).show();
                        ARouter.getInstance()
                                .build("/app/SigningDetailActivity")
                                .withTransition(R.anim.fade_right_in,R.anim.fade_scale_out)
                                .navigation(SigningActivity.this);
//                        SigningActivity.this.finish();
                        Looper.loop();
                    }
                    else{
                        Looper.prepare();
                        Toast.makeText(SigningActivity.this,"发布失败" + code,Toast.LENGTH_LONG).show();
                        Looper.loop();
                    }
                }

                @Override
                public void onFailure(Call call, IOException e) {
                    Looper.prepare();
                    Toast.makeText(SigningActivity.this,"通信失败，失败报错："+ e,Toast.LENGTH_LONG).show();
                    Looper.loop();
                }
            });
//            ARouter.getInstance()
//                    .build("/app/SigningDetailActivity")
//                    .withTransition(R.anim.fade_right_in,R.anim.fade_scale_out)
//                    .navigation(SigningActivity.this);
        }
    };

    DialogInterface.OnClickListener cancel = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            departmentsSet.clear();
        }
    };

    @Override
    protected void onRestart() {
        super.onRestart();
    }
}
