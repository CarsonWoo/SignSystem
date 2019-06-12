package com.carson.signsystem.home.view;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.carson.signsystem.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@Route(path = "/app/InformationEntryActivity")
public class InformationEntryActivity extends AppCompatActivity {

    @BindView(R.id.information_entry_submit)
    Button sumbit;

    @BindView(R.id.information_entry_username_input)
    EditText username;

    @BindView(R.id.information_entry_job_number_input)
    EditText number;

    @BindView(R.id.information_entry_apartment_choose)
    Spinner department_choose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        setContentView(R.layout.activity_information_entry);

        ARouter.getInstance().inject(this);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.information_entry_submit)
    public void submitEmployee(){
        String name = username.getText().toString();
        String job_number = number.getText().toString();

    }
}
