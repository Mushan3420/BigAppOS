package model;

import java.io.Serializable;

/**
 * Created by wjwu on 2015/9/4.
 */
public class ResponseRegistError implements Serializable {
    public String requestid;
    public String error_code;
    public String error_msg;
}
