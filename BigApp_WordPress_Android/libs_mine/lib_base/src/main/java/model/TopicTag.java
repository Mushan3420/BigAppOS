package model;

import java.io.Serializable;

/**
 * Created by wjwu on 2015/10/20.
 */
public class TopicTag implements Serializable {
    public int ID;
    public String name;
    public String slug;
    public String description;
    public String taxonomy;
    public int count;
    public String link;
}
