package com.carson.signsystem.home.view;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMapException;
import com.amap.api.maps2d.AMapUtils;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.Circle;
import com.amap.api.maps2d.model.CircleOptions;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.LatLngBounds;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.carson.signsystem.R;
import com.carson.signsystem.utils.Constants;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@Route(path = Constants.ACTIVITY_SIGN)
public class SignActivity extends AppCompatActivity implements AMap.OnCameraChangeListener,
        AMap.OnMapLoadedListener, AMapLocationListener {

    private static final String TAG = "SignActivity";

    private static final CameraPosition GUANGZHOU = new CameraPosition.Builder()
            .target(new LatLng(Constants.GUANGZHOU_LOCATION[0], Constants.GUANGZHOU_LOCATION[1]))
            .zoom(18).bearing(0).tilt(30).build();

    @BindView(R.id.map_view)
    MapView map_view;
    @BindView(R.id.btn_sign)
    Button btn_sign;
    @BindView(R.id.iv_icon_locate)
    ImageView iv_locate;
    @BindView(R.id.tv_sign_time)
    TextView tv_sign_time;
    @BindView(R.id.tv_sign_address)
    TextView tv_sign_address;
    @BindView(R.id.check_in_layout)
    ConstraintLayout check_in_layout;
    @BindView(R.id.iv_back)
    ImageView iv_back;

    // 地图控制对象
    private AMap aMap;
    // 范围圆圈对象
    private Circle mCircle;
    // 定位对象
    private AMapLocationClient mLocationClient;
    // 定位对象属性
    private AMapLocationClientOption mLocationOption;
    private Marker mLocationMarker;
    // 经纬度对象 分别对应签到点以及定位点
    private LatLng checkinPoint, mLocation, mSchoolLocation, testLocation;
    // 判断是否正在定位中
    private boolean isLocateAction;
    // 判断是否已经签到了
    private boolean isAlreadyCheckin;

    private ObjectAnimator collapseAnimator, expandAnimator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_sign);

//        getWindow().getDecorView().setFitsSystemWindows(true);
//        getWindow().getDecorView().setBackgroundColor(Color.TRANSPARENT);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        ARouter.getInstance().inject(this);
        ButterKnife.bind(this);

        checkRequirePermission();

        map_view.onCreate(savedInstanceState);

        // 到时联网获取
        isAlreadyCheckin = false;

        initMap();

        if (!isAlreadyCheckin) {
            startExpandAnimation();
        }

        initLocation();

        startLocation();

    }

    private void startExpandAnimation() {
//        ConstraintSet set = new ConstraintSet();
        check_in_layout.setVisibility(View.VISIBLE);
        expandAnimator = ObjectAnimator
                .ofFloat(check_in_layout, "translationY",
                        500, 0);
        expandAnimator.setDuration(1000);
        expandAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        expandAnimator.start();
    }

    private void startEllipseAnimation() {
        collapseAnimator = ObjectAnimator
                .ofFloat(check_in_layout, "translationY", 0,
                        500);
        collapseAnimator.setDuration(1000);
        collapseAnimator.setInterpolator(new AccelerateInterpolator());
        collapseAnimator.start();
        collapseAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                check_in_layout.setVisibility(View.GONE);
            }
        });
    }

    private void checkRequirePermission() {
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    /**
     * 初始化地图
     */
    private void initMap() {
        if (aMap == null) {
            aMap = map_view.getMap();
        }
        aMap.getUiSettings().setZoomControlsEnabled(false);
        aMap.setOnCameraChangeListener(this);
        aMap.setOnMapLoadedListener(this);
    }

    /**
     * 初始化定位
     */
    private void initLocation() {
        // 初始化client
        mLocationClient = new AMapLocationClient(getApplicationContext());
        // 设置定位监听
        mLocationClient.setLocationListener(this);
        // 设置学校定位
        mSchoolLocation = new LatLng(Constants.GDUFS_LOCATION[0], Constants.GDUFS_LOCATION[1]);
        // 测试定位
//        testLocation = new LatLng(23.063545, 113.395317);
//        Log.e(TAG, "test distance = " + AMapUtils.calculateLineDistance(mSchoolLocation, testLocation));
    }

    /**
     * 开始定位
     */
    @OnClick(R.id.iv_icon_locate)
    public void startLocation() {
        // 设置定位参数
        mLocationClient.setLocationOption(getOption());
        // 启动定位
        mLocationClient.startLocation();
    }

    /**
     * 销毁定位
     */
    private void destroyLocation() {
        if (null != mLocationClient) {
            mLocationClient.onDestroy();
            mLocationClient = null;
        }
    }

    /**
     * 设置定位参数
     * @return 定位参数类
     */
    private AMapLocationClientOption getOption() {
        AMapLocationClientOption option = new AMapLocationClientOption();
        // 设置高精度模式
        option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        // 设置请求超时时间 默认为30s 在仅设备模式下无效
        option.setHttpTimeOut(30000);
        // 是否返回逆地理位置信息 默认为true
        option.setNeedAddress(true);
        // 设置是否返回缓存位置
        option.setLocationCacheEnable(false);
        // 是否单次定位
        option.setOnceLocation(true);
        return option;

    }

    @OnClick(R.id.iv_back)
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_scale_in, R.anim.fade_right_out);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "ok");
            initLocation();
            startLocation();
        }
    }

    /**
     * 返回定位结果回调
     * @param aMapLocation 定位结果
     */
    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        Log.e(TAG, "onLocationChanged");
        if (aMapLocation != null
                && aMapLocation.getErrorCode() == 0) {
            // 这是目前定位的位置
            mLocation = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
            String address = aMapLocation.getProvince() + aMapLocation.getCity() + aMapLocation.getDistrict() + aMapLocation.getStreet();
            tv_sign_address.setText(address);
            checkinPoint = mLocation;
            // 判断距离是否大于500米
            float distance = AMapUtils.calculateLineDistance(mLocation, mSchoolLocation);
//            float distance = AMapUtils.calculateLineDistance(testLocation, mSchoolLocation);
            isLocateAction = true;
            if (distance > 500) {
                // 超出
                try {
                    LatLngBounds latLngBounds = createBounds(mLocation, mSchoolLocation);
                    aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 10), 1000L, null);
                } catch (AMapException e) {
                    e.printStackTrace();
                }
            } else {
                aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mSchoolLocation, 16f));
            }

            if (mCircle != null) {
                mCircle.setCenter(mSchoolLocation);
            } else {
                mCircle = aMap.addCircle(new CircleOptions().center(mSchoolLocation)
                        .strokeColor(getResources().getColor(R.color.colorAccent))
                        .radius(500).strokeWidth(5));
            }

            if (mLocationMarker == null) {
                mLocationMarker = aMap.addMarker(new MarkerOptions()
                        .position(mLocation)
                        .anchor(0.5f, 0.5f));
            } else {
                mLocationMarker.setPosition(mLocation);
            }
        } else {
            assert aMapLocation != null;
            // 定位失败
            String errText = "定位失败, " + aMapLocation.getErrorCode() + ": " + aMapLocation.getErrorInfo();
            Log.e(TAG, errText);
        }
    }

    /**
     * 地图移动过程回调
     * @param cameraPosition
     */
    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        Log.e(TAG, "onCameraChange");
//        Log.e(TAG, "camera zoom = "+ cameraPosition.zoom);
//
//        targetZoom = cameraPosition.zoom;
//        if (targetZoom > originZoom || originZoom < 16f) {
//            float scale = targetZoom / originZoom;
//
//        }
    }

    /**
     * 地图移动结束回调
     * 在这里判断定位范围是否超过限定范围
     * @param cameraPosition
     */
    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {
        Log.e(TAG, "onCameraChangeFinish");
        Log.e(TAG, "position = " + cameraPosition.target.latitude + " " + cameraPosition.target.longitude);
        if (isLocateAction) isLocateAction = false;

        if (mCircle != null) {
            if (mCircle.contains(mLocation)) {
                checkinPoint = mLocation;
            } else {
//            // 测试
//            if (mCircle.contains(testLocation)) {
//                checkinPoint = testLocation;
//            } else {
                try {
                    LatLngBounds latLngBounds = createBounds(mLocation, mSchoolLocation);

//                    aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 10), 1000L, null);
                } catch (AMapException e) {
                    e.printStackTrace();
                }
            }
        } else {
            startLocation();
        }
    }

    /**
     * 地图加载完成的回调
     */
    @Override
    public void onMapLoaded() {
        Log.e(TAG, "onMapLoaded");
        addMarkerInScreenCenter();
    }

    /**
     * 添加学校marker标记 且判断是否在范围内
     */
    private void addMarkerInScreenCenter() {
//        LatLng mLatLng = aMap.getCameraPosition().target;

//        Point screenPoint = aMap.getProjection().toScreenLocation(mLatLng);
//        MarkerOptions options = new MarkerOptions();
        // 地图上的学校标记
        aMap.addMarker(new MarkerOptions()
                .position(mSchoolLocation)
                .anchor(0.5f, 0.5f)
                .title("广东外语外贸大学")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_school)));
//        float distance = AMapUtils.calculateLineDistance(mLatLng, mSchoolLocation);
        // 设置Marker在屏幕上 不跟随地图移动
//        schoolMarker.setPositionByPixels(screenPoint.x, screenPoint.y);
        aMap.addCircle(new CircleOptions().center(mSchoolLocation).strokeColor(getResources().getColor(R.color.colorAccent))
                .radius(500).strokeWidth(5));

        // test
//        aMap.addMarker(new MarkerOptions()
//                .position(testLocation).anchor(0.5f, 0.5f));

    }

    /**
     * 签到
     */
    @OnClick(R.id.btn_sign)
    public void checkIn() {
        if (!isAlreadyCheckin) {
            float distance = AMapUtils.calculateLineDistance(mSchoolLocation, mLocation);
//            float distance = AMapUtils.calculateLineDistance(mSchoolLocation, testLocation);
            if (distance > 500) {
                Toast.makeText(this, "已超出签到范围", Toast.LENGTH_LONG).show();
            } else {
                if (checkinPoint != null) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日HH:mm");
                    String date = sdf.format(new Date());
                    isAlreadyCheckin = true;
                    mLocationMarker.setTitle("签到");
                    mLocationMarker.setSnippet(date);
                    startEllipseAnimation();
//                    checkinMarker = aMap.addMarker(new MarkerOptions().position(checkinPoint).title("签到").snippet(date));
                    // 签到成功提示
                    Toast.makeText(this, "签到成功", Toast.LENGTH_LONG).show();
                } else {
                    startLocation();
                    // 定位中提示
                }
            }

        } else {
            // 已签到提示
        }
    }

    /**
     * 创建范围
     */
    private LatLngBounds createBounds(LatLng latLngA, LatLng latLngB) throws AMapException {
        LatLng northeastLatlng;
        LatLng southwestLatlng;

        double topLat, topLng, bottomLat, bottomLng;
        if (latLngA.latitude >= latLngB.latitude) {
            topLat = latLngA.latitude;
            bottomLat = latLngB.latitude;
        } else {
            topLat = latLngB.latitude;
            bottomLat = latLngA.latitude;
        }
        if (latLngA.longitude >= latLngB.longitude) {
            topLng = latLngA.longitude;
            bottomLng = latLngB.longitude;
        } else {
            topLng = latLngB.longitude;
            bottomLng = latLngA.longitude;
        }
        northeastLatlng = new LatLng(topLat, topLng);
        southwestLatlng = new LatLng(bottomLat, bottomLng);

        return new LatLngBounds(southwestLatlng, northeastLatlng);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 重新绘制加载地图
        map_view.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 暂停绘制
        map_view.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // 在意外退出后 从outState中获取并保存地图当前的状态
        map_view.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        map_view.onDestroy();
        destroyLocation();
        if (null != collapseAnimator) {
            collapseAnimator.removeAllListeners();
            collapseAnimator = null;
        }
        if (null != expandAnimator) {
            expandAnimator = null;
        }
    }

}
