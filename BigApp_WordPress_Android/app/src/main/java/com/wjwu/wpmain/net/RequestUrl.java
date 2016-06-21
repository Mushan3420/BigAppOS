package com.wjwu.wpmain.net;

/**
 * Created by wjwu on 2015/9/2.
 */
public class RequestUrl {
    /***
     * 获取头部菜单：
     * HTTP方法：GET
     */
    public static String nav_list = CustomConfigure.BASE_URL + "?" + CustomConfigure.YZ_APP + "&api_route=taxonomies&action=get_nav_list";
    public static String post_tags = CustomConfigure.BASE_URL + "?" + CustomConfigure.YZ_APP + "&api_route=taxonomies&action=get_post_tags";
    public static String base_content_list = CustomConfigure.BASE_URL + "?" + CustomConfigure.YZ_APP + "&api_route=posts&action=get_posts";
    public static String login = CustomConfigure.BASE_URL + "/wp-login.php?" + CustomConfigure.YZ_APP + "&api_route=auth&action=login";
    public static String regist = CustomConfigure.BASE_URL + "/wp-login.php?" + CustomConfigure.YZ_APP + "&api_route=auth&action=register";
    public static String search = CustomConfigure.BASE_URL + "?" + CustomConfigure.YZ_APP + "&api_route=posts&action=get_posts";
    public static String my_commit = CustomConfigure.BASE_URL + "?" + CustomConfigure.YZ_APP + "&api_route=comments&action=my_comments";
    public static String my_collect = CustomConfigure.BASE_URL + "?" + CustomConfigure.YZ_APP + "&api_route=favorite&action=list";
    public static String add_collect = CustomConfigure.BASE_URL + "?" + CustomConfigure.YZ_APP + "&api_route=favorite&action=add&post_id=";
    public static String sync_collect_anony = CustomConfigure.BASE_URL + "?" + CustomConfigure.YZ_APP + "&api_route=favorite&action=syncfp";
    public static String del_collect = CustomConfigure.BASE_URL + "?" + CustomConfigure.YZ_APP + "&api_route=favorite&action=remove&post_id=";
    public static String clear_collect = CustomConfigure.BASE_URL + "?" + CustomConfigure.YZ_APP + "&api_route=favorite&action=clear";
    public static String topic_commit = CustomConfigure.BASE_URL + "?" + CustomConfigure.YZ_APP + "&api_route=comments&action=get_comments&id=";
    public static String del_commit = CustomConfigure.BASE_URL + "?" + CustomConfigure.YZ_APP + "&api_route=comments&action=delete_comment&comment=";
    public static String add_commit = CustomConfigure.BASE_URL + "?" + CustomConfigure.YZ_APP + "&api_route=comments&action=add_comment";
    public static String get_conf = CustomConfigure.BASE_URL + "?" + CustomConfigure.YZ_APP + "&api_route=bigapp_api&action=get_conf";
//    public static String get_conf = "http://192.168.180.50:8089/wordpress/?yz_app=1&api_route=bigapp_api&action=get_conf";
//    [NSString stringWithFormat:@"/?yz_app=1&api_route=comments&action=&comment=%@&id=%@&author=%@&email=%@",comment,articleId,author,email];

    public static String third_login_check = CustomConfigure.BASE_URL + "/wp-login.php?" + CustomConfigure.YZ_APP + "&api_route=sociallogin&action=check";
    public static String edit_user_info = CustomConfigure.BASE_URL + "?" + CustomConfigure.YZ_APP + "&api_route=users&action=edit_user";
    public static String edit_avatar = CustomConfigure.BASE_URL + "?" + CustomConfigure.YZ_APP + "&api_route=users&action=upload_avatar";
}
