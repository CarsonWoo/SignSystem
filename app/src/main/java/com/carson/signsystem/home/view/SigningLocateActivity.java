package com.carson.signsystem.home.view;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.CircleOptions;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.carson.signsystem.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@Route(path = "/app/SigningLocateActivity")
public class SigningLocateActivity extends AppCompatActivity implements LocationSource, AMapLocationListener {

    private final static String TAG = "SigningLocateActivity";

    @BindView(R.id.signing_locate_mapview)
    MapView mapView;

    @BindView(R.id.signing_locate_address)
    TextView address;

    @BindView(R.id.signing_locate_address_range_choose)
    Spinner range_choose;


    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;

    public AMapLocationClientOption mLocationClientOption = null;

    private OnLocationChangedListener mListener = null;

    private AMap aMap;

    //最终上传的地址
    String simpleAddress = "";
    //选定的地址范围
    String range = "";

    //定位点的经纬度
    double myLatitude = 0;
    double myLongtitude = 0;
//    //获取数据源
//    String[] rangeItem = getResources().getStringArray(R.array.address_range);
//    //绑定数据源
//    ArrayAdapter<String> range_choose_adapter = new ArrayAdapter<>(SigningLocateActivity.this,R.layout.support_simple_spinner_dropdown_item,rangeItem);


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        setContentView(R.layout.activity_signing_locate);

        ARouter.getInstance().inject(this);
        ButterKnife.bind(this);


        mapView.onCreate(savedInstanceState);

        //获取数据源
        String[] range = getResources().getStringArray(R.array.address_range);
        //绑定数据源
        ArrayAdapter<String> range_choose_adapter = new ArrayAdapter<>(SigningLocateActivity.this,R.layout.support_simple_spinner_dropdown_item,range);
        //将适配器绑定spinner
        range_choose.setAdapter(range_choose_adapter);

        initMap();

        locate();

        chooseRange();
    }

    private void chooseRange() {
        range_choose.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                range = adapterView.getItemAtPosition(i).toString();
//                Toast.makeText(SigningLocateActivity.this,"你选择的范围是："+ range,Toast.LENGTH_LONG).show();
//                if (aMap == null){
//                    aMap = mapView.getMap();
//                }
                MarkerOptions myPoint = new MarkerOptions()
                        .position(new LatLng(myLatitude,myLongtitude));
                CircleOptions rangeCircle = new CircleOptions()
                        .center(new LatLng(myLatitude,myLongtitude))
                        .fillColor(Color.argb(64,0,0,255))
                        .strokeColor(Color.argb(32,1,1,1))
                ;
                //根据选择范围画圆
                if (range.equals("1km")){
//                    Log.e("现在选定范围","1km");
                    aMap.clear();
                    rangeCircle.radius(1000);
                    aMap.addMarker(myPoint);
                    aMap.addCircle(rangeCircle);
                }
                if (range.equals("2km")){
//                    Log.e("现在选定范围","2km");
                    aMap.clear();
                    rangeCircle.radius(2000);
                    aMap.addMarker(myPoint);
                    aMap.addCircle(rangeCircle);
//                    aMap.addCircle(new CircleOptions()
//                            .center(new LatLng(myLatitude,myLongtitude))
//                            .fillColor(Color.argb(64,0,0,255))
//                            .strokeColor(Color.argb(255,1,1,1))
//                            .radius(2000));
                }
                if (range.equals("3km")){
//                    Log.e("现在选定范围","3km");
                    aMap.clear();
                    rangeCircle.radius(3000);
                    aMap.addMarker(myPoint);
                    aMap.addCircle(rangeCircle);
//                    aMap.addCircle(new CircleOptions()
//                            .center(new LatLng(myLatitude,myLongtitude))
//                            .fillColor(Color.argb(64,0,0,255))
//                            .strokeColor(Color.argb(255,1,1,1))
//                            .radius(3000));
                }
                if (range.equals("4km")){
//                    Log.e("现在选定范围","4km");
                    aMap.clear();
                    rangeCircle.radius(4000);
                    aMap.addMarker(myPoint);
                    aMap.addCircle(rangeCircle);
//                    aMap.addCircle(new CircleOptions()
//                            .center(new LatLng(myLatitude,myLongtitude))
//                            .fillColor(Color.argb(64,0,0,255))
//                            .strokeColor(Color.argb(255,1,1,1))
//                            .radius(4000));
                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                range = "1km";
                aMap.addCircle(new CircleOptions()
                    .center(new LatLng(myLatitude,myLongtitude))
                    .fillColor(Color.argb(64,0,0,255))
                    .strokeColor(Color.argb(255,1,1,1))
                        .radius(1000));
//                Toast.makeText(SigningLocateActivity.this,"默认范围为：" + range,Toast.LENGTH_LONG).show();
            }
        });
    }

    private void locate() {
        

    }

    private void initMap() {
        if (aMap == null) {
            aMap = mapView.getMap();
        }
        MyLocationStyle myLocationStyle;
        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.showMyLocation(true);//显示蓝点

        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW);//连续定位、且将视角移动到地图中心点，定位蓝点跟随设备移动。（1秒1次定位）

        myLocationStyle.radiusFillColor(100);
        myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。

        aMap.setLocationSource(this);
//        aMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
//aMap.getUiSettings().setMyLocationButtonEnabled(true);设置默认定位按钮是否显示，非必需设置。

        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。

        aMap.addCircle(new CircleOptions()
                .center(new LatLng(myLatitude,myLongtitude))
                .fillColor(Color.argb(64,0,0,255))
                .strokeColor(Color.argb(255,1,1,1))
                .radius(1000));

    }


    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
//        Log.e(TAG, "onLocationChanged");
        if (aMapLocation != null) {
//            Log.e(TAG, "aMapLoccation !- null");
            if (aMapLocation.getErrorCode() == 0) {
//                Log.e(TAG, "aMapLocation.getErrorCode?.0");
//                //定位成功回调信息，设置相关消息
                aMapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见官方定位类型表
                myLatitude = aMapLocation.getLatitude();//获取纬度
                myLongtitude = aMapLocation.getLongitude();//获取经度
                aMapLocation.getAccuracy();//获取精度信息
//


//                    //设置缩放级别
//                    mapView.moveCamera(CameraUpdateFactory.zoomTo(17));
                //将地图移动到定位点
                simpleAddress = aMapLocation.getProvince() + aMapLocation.getCity() + aMapLocation.getDistrict() + aMapLocation.getStreet();
                address.setText(simpleAddress);//显示地方
                Toast.makeText(SigningLocateActivity.this,aMapLocation.getAddress(),Toast.LENGTH_LONG).show();
//                MyLocationStyle myLocationStyle;
//                myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
//                myLocationStyle.showMyLocation(true);//显示蓝点
//                myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);//定位一次且将视角移动到中心点
                mListener.onLocationChanged(aMapLocation);//显示小蓝点
                aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(aMapLocation.getLatitude(),aMapLocation.getLongitude()),13));

//                    //点击定位按钮 能够将地图的中心移动到定位点
//                    mListener.onLocationChanged(aMapLocation);
//
            }
        } else {
            //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
            Log.e("AmapError", "location Error, ErrCode:"
                    + aMapLocation.getErrorCode() + ", errInfo:"
                    + aMapLocation.getErrorInfo());
            Toast.makeText(getApplicationContext(), "定位失败", Toast.LENGTH_LONG).show();
        }
//        }

    }
    //响应定位时的方法
    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;

        mLocationClient = new AMapLocationClient(this);

        mLocationClient.setLocationListener(this);

        mLocationClientOption = new AMapLocationClientOption();
        //定位场景为签到
//        mLocationClientOption.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.SignIn);

        //设置定位模式为高精度
        mLocationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);

        //仅定位一次试试
        mLocationClientOption.setOnceLocation(true);
        //最新的一次定位  即 3秒内最精确的定位
//        mLocationClientOption.setOnceLocationLatest(true);

        //定位用户 绑定 定位选项
        mLocationClient.setLocationOption(mLocationClientOption);


        //关闭一次再启动比较稳健
//        mLocationClient.stopLocation();
        mLocationClient.startLocation();
    }
    //终止定位的响应方法
    @Override
    public void deactivate() {
            mListener = null;
            if (mLocationClient != null){
                mLocationClient.stopLocation();
                mLocationClient.onDestroy();
            }
            mLocationClient = null;
    }

    @OnClick(R.id.signing_locate_address_confirm)
    public void setSigning(){
        SigningLocateActivity.this.finish();

        ARouter.getInstance()
                .build("/app/SigningActivity")
                .withString("address",simpleAddress)
                .withString("range",range)
                .navigation(SigningLocateActivity.this);
    }

//    @OnItemClick(R.id.signing_locate_address_range_choose)
//    public void rangeShow(){
//        String rangeString = range_choose_adapter.getItem().toString();
//    }
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
}
