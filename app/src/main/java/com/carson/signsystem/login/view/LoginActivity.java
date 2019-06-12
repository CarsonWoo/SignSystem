package com.carson.signsystem.login.view;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.Response;

@Route(path = Constants.ACTIVITY_LOGIN)
public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    @Autowired(name = "identity")
    String identity;

    @Autowired(name = "code")
    String code;

    @BindView(R.id.login_button)
    Button login_button;

    @BindView(R.id.job_number_input)
    EditText job_number_input;
    @BindView(R.id.password_input)
    EditText password_input;

    @BindView(R.id.forget_password)
    TextView forget_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        setContentView(R.layout.activity_login);

        ARouter.getInstance().inject(this);

        ButterKnife.bind(this);
        Log.e(TAG, identity);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // 返回时引用动画
        overridePendingTransition(R.anim.fade_scale_in, R.anim.fade_right_out);
    }


    //登录按钮的响应事件
    @OnClick(R.id.login_button)
    public void onLogin(){
        //获取输入的工号和密码
        String job_number = job_number_input.getText().toString();
        String password = password_input.getText().toString();
        String url = "";
        //不同端设置对应的地址端口
        if (identity.equals("manager")){
            url = Constants.ADDRESS + "/employer_login";
        }else if (identity.equals("staff")){
            url = Constants.ADDRESS + "/employee_login";
        }
//        LoginModel model = new LoginModel(url, job_number, password);


//        Toast.makeText(LoginActivity.this,"URL为："+ url,Toast.LENGTH_LONG).show();

        //将对应信息封装post请求
        FormBody formBody = new FormBody.Builder()
                .add("job_number",job_number)
                .add("password",password)
                .build();
        HttpUtil.postOkHttpRequest(url,formBody, new okhttp3.Callback(){

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();

                Log.e(TAG, responseData);

                //此处返回的Json数据仅为一个登录是否成功的验证码，故用JSonObject更简单
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(responseData);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String code = null;
//                int code = 0;
                try {
                    code = jsonObject.getString("code");
//                    code = Integer.parseInt(codeString);
                } catch (JSONException | NullPointerException e) {
                    e.printStackTrace();
                }

                //登录成功后判断跳转至对应界面
                //字符串内容比对切记用equals方法
                if (code.equals("1")){
                    if (identity.equals("manager")){
                        ARouter.getInstance()
                                .build("/app/CompanyActivity")
                                .withString("job_number",job_number)
                                .withTransition(R.anim.fade_right_in,R.anim.fade_scale_out)
                                .navigation(LoginActivity.this);
                    }
                    if (identity.equals("staff")){
                        ARouter.getInstance()
                                .build(Constants.ACTIVITY_STAFF)
                                .withString("job_number",job_number)
                                .withTransition(R.anim.fade_right_in,R.anim.fade_scale_out)
                                .navigation(LoginActivity.this);
                    }
                }
                else{
                    //UI操作需要在主线程，包括Toast
                    Looper.prepare();
                    Toast.makeText(LoginActivity.this,"工号或密码错误，请重新输入", Toast.LENGTH_LONG).show();
//                    Toast.makeText(LoginActivity.this,"返回数据为："+ code, Toast.LENGTH_LONG).show();
                    Looper.loop();
                }
            }

            @Override
            public void onFailure(Call call, IOException e){
                Log.e(TAG, e.getMessage());
                Looper.prepare();
//                Toast.makeText(LoginActivity.this,"网络通信发生错误",Toast.LENGTH_LONG).show();
                Toast.makeText(LoginActivity.this,"错误信息："+ e.toString(),Toast.LENGTH_LONG).show();
                Looper.loop();
            }
        });

    }

    @OnClick(R.id.forget_password)
    public void setForget_password(){
        ARouter.getInstance()
                .build("/app/AuthenticationActivity")
                .withTransition(R.anim.fade_right_in,R.anim.fade_scale_out)
                .navigation(LoginActivity.this);
    }
}