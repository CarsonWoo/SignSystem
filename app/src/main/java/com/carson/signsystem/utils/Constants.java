package com.carson.signsystem.utils;

/**
 * Created by 84594 on 2019/5/12.
 */

public class Constants {

    // 主工程HomeActivity 如下见名知意 不再赘述
    public static final String ACTIVITY_SIGN = "/app/SignActivity";
    public static final String ACTIVITY_VERIFY = "/app/VerifyActivity";
    public static final String ACTIVITY_LOGIN = "/app/LoginActivity";
    public static final String ACTIVITY_STAFF = "/app/StaffActivity";


    // 常量
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_IDENTITY = "identity";
    public static final String IDENTITY_STAFF = "staff";
    public static final String IDENTITY_MANAGER = "manager";

    //服务器地址
    public static final String ADDRESS = "http://129.204.22.24:1234";


    // 定位常量
    public static final double[] GUANGZHOU_LOCATION = {23.0632, 113.1553};
    public static final double[] GDUFS_LOCATION = {23.063802447019274, 113.39803985960262};
}
