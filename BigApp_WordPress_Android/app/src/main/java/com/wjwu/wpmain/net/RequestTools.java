package com.wjwu.wpmain.net;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.RequestFuture;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.wjwu.wpmain.cache.BaseResponse;
import com.wjwu.wpmain.lib_base.BaseApplication;
import com.wjwu.wpmain.util.CommonUtils;
import com.wjwu.wpmain.util.ResponseListener;
import com.wjwu.wpmain.util.ZLogUtils;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import model.ResponseLoginError;
import model.ResponseRegistError;

/**
 * Created by wjwu on 2015/8/29.
 */
public class RequestTools {

    private static final int CACHE_MAXAGE = 30;

    private void setOldCachInvalidWhenHasNewDatas(String url) {
        BaseApplication.getInstance().getRequestQueue().getCache().invalidate(url, true);
    }

    private void forceExpireCache(String url) {
        Cache cache = BaseApplication.getInstance().getRequestQueue().getCache();
        Cache.Entry entry = cache.get(url);
        if (entry != null && entry.data != null && entry.data.length > 0) {
            if (!entry.isExpired()) {
                setOldCachInvalidWhenHasNewDatas(url);
            }
        }
    }

    private void delCacheByUrl(String url) {
        BaseApplication.getInstance().getRequestQueue().getCache().remove(url);
    }

    private void cancelRequestByTag(String tag) {
        BaseApplication.getInstance().getRequestQueue().cancelAll(tag);
    }

    private void cancelAllRequest() {
        BaseApplication.getInstance().getRequestQueue().cancelAll(new RequestQueue.RequestFilter() {
            @Override
            public boolean apply(Request<?> request) {
                //对request做判断  return false表示不取消
                return true;
            }
        });
    }

    public void clearAllCache() {
        BaseApplication.getInstance().getRequestQueue().getCache().clear();
    }

    //1.我们缓存需要缓存的信息；（对于需要缓存的，我们可以设置其缓存有效时间为60秒）---ok--
    //2.如果没有网络，我们直接从缓存中取（需要手动调用）---ok--
    //3.如果有网络，我们先从缓存中取，---ok--
    //3.1，如果取到了，记录其expire时间，然后我们再从网络上获取，如果时间跟之前记录的是同一个，则表示缓存在60秒的有效期内，则不用刷新界面，也不用更新缓存。 （如果判断服务器是用的缓存的，还是从网络上获取的呢，当前时间与之前记录的时间对比？如果大于60秒则表示是新的么？可以有）---ok--
    //3.2，如果取到了，记录其expire时间，然后我们再从网络上获取，如果时间跟之前记录的不同，则表示是从网络上新获取的数据，则更新缓存(设置60秒有效期)，并刷新界面 ---ok--
    //3.3，如果没有取到，则直接从网络上获取，并缓存(设置60秒有效期)，刷新界面 ---ok--
    //4.用户可以清空缓存

    /***
     * 发送网络请求
     *
     * @param url           网络接口地址
     * @param cached        是否缓存
     * @param requestObject 请求参数对象
     */
    public <T> void sendRequest(String url, boolean cached, Object requestObject, final TypeToken<BaseResponse<T>> token, String tag) {
        sendRequest(url, cached, Request.Method.POST, requestObject, token, tag);
    }

    public <T> void sendRequest(String url, boolean cached, int requestMethod, Object requestObject, final TypeToken<BaseResponse<T>> token, String tag) {
        sendRequest(url, cached, CACHE_MAXAGE, requestMethod, requestObject, token, tag);
    }

    /***
     * 发送网络请求
     *
     * @param url           网络接口地址
     * @param cached        是否缓存
     * @param requestMethod 请求方法 默认为Request.Method.POST
     * @param requestObject 请求参数对象
     */
    public <T> void sendRequest(String url, boolean cached, long cachedTime, int requestMethod, Object requestObject, final TypeToken<BaseResponse<T>> token, String tag) {
        boolean hasNetwork = CommonUtils.checkNetworkEnable(BaseApplication.getInstance().getApplicationContext());
        long oldSoftExpire = 0;
        if (cached) {
            //需要缓存的,先从缓存中取
            Cache cache = BaseApplication.getInstance().getRequestQueue().getCache();
            Cache.Entry entry = cache.get(url);
            Log.d("", "wenjun request getCacheFromUrl entry = " + entry
                    + ", url = " + url);
            if (entry != null) {
                try {
                    oldSoftExpire = entry.softTtl;
                    String data = new String(entry.data, "UTF-8");
                    mListener.cacheData(token == null ? data : new Gson().fromJson(data, token.getType()), hasNetwork);
                } catch (Exception e) {
                    ZLogUtils.logException(e);
                    mListener.cacheDataError(true, hasNetwork);
                }
            } else {
                mListener.cacheDataError(false, hasNetwork);
            }
        } else if (!hasNetwork) {
            mListener.useCacheNotAndNoNetwork();
        }
        if (hasNetwork) {//如果有网络，再从网络上获取一遍
            getFromNetWork(url, cached, cachedTime, oldSoftExpire, requestMethod, requestObject, token, tag);
        }
    }

    private <T> void getFromNetWork(String url, boolean cached, long cachedTime, long oldSoftExpire, int requestMethod, Object requestObject, TypeToken<BaseResponse<T>> token, String tag) {
        if (requestObject != null) {
            makeRequest(url, cached, cachedTime, oldSoftExpire, requestMethod, requestObject, token, tag);
            return;
        }
        makeJsonRequest(url, cached, cachedTime, oldSoftExpire, requestMethod, requestObject, token, tag);
    }

    private <T> void makeJsonRequest(final String url, final boolean cached, final long cachedTime, final long oldSoftExpire, int requestMethod, final Object requestObject, final TypeToken<BaseResponse<T>> token, String tag) {
        Log.e("", "wenjun request makeJsonRequest cached = " + cached + ", oldSoftExpire = " + oldSoftExpire + ", url = " + url);
        JsonRequest<Object> req = new JsonRequest<Object>(requestMethod,
                url, requestObject == null ? null : new Gson().toJson(requestObject),
                new Response.Listener<Object>() {
                    @Override
                    public void onResponse(Object response) {
                        mListener.success(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mListener.error(error);
                    }
                }) {
            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                if (!url.contains(RequestUrl.login) && BaseApplication.getCookies() != null) {
                    headers.put("Cookie", BaseApplication.getCookies());
                }
                return headers;
            }

            @Override
            public Priority getPriority() {
                //优先级
                return Priority.HIGH;
            }

            @Override
            protected Response<Object> parseNetworkResponse(NetworkResponse response) {
                try {
                    boolean successCache = true;
                    String json = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers));
                    Log.e("", "wenjun request parseNetworkResponse response json = " + json);
                    BaseResponse<T> res = null;
                    if (token != null) {
                        try {
                            res = new Gson().fromJson(json, token.getType());
                            if (res.error_code != 0) {
                                successCache = false;
                            }
                        } catch (Exception e) {
                            successCache = false;
                            BaseResponse<String> tmpResponse = new Gson().fromJson(json, new TypeToken<BaseResponse<String>>() {
                            }.getType());
                            res = new BaseResponse<>();
                            res.error_code = tmpResponse.error_code;
                            res.requestid = tmpResponse.requestid;
                            res.error_msg = tmpResponse.data;
                            //失败后，data为String
                        }
                    }
                    Cache.Entry cache = null;
                    if (cached && successCache) {//如果有成功的缓存，则对比缓存是否有更新，如果没有更新则缓存成功的缓存
                        cache = cache(response, cachedTime, oldSoftExpire);
                        if (cache == null) {
                            //如果cache为null,表示缓存没有更新，则不用更新ui
                            Log.e("", "wenjun request parseNetworkResponse use cache");
                            return Response.success(null, null);
                        }
                    }
                    Response responseResult = Response.success(
                            token == null ? json : res, cache);
                    Log.e("", "wenjun request parseNetworkResponse intermediate = " + responseResult.intermediate + ", token = " + token);
                    return responseResult;
                } catch (UnsupportedEncodingException var3) {
                    return Response.error(new ParseError(var3));
                } catch (JsonSyntaxException var4) {
                    return Response.error(new ParseError(var4));
                }
            }
        };
        // Adding request to request queue
        // Tag used to cancel the request
        req.setShouldCache(cached);
        req.setRetryPolicy(new DefaultRetryPolicy(CommonUtils.getConnectTimeOutTime(), 1, 1.0F));
        BaseApplication.getInstance().addToRequestQueue(req, tag);
    }

    public <T> void makeJsonRequestForCache(String name, final String url, int requestMethod, final Object requestObject, final TypeToken<BaseResponse<T>> token, String tag) {
        Log.e("", "wenjun request makeJsonRequestForCache url = " + url + ", name = " + name);
        long oldSoftExpireTmp = 0;
        //需要缓存的,先从缓存中取
        final Cache cache = BaseApplication.getInstance().getRequestQueue().getCache();
        Cache.Entry entry = cache.get(url);
        if (entry != null) {
            oldSoftExpireTmp = entry.softTtl;
        }
        final long oldSoftExpire = oldSoftExpireTmp;
        RequestFuture requestFuture = RequestFuture.newFuture();
        JsonRequest<Object> request = new JsonRequest<Object>(requestMethod, url, requestObject == null ? null : new Gson().toJson(requestObject), requestFuture, requestFuture) {
            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                if (!url.contains(RequestUrl.login) && BaseApplication.getCookies() != null) {
                    headers.put("Cookie", BaseApplication.getCookies());
                }
                return headers;
            }

            @Override
            protected Response<Object> parseNetworkResponse(NetworkResponse response) {
                try {
                    boolean successCache = true;
                    String json = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers));
                    Log.e("", "wenjun request makeJsonRequestForCache response json = " + json);
                    BaseResponse<T> res = null;
                    if (token != null) {
                        try {
                            res = new Gson().fromJson(json, token.getType());
                            if (res.error_code != 0) {
                                successCache = false;
                            }
                        } catch (Exception e) {
                            successCache = false;
                            BaseResponse<String> tmpResponse = new Gson().fromJson(json, new TypeToken<BaseResponse<String>>() {
                            }.getType());
                            res = new BaseResponse<>();
                            res.error_code = tmpResponse.error_code;
                            res.requestid = tmpResponse.requestid;
                            res.error_msg = tmpResponse.data;
                            //失败后，data为String
                        }
                    }
                    Cache.Entry cache = null;
                    if (successCache) {//如果有成功的缓存，则对比缓存是否有更新，如果没有更新则缓存成功的缓存
                        cache = cache(response, 0, oldSoftExpire);
                    }
                    Response responseResult = Response.success(
                            token == null ? json : res, cache);
                    return responseResult;
                } catch (UnsupportedEncodingException var3) {
                    return Response.error(new ParseError(var3));
                } catch (JsonSyntaxException var4) {
                    return Response.error(new ParseError(var4));
                }
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(CommonUtils.getConnectTimeOutTime(), 1, 1.0F));
        request.setShouldCache(true);
        BaseApplication.getInstance().addToRequestQueue(request, tag);
        Log.e("", "wenjun request makeJsonRequestForCache end");
        Object result = null;
        try {
            result = requestFuture.get();
            mListener.onSuccess(result);
        } catch (InterruptedException e) {
            mListener.onError(new VolleyError(e));
            e.printStackTrace();
        } catch (ExecutionException e) {
            mListener.onError(new VolleyError(e));
            e.printStackTrace();
        }
    }

    public <T> void makeCustomJsonRequest(final String url, final boolean cached, int requestMethod, final Object requestObject, final TypeToken<T> token, String tag) {
        Log.e("", "wenjun request makeCustomJsonRequest url = " + url);
        JsonRequest<Object> req = new JsonRequest<Object>(requestMethod,
                url, requestObject == null ? null : new Gson().toJson(requestObject),
                new Response.Listener<Object>() {
                    @Override
                    public void onResponse(Object response) {
                        mListener.onSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mListener.onError(error);
                    }
                }) {
            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                return headers;
            }

            @Override
            public Priority getPriority() {
                //优先级
                return Priority.HIGH;
            }

            @Override
            protected Response<Object> parseNetworkResponse(NetworkResponse response) {
                try {
                    boolean successCache = true;
                    String json = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers));
                    Log.e("", "wenjun request makeCustomJsonRequest parseNetworkResponse response json = " + json);
                    T t = null;
                    if (token != null) {
                        try {
                            t = new Gson().fromJson(json, token.getType());
                        } catch (Exception e) {
                            //失败后，data为String
                            ZLogUtils.logException(e);
                        }
                    }
                    Response responseResult = Response.success(
                            t == null ? json : t, null);
                    return responseResult;
                } catch (UnsupportedEncodingException var3) {
                    return Response.error(new ParseError(var3));
                } catch (JsonSyntaxException var4) {
                    return Response.error(new ParseError(var4));
                }
            }
        };
        // Adding request to request queue
        // Tag used to cancel the request
        req.setRetryPolicy(new DefaultRetryPolicy(CommonUtils.getConnectTimeOutTime(), 1, 1.0F));
        req.setShouldCache(cached);
        BaseApplication.getInstance().addToRequestQueue(req, tag);
    }

    public <T> Object makeJsonRequestForCache2(String name, final String url, int requestMethod, final Object requestObject, final TypeToken<BaseResponse<T>> token, String tag) {
        Log.e("", "wenjun request makeJsonRequestForCache url = " + url + ", name = " + name);
        long oldSoftExpireTmp = 0;
        //需要缓存的,先从缓存中取
        final Cache cache = BaseApplication.getInstance().getRequestQueue().getCache();
        Cache.Entry entry = cache.get(url);
        if (entry != null) {
            oldSoftExpireTmp = entry.softTtl;
        }
        final long oldSoftExpire = oldSoftExpireTmp;
        RequestFuture requestFuture = RequestFuture.newFuture();
        JsonRequest<Object> request = new JsonRequest<Object>(requestMethod, url, requestObject == null ? null : new Gson().toJson(requestObject), requestFuture, requestFuture) {
            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                if (!url.contains(RequestUrl.login) && BaseApplication.getCookies() != null) {
                    headers.put("Cookie", BaseApplication.getCookies());
                }
                return headers;
            }

            @Override
            protected Response<Object> parseNetworkResponse(NetworkResponse response) {
                try {
                    boolean successCache = true;
                    String json = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers));
                    Log.e("", "wenjun request makeJsonRequestForCache response json = " + json);
                    BaseResponse<T> res = null;
                    if (token != null) {
                        try {
                            res = new Gson().fromJson(json, token.getType());
                            if (res.error_code != 0) {
                                successCache = false;
                            }
                        } catch (Exception e) {
                            successCache = false;
                            BaseResponse<String> tmpResponse = new Gson().fromJson(json, new TypeToken<BaseResponse<String>>() {
                            }.getType());
                            res = new BaseResponse<>();
                            res.error_code = tmpResponse.error_code;
                            res.requestid = tmpResponse.requestid;
                            res.error_msg = tmpResponse.error_msg;
                            //失败后，data为String
                        }
                    }
                    Cache.Entry cache = null;
                    if (successCache) {//如果有成功的缓存，则对比缓存是否有更新，如果没有更新则缓存成功的缓存
                        cache = cache(response, 0, oldSoftExpire);
                    }
                    Response responseResult = Response.success(
                            token == null ? json : res, cache);
                    return responseResult;
                } catch (UnsupportedEncodingException var3) {
                    return Response.error(new ParseError(var3));
                } catch (JsonSyntaxException var4) {
                    return Response.error(new ParseError(var4));
                }
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(CommonUtils.getConnectTimeOutTime(), 1, 1.0F));
        request.setShouldCache(true);
        BaseApplication.getInstance().addToRequestQueue(request, tag);
        Log.e("", "wenjun request makeJsonRequestForCache end");
        Object result = null;
        try {
            result = requestFuture.get();
        } catch (InterruptedException e) {
            result = e;
            e.printStackTrace();
        } catch (ExecutionException e) {
            result = e;
            e.printStackTrace();
        }
        return result;
    }

    private <T> void makeRequest(final String url, final boolean cached, final long cachedTime, final long oldSoftExpire, final int requestMethod, final Object requestObject, final TypeToken<BaseResponse<T>> token, String tag) {
        Log.e("", "wenjun request makeRequest cached = " + cached + ", oldSoftExpire = " + oldSoftExpire + ", url = " + url
                + ", requestMethod = " + requestMethod);
        Request<Object> req = new Request<Object>(requestMethod,
                url, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mListener.error(error);
            }
        }) {
            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                if (!url.contains(RequestUrl.login) && BaseApplication.getCookies() != null) {
                    headers.put("Cookie", BaseApplication.getCookies());
                }
                return headers;
            }

            @Override
            public Priority getPriority() {
                //优先级
                return Priority.HIGH;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Log.e("", "wenjun request makeRequest getPostParams requestObject = " + new Gson().toJson(requestObject));
                if (requestObject != null && requestObject instanceof HashMap) {
                    return (HashMap<String, String>) requestObject;
                }
                return super.getParams();
            }

            @Override
            protected Response<Object> parseNetworkResponse(NetworkResponse response) {
                try {
                    boolean successCache = true;
                    String json = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers));
                    Log.e("", "wenjun request makeRequest response json = " + json);
                    BaseResponse<T> res = null;
                    if (token != null) {
                        try {
                            res = new Gson().fromJson(json, token.getType());
                            if (res.error_code != 0) {
                                successCache = false;
                            }
                        } catch (Exception e) {
                            successCache = false;
                            if (url.contains(RequestUrl.login)) {
                                try {
                                    ResponseLoginError tmpResponse = new Gson().fromJson(json, new TypeToken<ResponseLoginError>() {
                                    }.getType());
                                    res = new BaseResponse<>();
                                    res.error_code = -2;
                                    res.requestid = tmpResponse.requestid;
                                    if (tmpResponse.error_msg != null) {
                                        res.error_msg = tmpResponse.error_msg[0];
                                    }
                                } catch (Exception e2) {
                                    ResponseRegistError tmpResponse = new Gson().fromJson(json, new TypeToken<ResponseRegistError>() {
                                    }.getType());
                                    res = new BaseResponse<>();
                                    res.error_code = -2;
                                    res.requestid = tmpResponse.requestid;
                                    res.error_msg = tmpResponse.error_msg;
                                }
                            } else {
                                ResponseRegistError tmpResponse = new Gson().fromJson(json, new TypeToken<ResponseRegistError>() {
                                }.getType());
                                res = new BaseResponse<>();
                                res.error_code = -2;
                                res.requestid = tmpResponse.requestid;
                                res.error_msg = tmpResponse.error_msg;
                            }
                            //登录失败返回的error_msg为数组， 注册失败返回的是Object
                        }
                    }
                    Cache.Entry cache = null;
                    if (cached && successCache) {//如果有成功的缓存，则对比缓存是否有更新，如果没有更新则缓存成功的缓存
                        cache = cache(response, cachedTime, oldSoftExpire);
                        if (cache == null) {
                            //如果cache为null,表示缓存没有更新，则不用更新ui
                            Log.e("", "wenjun request parseNetworkResponse use cache");
                            return Response.success(null, null);
                        }
                    }
                    if ((url.contains(RequestUrl.login) ||
                            url.contains(RequestUrl.third_login_check)) && res != null && res.error_code == 0) {
                        String rawCookies = response.headers.get("Set-Cookie");
                        BaseApplication.setCookies(rawCookies);
                    }
                    Response responseResult = Response.success(
                            token == null ? json : res, cache);
                    Log.e("", "wenjun request makeRequest  intermediate = " + responseResult.intermediate + ", token = " + token);
                    return responseResult;
                } catch (UnsupportedEncodingException var3) {
                    return Response.error(new ParseError(var3));
                } catch (JsonSyntaxException var4) {
                    return Response.error(new ParseError(var4));
                } catch (Exception var5) {
                    return Response.error(new ParseError(var5));
                }
            }

            @Override
            protected void deliverResponse(Object response) {
                mListener.success(response);
            }
        };
        // Adding request to request queue
        // Tag used to cancel the request
        req.setRetryPolicy(new DefaultRetryPolicy(CommonUtils.getConnectTimeOutTime(), 1, 1.0F));
        req.setShouldCache(cached);
        BaseApplication.getInstance().addToRequestQueue(req, tag);

    }

    /***
     * 緩存的數據
     *
     * @param response
     * @param maxAge        缓存的有效时间，默认60秒
     * @param oldSoftExpire
     * @return
     */
    private Cache.Entry cache(NetworkResponse response, long maxAge, long oldSoftExpire) {
        long now = System.currentTimeMillis();
        if (maxAge == 0) maxAge = CACHE_MAXAGE;
//        （如果判断服务器是用的缓存的，还是从网络上获取的呢，当前时间与之前记录的时间对比？如果大于60秒则表示是新的么？可以有）
        long softExpire = now + maxAge * 1000;
        Log.e("", "wenjun request cache() oldSoftExpire - now = " + (oldSoftExpire - now) + ", oldSoftExpire = " + oldSoftExpire + ", softExpire = " + softExpire);
        if ((oldSoftExpire > now) && (oldSoftExpire < softExpire)) {
            //如果之前的时间一定大于现在的时间，并且之前的时间减去现在的时间如果在60秒内，则表示是同一个，不用再更新缓存了
            return null;
        }

        Map<String, String> headers = response.headers;

        long serverDate = 0;
        String serverEtag = null;
        String headerValue;

        headerValue = headers.get("Date");
        if (headerValue != null) {
            serverDate = HttpHeaderParser.parseDateAsEpoch(headerValue);
        }
        Cache.Entry entry = new Cache.Entry();
        entry.data = response.data;
        entry.etag = serverEtag;
        entry.softTtl = softExpire;
        entry.ttl = entry.softTtl;
        entry.serverDate = serverDate;
        entry.responseHeaders = headers;
        return entry;
    }

    private ResponseListener mListener;

    public RequestTools(ResponseListener listener) {
        mListener = listener;
    }

//    public void makeJsonArrayRequest(String url, boolean cached) {
//        JsonArrayRequest req = new JsonArrayRequest(url,
//                new Response.Listener<JSONArray>() {
//                    @Override
//                    public void onResponse(JSONArray response) {
//                        Log.d("", response.toString());
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.e("UZ_VOLLEY", "Error: " + error.getMessage());
//            }
//        });
//        // Adding request to request queue
//        // Tag used to cancel the request
//        req.setShouldCache(cached);
//        BaseApplication.getInstance().addToRequestQueue(req, "json_array_req");
//    }
//
//    public void makeStringRequest(String url, boolean cached) {
//        StringRequest req = new StringRequest(Request.Method.GET,
//                url, new Response.Listener<String>() {
//
//            @Override
//            public void onResponse(String response) {
//                Log.d("", response.toString());
//            }
//        }, new Response.ErrorListener() {
//
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.e("UZ_VOLLEY", "Error: " + error.getMessage());
//            }
//        });
//        // Adding request to request queue
//        // Tag used to cancel the request
//        req.setShouldCache(cached);
//        BaseApplication.getInstance().addToRequestQueue(req, "string_req");
//    }
}
