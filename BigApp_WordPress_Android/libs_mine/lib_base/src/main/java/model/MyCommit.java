package model;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by wjwu on 2015/9/5.
 */
public class MyCommit implements Serializable {
    public int ID;
    public int post;
    public String content;
    public String status;
    public String type;
    public int parent;
    public Author author;
    public String date;
    public String date_tz;
    public String date_gmt;
    public HashMap<String, Object> post_info;
}
