package com.carson.signsystem.login.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.carson.signsystem.R;
import com.carson.signsystem.utils.Constants;

@Route(path = Constants.ACTIVITY_LOGIN)
public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ARouter.getInstance().inject(this);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // 返回时引用动画
        overridePendingTransition(R.anim.fade_scale_in, R.anim.fade_right_out);
    }
}