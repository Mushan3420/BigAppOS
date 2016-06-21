package com.wjwu.wpmain.cache;

/**
 * Created by wjwu on 2015/9/2.
 */
public class BaseResponse<T> {
    public String requestid;
    public int error_code;
    public String error_msg;
    public T data;
}
