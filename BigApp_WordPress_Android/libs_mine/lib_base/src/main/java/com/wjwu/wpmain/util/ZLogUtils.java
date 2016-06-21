package com.wjwu.wpmain.util;

import android.util.Log;

/**
 * Created by wjwu on 2015/9/2.
 */
public class ZLogUtils {

    public static void logException(Exception e) {
        Log.e("ZLOGUTILS", "wp error = " + ((e == null) ? null : e.getMessage()));
    }
}
