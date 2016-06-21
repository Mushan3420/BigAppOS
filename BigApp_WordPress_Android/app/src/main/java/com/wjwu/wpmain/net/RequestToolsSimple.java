package com.wjwu.wpmain.net;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.wjwu.wpmain.lib_base.BaseApplication;
import com.wjwu.wpmain.util.CommonUtils;
import com.wjwu.wpmain.util.ResponseListenerSimple;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by wjwu on 2015/8/29.
 */
public class RequestToolsSimple {
    private ResponseListenerSimple mListener;

    public RequestToolsSimple(ResponseListenerSimple listener) {
        mListener = listener;
    }

    private void cancelRequestByTag(String tag) {
        BaseApplication.getInstance().getRequestQueue().cancelAll(tag);
    }

    /***
     * 发送网络请求
     *
     * @param url           网络接口地址
     * @param requestMethod 请求方法 默认为Request.Method.POST
     * @param requestObject 请求参数对象
     */
    public <T> void sendRequest(String url, int requestMethod, Object requestObject, String tag) {
        if (CommonUtils.checkNetworkEnable(BaseApplication.getInstance().getApplicationContext())) {//如果有网络，再从网络上获取一遍
            getFromNetWork(url, requestMethod, requestObject, tag);
            return;
        }
        if (mListener != null) {
            mListener.error(null, false);
        }
    }

    private <T> void getFromNetWork(String url, int requestMethod, Object requestObject, String tag) {
        if (requestObject != null) {
            makeRequest(url, requestMethod, requestObject, tag);
            return;
        }
        makeStringRequest(url, requestMethod, requestObject, tag);
    }

    private <T> void makeJsonRequest(String url, Object requestObject, String tag) {
        if (requestObject != null) {
            makeRequest(url, Request.Method.POST, requestObject, tag);
            return;
        }
        makeStringRequest(url, Request.Method.POST, requestObject, tag);
    }

    private <T> void makeStringRequest(final String url, int requestMethod, final Object requestObject, String tag) {
        Log.e("", "wenjun request makeStringRequest url = " + url);
        StringRequest req = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("", "wenjun request makeStringRequest response = " + response);
                mListener.success(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mListener.error(error, true);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                if (!url.contains(RequestUrl.login) && BaseApplication.getCookies() != null) {
                    headers.put("Cookie", BaseApplication.getCookies());
                }
                return headers;
            }
        };
        req.setShouldCache(false);
        BaseApplication.getInstance().addToRequestQueue(req, tag);
    }

    private <T> void makeRequest(final String url, final int requestMethod, final Object requestObject, String tag) {
        Log.e("", "wenjun request makeRequest  url = " + url
                + ", requestMethod = " + requestMethod);
        Request<String> req = new Request<String>(requestMethod,
                url, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mListener.error(error, true);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                if (!url.contains(RequestUrl.login) && BaseApplication.getCookies() != null) {
                    headers.put("Cookie", BaseApplication.getCookies());
                }
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                if (requestObject != null && requestObject instanceof HashMap) {
                    return (HashMap<String, String>) requestObject;
                }
                return super.getParams();
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                try {
                    return Response.success(new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers)), null);
                } catch (Exception e) {
                    return Response.error(new ParseError(e));
                }
            }

            @Override
            protected void deliverResponse(String response) {
                Log.e("", "wenjun request makeRequest response = " + response);
                mListener.success(response);
            }
        };
        req.setShouldCache(false);
        BaseApplication.getInstance().addToRequestQueue(req, tag);
    }

    public Object makeSyncRequest(final String url, final int requestMethod, final Object requestObject, String tag) {
        RequestFuture<String> future = RequestFuture.newFuture();
        StringRequest request = new StringRequest(requestMethod, url, future, future) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Log.e("", "wenjun request makeRequest getPostParams requestObject = " + new Gson().toJson(requestObject));
                if (requestObject != null && requestObject instanceof HashMap) {
                    return (HashMap<String, String>) requestObject;
                }
                return super.getParams();
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                if (!url.contains(RequestUrl.login) && BaseApplication.getCookies() != null) {
                    headers.put("Cookie", BaseApplication.getCookies());
                }
                return headers;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(CommonUtils.getConnectTimeOutTime(), 1, 1.0F));
        request.setShouldCache(false);
        BaseApplication.getInstance().addToRequestQueue(request, tag);
        Log.e("", "wenjun request makeJsonRequestForCache end");
        Object result;
        try {
            result = future.get();
        } catch (InterruptedException e) {
            result = e;
            e.printStackTrace();
        } catch (ExecutionException e) {
            result = e;
            e.printStackTrace();
        }
        return result;
    }
}
