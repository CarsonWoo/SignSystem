package com.carson.signsystem.home.view;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.carson.signsystem.R;
import com.carson.signsystem.utils.Constants;

@Route(path = Constants.ACTIVITY_HOME)
public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ARouter.getInstance().inject(this);

        // 判断是否存在缓存 这里要结合登录界面进行储存
        SharedPreferences sharedPreferences = getSharedPreferences("SignSystem", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");
        String password = sharedPreferences.getString("password", "");

        if (username.isEmpty() || password.isEmpty()) {
            ARouter.getInstance().build(Constants.ACTIVITY_VERIFY).withTransition(R.anim.fade_right_in,
                    R.anim.fade_scale_out).navigation(HomeActivity.this);
            finishAfterTransition();
        }

    }
}
