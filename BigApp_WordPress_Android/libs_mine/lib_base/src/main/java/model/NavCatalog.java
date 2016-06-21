package model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by wjwu on 2015/9/2.
 */
public class NavCatalog implements Serializable {
    public String ID;
    public String name;
    public String description;
    public String parent;
    public String count;
    public String object;
    public String type;
    public String link;
    public String org_name;
    public String rank;
    public ArrayList<Banner> banner_list;
}
