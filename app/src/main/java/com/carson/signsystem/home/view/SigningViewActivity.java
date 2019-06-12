package com.carson.signsystem.home.view;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.carson.signsystem.R;
import com.carson.signsystem.home.adapter.SigningItemAdapter;
import com.carson.signsystem.home.model.SigningViewData;
import com.carson.signsystem.utils.Constants;
import com.carson.signsystem.utils.HttpUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Response;

@Route(path = "/app/SigningViewActivity")
public class SigningViewActivity extends AppCompatActivity {


    @BindView(R.id.signing_view)
    RecyclerView SigningList;

    private ArrayList<SigningViewData> mDataList;
    private SigningItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        setContentView(R.layout.activity_signing_view);

        ARouter.getInstance().inject(this);
        ButterKnife.bind(this);

        mDataList = new ArrayList<>();
        loadData();
        adapter = new SigningItemAdapter(this, mDataList);
        SigningList.setLayoutManager(new LinearLayoutManager(this));
        SigningList.setAdapter(adapter);
    }

    private void loadData() {
        String url = Constants.ADDRESS + "/check_sign_in";
        HttpUtil.sendOkHttpRequest(url, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("通讯失败","失败原因："+ e.toString());
                Looper.prepare();
                Toast.makeText(SigningViewActivity.this,"通讯失败，原因为："+e.toString(),Toast.LENGTH_LONG).show();
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                JsonParser parser = new JsonParser();
                JsonArray jsonArray = parser.parse(responseData).getAsJsonArray();
                Gson gson = new Gson();
                for (JsonElement signings:jsonArray){
                    SigningViewData items = gson.fromJson(signings, SigningViewData.class);
                    mDataList.add(items);
                }
                runOnUiThread(() -> adapter.notifyDataSetChanged());
//                adapter.notifyDataSetChanged();
            }
        });
    }

}
