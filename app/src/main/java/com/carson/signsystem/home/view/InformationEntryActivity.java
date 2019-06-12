package com.carson.signsystem.home.view;

import android.graphics.Color;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.carson.signsystem.R;
import com.carson.signsystem.utils.Constants;
import com.carson.signsystem.utils.HttpUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.Response;

@Route(path = "/app/InformationEntryActivity")
public class InformationEntryActivity extends AppCompatActivity {

    @BindView(R.id.information_entry_submit)
    Button sumbit;

    @BindView(R.id.information_entry_username_input)
    EditText username;

    @BindView(R.id.information_entry_job_number_input)
    EditText number;

    @BindView(R.id.information_entry_apartment_choose)
    Spinner department_choose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        setContentView(R.layout.activity_information_entry);

        ARouter.getInstance().inject(this);
        ButterKnife.bind(this);

        //获取数据源
        String[] departments = getResources().getStringArray(R.array.departments);
        //绑定数据源
        ArrayAdapter<String> department_choose_adapter = new ArrayAdapter<>(InformationEntryActivity.this,R.layout.support_simple_spinner_dropdown_item,departments);
        //将适配器绑定spinner
        department_choose.setAdapter(department_choose_adapter);


    }

    @OnClick(R.id.information_entry_submit)
    public void submitEmployee(){
        String name = username.getText().toString();
        String job_number = number.getText().toString();
        String department = department_choose.getSelectedItem().toString();

        String url = Constants.ADDRESS + "/register_employee";

        FormBody formBody = new FormBody.Builder()
                .add("job_number",job_number)
                .add("name",name)
                .add("department",department)
                .build();
        HttpUtil.postOkHttpRequest(url,formBody, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Looper.prepare();
                Toast.makeText(InformationEntryActivity.this,"通信失败，错误信息："+ e,Toast.LENGTH_LONG).show();
                Looper.loop();
            }

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
                    Toast.makeText(InformationEntryActivity.this,"发布成功",Toast.LENGTH_LONG).show();
                    Looper.loop();
                }
                else{
                    Looper.prepare();
                    Toast.makeText(InformationEntryActivity.this,"发布失败,返回数据为：" + responseData,Toast.LENGTH_LONG).show();
                    Log.e("返回数据为",responseData);
                    Log.e("返回json为：", String.valueOf(jsonObject));
                    Looper.loop();
                }
            }
        });
    }
}
