package com.carson.signsystem.home.view;

import android.graphics.Rect;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.carson.signsystem.R;
import com.carson.signsystem.home.adapter.StaffListAdapter;
import com.carson.signsystem.home.model.SigningViewData;
import com.carson.signsystem.home.model.StaffListData;
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

@Route(path = "/app/StaffCheckActivity")
public class StaffCheckActivity extends AppCompatActivity {

    @BindView(R.id.staff_listview)
    RecyclerView staffList;

    private ArrayList<StaffListData> mDataList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_check);

        ARouter.getInstance().inject(this);
        ButterKnife.bind(this);


        mDataList = new ArrayList<>();
        loadData();
        staffList.setAdapter(new StaffListAdapter(this,mDataList));
        staffList.setLayoutManager(new LinearLayoutManager(this));
        staffList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, int itemPosition, @NonNull RecyclerView parent) {
                super.getItemOffsets(outRect, itemPosition, parent);
            }
        });
    }

    private void loadData() {
        String url = Constants.ADDRESS + "/all_employee_info";
        HttpUtil.sendOkHttpRequest(url, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("通讯失败","失败原因："+ e.toString());
                Looper.prepare();
                Toast.makeText(StaffCheckActivity.this,"通讯失败，原因为："+e.toString(),Toast.LENGTH_LONG).show();
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                JsonParser parser = new JsonParser();
                JsonArray jsonArray = parser.parse(responseData).getAsJsonArray();
                Gson gson = new Gson();
                for (JsonElement staff:jsonArray){
                    StaffListData items = gson.fromJson(staff, StaffListData.class);
                    mDataList.add(items);
                }
    }
}); }

}
