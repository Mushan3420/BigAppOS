package com.wjwu.wpmain.util;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshAdapterViewBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.wjwu.wpmain.cache.BaseResponse;

public abstract class ResponseListener<T> {
    private Context mContext;
    private boolean loading = false;
    private PullToRefreshAdapterViewBase mPullToRefreshListView;

    public ResponseListener(Context context) {
        mContext = context;
    }

    public ResponseListener(Context context, PullToRefreshAdapterViewBase pullToRefreshListView) {
        this(context);
        mPullToRefreshListView = pullToRefreshListView;
    }

    public void setIsLoading() {
        loading = true;
    }

    public boolean isLoading() {
        Log.e("", "wenjun isLoading = " + loading);
        return loading;
    }

    /***
     * @param obj 返回NULL，表示缓存没有更新，不用刷新UI
     */
    public void success(Object obj) {
        if (obj == null) {//数据不用更新
            onSuccessInner(null);
            return;
        }
        try {
            if (obj instanceof String) {
                onSuccessInner(obj);
                return;
            }
            BaseResponse<T> res = (BaseResponse<T>) obj;
            if (res.error_code == 0) {
                onSuccessInner(res);
                return;
            }
            if (TextUtils.isEmpty(res.error_msg)) {
                ZToastUtils.toastRequestError(mContext);
            } else {
                ZToastUtils.toastMessage(mContext, res.error_msg);
            }
        } catch (Exception e) {
            ZLogUtils.logException(e);
            ZToastUtils.toastDataError(mContext);
        }
        loading = false;
        onPullComplete(true);
        onSuccessError();
    }

    public void error(VolleyError error) {
        loading = false;
        onPullComplete(true);
        ZLogUtils.logException(error);
        ZToastUtils.toastRequestError(mContext);
        onError(error);
    }

    public void cacheData(Object obj, boolean hasNetwork) {
        Log.e("", "wenjun cacheData = " + obj + ", hasNetwork = " + hasNetwork);
        if (!hasNetwork) {
            loading = false;
            onPullComplete(hasNetwork);
        }
        onCacheData(obj, hasNetwork);
    }

    /**
     * 请求指定了缓存时，如果没有本地缓存或者获取本地缓存失败的时候，会调用此处
     */
    public void cacheDataError(boolean hasCache, boolean hasNetwork) {
        if (!hasNetwork) { //（如果网络是好的，会从网络上获取，此处不用处理；如果网络不是正常的）  //没有网络则提示
            loading = false;
            onPullComplete(hasNetwork);
            if (hasCache) { //如果緩存解析出錯了，则报错，如果缓存为Null，则不处理
                ZToastUtils.toastCacheError(mContext);
            }
        }
        onCacheDataError(hasNetwork);
    }

    private void onPullComplete(boolean hasNetwork) {
        if (mPullToRefreshListView == null) {
            return;
        }
        if (hasNetwork) {
            mPullToRefreshListView.onRefreshComplete();
            return;
        }
        //如果没有网络则需要延时完成
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mPullToRefreshListView != null) {
                    mPullToRefreshListView.onRefreshComplete();
                }
            }
        }, 200);
    }

    private void onSuccessInner(Object obj) {
        onSuccess(obj);
        loading = false;
        onPullComplete(true);
    }

    public void onDestory() {
        mPullToRefreshListView = null;
        mContext = null;
    }

    public void useCacheNotAndNoNetwork() {

    }

    /***
     * @param obj obj为空代表请求失败，或者返回error_code不为0；其它代表正常
     */
    public abstract void onSuccess(Object obj);

    /***
     * 成功了，但是解析数据错误，或者error_code不为0
     */
    public abstract void onSuccessError();

    /***
     * 有网络时，请求失败
     */
    public abstract void onError(VolleyError error);

    /***
     * 请求指定了缓存时，如果获取到了本地缓存，会调用此处
     */
    public abstract void onCacheData(Object obj, boolean hasNetwork);

    /***
     * 请求指定了缓存时，但没有网络，并且没有本地缓存或者获取本地缓存失败的时候会调用此处
     */
    public abstract void onCacheDataError(boolean hasNetwork);

}