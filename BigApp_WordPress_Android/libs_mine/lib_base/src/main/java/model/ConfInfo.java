package model;

import java.io.Serializable;

/**
 * Created by wjwu on 2015/9/22.
 */
public class ConfInfo implements Serializable {
    /***
     * 用户是否能注册
     */
    public String users_can_register;
    /***
     * 是否开启嵌套评论
     */
    public String thread_comments;
    public String version_api_url;

    public String wechat_login;
    public String qq_login;
    public String sina_login;
    public String avatar_allow_upload;
    public String show_avatars;

    public String bigapp_special_switch;  //专题是否启用 1启用；0不启用
    public String bigapp_special_list_style; //列表样式 1 样式1 ； 2 样式2
    public String bigapp_special_detail_style; //详情样式 1 样式1 ； 2 样式2
}
