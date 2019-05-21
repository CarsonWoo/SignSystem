package com.carson.signsystem.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.carson.signsystem.R;


/**
 * 即将作为启动活动 可最后再写
 */
public class LaunchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
    }
}
