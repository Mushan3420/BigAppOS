package com.wjwu.wpmain.share;

/**
 * Created by wjwu on 2015/9/17.
 */
public class ShareItem {
    /***
     * 图片资源
     */
    public int drawableId;
    /***
     * 文字资源
     */
    public int stringId;
    /***
     * 平台（QQ, WECHAT, SINNA）
     */
    public String platform;

    public ShareItem(int drawableId, int stringId, String platform) {
        this.drawableId = drawableId;
        this.stringId = stringId;
        this.platform = platform;
    }
}
