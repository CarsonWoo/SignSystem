package com.carson.signsystem.home.view;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.carson.signsystem.R;
import com.carson.signsystem.login.view.LoginActivity;
import com.carson.signsystem.utils.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


@Route(path = "/app/CompanyActivity")
public class CompanyActivity extends AppCompatActivity {

    @BindView(R.id.signing_release)
    View signing_release;

    @BindView(R.id.information_entry_exit)
    Button exit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company);


        ARouter.getInstance().inject(this);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.signing_release)
    public void setSigning_release(){
        ARouter.getInstance()
                .build("/app/SigningActivity")
                .withTransition(R.anim.fade_right_in,R.anim.fade_scale_out)
                .navigation(CompanyActivity.this);
    }

//    //没做出来，算了
////    @OnClick(R.id.signing_check)
////    public void setSigning_check(){
////        ARouter.getInstance()
////                .build("/app/SigningViewActivity")
////                .withTransition(R.anim.fade_right_in,R.anim.fade_scale_out)
////                .navigation(CompanyActivity.this);
//    }

    @OnClick(R.id.information_entry_exit)
    public void exit(){
        ARouter.getInstance()
                .build(Constants.ACTIVITY_LOGIN)
                .withString("identity","manager")
                .withTransition(R.anim.fade_right_out,R.anim.fade_scale_in)
                .navigation(CompanyActivity.this);
    }
}
