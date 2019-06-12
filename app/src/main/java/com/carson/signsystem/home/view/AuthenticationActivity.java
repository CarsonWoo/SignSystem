package com.carson.signsystem.home.view;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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


@Route(path = "/app/AuthenticationActivity")
public class AuthenticationActivity extends AppCompatActivity {

    @BindView(R.id.authencation_job_number_input)
    EditText authencation_job_number;
    @BindView(R.id.authencation_name_input)
    EditText authencation_name;

    @BindView(R.id.authencation_submit)
    Button authencation_submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        setContentView(R.layout.activity_authentication);


        ARouter.getInstance().inject(this);

        ButterKnife.bind(this);
    }

    @OnClick(R.id.authencation_submit)
    public void setAuthencation_submit(){
        String job_number = authencation_job_number.getText().toString();
        String name = authencation_name.getText().toString();

        String url = Constants.ADDRESS + "/find_pwd";

        FormBody formBody = new FormBody.Builder()
                .add("job_number",job_number)
                .add("name",name)
                .build();

        HttpUtil.postOkHttpRequest(url,formBody,new okhttp3.Callback(){

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();

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
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //字符串内容比对切记用equals方法
                if (code.equals("1")){

                    ARouter.getInstance()
                            .build("/app/ResetPasswordActivity")
                            .withString("job_number",job_number)                       //将工号传到下一个活动中以便使用
                            .withTransition(R.anim.fade_right_in,R.anim.fade_scale_out)
                            .navigation(AuthenticationActivity.this);
                }
                else{
                    //UI操作需要在主线程，包括Toast
                    Looper.prepare();
                    Toast.makeText(AuthenticationActivity.this,"工号或姓名匹配错误，请重新输入", Toast.LENGTH_LONG).show();
//                    Toast.makeText(LoginActivity.this,"返回数据为："+ code, Toast.LENGTH_LONG).show();
                    Looper.loop();
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                Looper.prepare();
                Toast.makeText(AuthenticationActivity.this,"网络通信发生错误",Toast.LENGTH_LONG).show();
                Looper.loop();
            }


        });
    }
}
