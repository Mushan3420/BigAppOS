package com.wjwu.wpmain.cache;

import android.content.ContentResolver;
import android.net.Uri;

import com.wjwu.wpmain.lib_base.BaseApplication;

/**
 * Created by wjwu on 2015/10/9.
 */
public class BaseDbTable {
    protected ContentResolver mContentResolver;
    protected Uri CONTENT_URL;

    protected void setContentURLAndResolver(String tableName, ContentResolver contentResolver) {
        CONTENT_URL = Uri.parse("content://"
                + BaseApplication.getAuthority() + "/" + tableName);
        mContentResolver = contentResolver;
    }
}
