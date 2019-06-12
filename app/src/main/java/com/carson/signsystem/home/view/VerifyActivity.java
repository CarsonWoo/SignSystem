package com.carson.signsystem.home.view;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.carson.signsystem.R;
import com.carson.signsystem.utils.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@Route(path = Constants.ACTIVITY_VERIFY)
public class VerifyActivity extends AppCompatActivity {

    // BufferKnife 注解
    @BindView(R.id.iv_staff)
    ImageView iv_staff;
    @BindView(R.id.iv_manager)
    ImageView iv_manager;
    @BindView(R.id.tv_staff)
    TextView tv_staff;
    @BindView(R.id.tv_manager)
    TextView tv_manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        setContentView(R.layout.activity_verify);

        // ARouter 绑定
        ARouter.getInstance().inject(this);
        // BufferKnife绑定
        ButterKnife.bind(this);

    }

    @OnClick({R.id.iv_staff, R.id.tv_staff})
    public void onStaffLogin() {
        // 路由定向
        ARouter.getInstance()
                .build(Constants.ACTIVITY_LOGIN)
                // 传参
                .withString("identity", "staff")
                .withString("code","0")
                // 进行动画
                .withTransition(R.anim.fade_right_in, R.anim.fade_scale_out)
                .navigation(VerifyActivity.this);
    }

    @OnClick({R.id.iv_manager, R.id.tv_manager})
    public void onManagerLogin() {
        ARouter.getInstance()
                .build(Constants.ACTIVITY_LOGIN)
                .withString("identity", "manager")
                .withString("code","1")
                .withTransition(R.anim.fade_right_in, R.anim.fade_scale_out)
                .navigation(VerifyActivity.this);
    }
}
