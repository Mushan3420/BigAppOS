package com.wjwu.wpmain.util;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.wjwu.wpmain.cache.BaseResponse;

public abstract class ResponseListenerSimple<T> {
    private Context mContext;
    private boolean loading = false;

    public ResponseListenerSimple(Context context) {
        mContext = context;
    }

    public void setIsLoading() {
        loading = true;
    }

    public boolean isLoading() {
        return loading;
    }

    /***
     * @param response 返回NULL，表示缓存没有更新，不用刷新UI
     */
    public void success(String response) {
        loading = false;
        onSuccess(response);
    }

    public void error(VolleyError error, boolean hasNetwork) {
        loading = false;
        if (hasNetwork) {
            ZLogUtils.logException(error);
            ZToastUtils.toastRequestError(mContext);
        }
        onError(error, hasNetwork);
    }

    /***
     * @param response obj为空代表请求失败，或者返回error_code不为0；其它代表正常
     */
    public abstract void onSuccess(String response);

    /***
     * 有网络时，请求失败
     */
    public abstract void onError(VolleyError error, boolean hasNetwork);
}