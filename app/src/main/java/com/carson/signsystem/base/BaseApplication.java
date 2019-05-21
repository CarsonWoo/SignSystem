package com.carson.signsystem.base;

import android.app.Application;

import com.alibaba.android.arouter.launcher.ARouter;
import com.carson.signsystem.BuildConfig;

/**
 * Created by 84594 on 2019/5/6.
 */

public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
//        if ()
        initRouter(this);
    }

    private void initRouter(Application application) {
        if (BuildConfig.DEBUG) {
            ARouter.openLog(); // 打印日志
            ARouter.openDebug(); // 打开调试模式 线上版本需要关闭 否则有安全风险
        }
        ARouter.init(application);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        ARouter.getInstance().destroy();
    }
}
