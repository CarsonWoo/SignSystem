package com.carson.signsystem.home.view;

import android.graphics.Color;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

@Route(path = "/app/ResetPasswordActivity")
public class ResetPasswordActivity extends AppCompatActivity {

    @BindView(R.id.new_password_input)
    EditText new_password;

    @BindView(R.id.new_password_again_input)
    EditText new_password_again;

    @BindView(R.id.reset_password_submit)
    Button reset_password;

    @Autowired
    public String job_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        setContentView(R.layout.activity_reset_password);

        ARouter.getInstance().inject(this);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.reset_password_submit)
    public void reset_password(){
        String password = new_password.getText().toString();
        String password_again = new_password_again.getText().toString();

        if (password.equals(password_again)){
//            Toast.makeText(ResetPasswordActivity.this,"密码输入正确，工号为"+ job_number,Toast.LENGTH_LONG).show();
            String url = Constants.ADDRESS + "/employee_changepwd";

            FormBody formBody = new FormBody.Builder()
                    .add("job_number",job_number)
                    .add("new_password",password)
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
                        Looper.prepare();
                        Toast.makeText(ResetPasswordActivity.this,"修改成功",Toast.LENGTH_LONG).show();
                        Looper.loop();

                        ARouter.getInstance()
                                .build(Constants.ACTIVITY_LOGIN)
                                .withTransition(R.anim.fade_right_in,R.anim.fade_scale_out)
                                .navigation(ResetPasswordActivity.this);
                    }
                    else{
                        //UI操作需要在主线程，包括Toast
                        Looper.prepare();
                        Toast.makeText(ResetPasswordActivity.this,"修改失败，请联系工作人员", Toast.LENGTH_LONG).show();
//                    Toast.makeText(LoginActivity.this,"返回数据为："+ code, Toast.LENGTH_LONG).show();
                        Looper.loop();
                    }
                }

                @Override
                public void onFailure(Call call, IOException e) {
                    Looper.prepare();
                    Toast.makeText(ResetPasswordActivity.this,"网络通信发生错误",Toast.LENGTH_LONG).show();
                    Looper.loop();
                }
            });
        }
        else{
            Toast.makeText(ResetPasswordActivity.this,"两次输入的密码不一致，请重试",Toast.LENGTH_LONG).show();
        }


    }
}
