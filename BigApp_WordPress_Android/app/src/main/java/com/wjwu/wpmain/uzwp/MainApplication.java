package com.wjwu.wpmain.uzwp;

import com.wjwu.wpmain.cache.SpTool;
import com.wjwu.wpmain.lib_base.BaseApplication;

import com.wjwu.wpmain.net.DataManager;

/**
 * Created by wjwu on 2015/9/14.
 */
public class MainApplication extends BaseApplication {

    public static void clearLoginInfo() {
        mUserId = null;
        mCookies = null;
        new SpTool(mInstance.getApplicationContext(), SpTool.SP_USER).clear();
        DataManager.clearUserInfo(mInstance.getApplicationContext());
    }
}
