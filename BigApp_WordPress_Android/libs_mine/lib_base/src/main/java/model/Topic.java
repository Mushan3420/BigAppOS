package model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by wjwu on 2015/9/4.
 */
public class Topic implements Serializable {
    public String imageUrl_1;
    public int ID;
    public String title;
    public String status;
    public String type;
    public Author author;
    public String content;
    public Object parent;
    public String link;
    public String date;
    public String modified;
    public String format;
    public String excerpt;
    public String comment_status;
    public int comment_num;
    public String views;
    public String date_tz;
    public String date_gmt;
    public String modified_tz;
    public String modified_gmt;
    public int comment_type;
    public ArrayList<TopicImage> featured_image;
    public TopicTerms terms;
    public boolean is_favorited;
}
