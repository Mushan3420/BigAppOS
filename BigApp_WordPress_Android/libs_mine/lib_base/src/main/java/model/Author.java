package model;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by wjwu on 2015/9/4.
 */
public class Author implements Serializable {

    public int ID;
    public String username;
    public String name;
    public String first_name;
    public String last_name;
    public String nickname;
    public String slug;
    public String URL;
    public String avatar;
    public String description;
    public String registered;
    /***
     * "meta": {
     * "links": {
     * "self": "http:\/\/192.168.180.93:8080\/wordpress\/?yz_app=1&api_route=users&action=get_user&id=1",
     * "archives": "http:\/\/192.168.180.93:8080\/wordpress\/?yz_app=1&api_route=users&action=get_posts&id=1"
     * }
     * }
     */
//    public HashMap<String, Object> meta;
}
