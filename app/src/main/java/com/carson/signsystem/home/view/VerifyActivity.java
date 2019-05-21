package com.carson.signsystem.home.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
        setContentView(R.layout.activity_verify);

        ARouter.getInstance().inject(this);
        ButterKnife.bind(this);

    }

    @OnClick({R.id.iv_staff, R.id.tv_staff})
    public void onStaffLogin() {
        ARouter.getInstance()
                .build(Constants.ACTIVITY_LOGIN)
                .withString("identity", "staff")
                .withTransition(R.anim.fade_right_in, R.anim.fade_scale_out)
                .navigation(VerifyActivity.this);
    }

    @OnClick({R.id.iv_manager, R.id.tv_manager})
    public void onManagerLogin() {
        ARouter.getInstance()
                .build(Constants.ACTIVITY_LOGIN)
                .withString("identity", "manager")
                .withTransition(R.anim.fade_right_in, R.anim.fade_scale_out)
                .navigation(VerifyActivity.this);
    }
}
