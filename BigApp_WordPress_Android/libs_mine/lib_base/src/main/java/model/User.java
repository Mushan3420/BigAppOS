package model;

import java.io.Serializable;

/**
 * Created by wjwu on 2015/9/4.
 */
public class User implements Serializable {
    public String username;
    public int uid;
    public int id;
    public String nice_name;
    public String email;
    public String status;
    public String logout_url;
    public String display_name;
    public long reg_time;
    public String[] roles;
    public String description;
    public String avatar;
}