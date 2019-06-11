package com.carson.signsystem.home.view;

import android.content.Context;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.carson.signsystem.R;
import com.carson.signsystem.utils.Constants;
import com.carson.signsystem.utils.HttpUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Response;

@Route(path = "/app/SigningViewActivity")
public class SigningViewActivity extends AppCompatActivity {


    @BindView(R.id.signing_view)
    ListView SigningList;

    ArrayList<SigningItem> result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signing_view);

        ARouter.getInstance().inject(this);
        ButterKnife.bind(this);



        loadData();
        SigningItemAdapter adapter = new SigningItemAdapter(SigningViewActivity.this,result);
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
                result = new ArrayList<>();
                for (JsonElement signings:jsonArray){
                    SigningItem items = gson.fromJson(signings,SigningItem.class);
                    result.add(items);
                }
//                result = gson.fromJson(responseData,new TypeToken<SigningItem>(){}.getType());


//                List<SigningItem> item = gson.fromJson(responseData,new TypeToken<List<SigningItem>>(){}.getType());
//                for (SigningItem item1 : item){

//                    time.setText(item1.getTime_begin() + item1.getTime_end());
//                    date.setText(item1.getDate_begin() + item1.getDate_end());
//                    place.setText(item1.getLocation());
//                }
            }
        });
    }

    public class SigningItem{
        private String id,location,range,date_begin,date_end,time_begin,time_end;

        public void setId(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getLocation() {
            return location;
        }

        public void setRange(String range) {
            this.range = range;
        }

        public String getRange() {
            return range;
        }

        public void setDate_begin(String date_begin) {
            this.date_begin = date_begin;
        }

        public String getDate_begin() {
            return date_begin;
        }

        public void setDate_end(String date_end) {
            this.date_end = date_end;
        }

        public String getDate_end() {
            return date_end;
        }

        public void setTime_begin(String time_begin) {
            this.time_begin = time_begin;
        }

        public String getTime_begin() {
            return time_begin;
        }

        public void setTime_end(String time_end) {
            this.time_end = time_end;
        }

        public String getTime_end() {
            return time_end;
        }
    }

    public class SigningItemAdapter extends BaseAdapter {
        private List<SigningItem> signingItems;
        private LayoutInflater inflater;

        public  SigningItemAdapter(Context context,List<SigningItem> signings){
            signingItems = signings;
            inflater = LayoutInflater.from(context);
        }


        @Override
        public int getCount() {
            return signingItems.size();
        }

        @Override
        public Object getItem(int i) {
            return signingItems.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View myView = inflater.inflate(R.layout.signing_view_item,null);
            SigningItem myItem = (SigningItem) getItem(i);

            TextView place = (TextView) findViewById(R.id.signing_item_place_name);
            TextView date = (TextView) findViewById(R.id.signing_item_date_range);
            TextView time = (TextView) findViewById(R.id.signing_item_time_range);
//            @BindView(R.id.signing_item_place_name)
//            TextView place;
//            @BindView(R.id.signing_item_date_range)
//            TextView date;
//            @BindView(R.id.signing_item_time_range)
//            TextView time;

            time.setText(myItem.getTime_begin() + myItem.getTime_end());
            date.setText(myItem.getDate_begin() + myItem.getDate_end());
            place.setText(myItem.getLocation());
            return myView;
        }



    }
}
