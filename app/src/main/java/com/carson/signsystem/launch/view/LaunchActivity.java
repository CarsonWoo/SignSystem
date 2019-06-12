package com.carson.signsystem.launch.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.carson.signsystem.R;
import com.carson.signsystem.utils.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * 即将作为启动活动 可最后再写
 */
public class LaunchActivity extends AppCompatActivity {

    @BindView(R.id.iv_icon_gdufs)
    ImageView iv_launch_icon;
    @BindView(R.id.tv_launch_text)
    TextView tv_launch_text;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        setContentView(R.layout.activity_launch);

         sharedPreferences = getSharedPreferences("SignSystem", MODE_PRIVATE);

        ButterKnife.bind(this);

        /**
         * 需要移植成mvp结构
         */
        initialAnimation();

    }

    private void initialAnimation() {
//        iv_launch_icon.setVisibility(View.VISIBLE);
//        tv_launch_text.setVisibility(View.VISIBLE);

        ObjectAnimator alpha_iv = ObjectAnimator.ofFloat(iv_launch_icon, "alpha", 0.0f, 1.0f);
        alpha_iv.setInterpolator(new LinearInterpolator());
        alpha_iv.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                iv_launch_icon.setVisibility(View.VISIBLE);
            }
        });
        alpha_iv.setDuration(500);

        ObjectAnimator alpha_tv = ObjectAnimator.ofFloat(tv_launch_text, "alpha", 0.0f, 1.0f);
        alpha_tv.setInterpolator(new LinearInterpolator());
        alpha_tv.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                tv_launch_text.setVisibility(View.VISIBLE);
            }
        });
        alpha_tv.setDuration(500);

        ObjectAnimator translate_iv = ObjectAnimator.ofFloat(iv_launch_icon, "translationY", 0, -100);
        translate_iv.setInterpolator(new AccelerateDecelerateInterpolator());
        translate_iv.setDuration(1500);

        ObjectAnimator rotate_iv = ObjectAnimator.ofFloat(iv_launch_icon, "rotation", -8, 8, -5, 5, -2, 2, 0);
        rotate_iv.setInterpolator(new AccelerateInterpolator());
        rotate_iv.setDuration(500);

        AnimatorSet anim_set = new AnimatorSet();
        anim_set.play(alpha_tv).after(translate_iv).after(rotate_iv).after(alpha_iv);
//        anim_set.setDuration(3000);

        anim_set.start();

        anim_set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                new Handler().postDelayed(() -> verifyLoginState(), 500);
            }
        });
    }

    private void verifyLoginState() {
        // 判断是否存在缓存 这里要结合登录界面进行储存
        String username = sharedPreferences.getString(Constants.KEY_USERNAME, "");
        String password = sharedPreferences.getString(Constants.KEY_PASSWORD, "");

        if (username.isEmpty() || password.isEmpty()) {
            ARouter.getInstance().build(Constants.ACTIVITY_VERIFY).withTransition(R.anim.fade_right_in,
                    R.anim.fade_scale_out).navigation(LaunchActivity.this);
            finishAfterTransition();
        } else {
            verifyUserIdentity();
        }
    }

    private void verifyUserIdentity() {
        String identity = sharedPreferences.getString(Constants.KEY_IDENTITY, Constants.IDENTITY_STAFF);

        if (identity.equals(Constants.IDENTITY_STAFF)) {
            // 跳转到staff对应的activity
        } else {
            // 跳转到manager对应的activity
        }
    }
}
