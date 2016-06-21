package com.wjwu.wpmain.net;

import com.wjwu.wpmain.uzwp.BuildConfig;

/**
 * Created by wjwu on 2015/9/2.
 */
public class CustomConfigure {
    public final static String BASE_URL = BuildConfig.API_HOST;
//    "http://www.kitty-travel.com/"
//    public final static String BASE_URL = "http://192.168.180.93:8080/wordpress/";
    /**
     * 客户端标识 1 android 2 iphone
     */
    public final static String CLIENT_TYPE = "client_type=1";
    public final static String YZ_APP = "yz_app=1&" + CLIENT_TYPE;
    public final static int MAX_DEFAUL_CATALOG_SHOW = 15;
}

