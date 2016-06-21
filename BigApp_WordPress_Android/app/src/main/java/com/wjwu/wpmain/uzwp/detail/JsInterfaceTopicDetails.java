package com.wjwu.wpmain.uzwp.detail;

import android.util.Log;
import android.webkit.JavascriptInterface;

import com.wjwu.wpmain.util.CommonUtils;

import model.Topic;

/**
 * Created by wjwu on 2015/9/19.
 */
public class JsInterfaceTopicDetails {
    private Topic mTopic;

    public JsInterfaceTopicDetails(Topic topic) {
        mTopic = topic;
    }

    @JavascriptInterface
    public void setFirstImage(String imageUrl) {
        mTopic.imageUrl_1 = imageUrl;
    }

    @JavascriptInterface
    public void setImageSrc(String imageUrl, String oldSrc, String newSrc) {
        Log.e("", "wenjun load_img setImageSrc imageUrl = " + imageUrl + ", oldSrc = " + oldSrc + ", newSrc = " + newSrc);
    }

    @JavascriptInterface
    public void onImageClick(String imageUrl) {
        Log.e("", "wenjun load_img onImageClick imageUrl = " + imageUrl);
    }

    @JavascriptInterface
    public String getTitle() {
        if (mTopic == null) {
            return "";
        }
        return CommonUtils.getYearDateFromGmt(mTopic.title);
    }

    @JavascriptInterface
    public String getTime() {
        if (mTopic == null) {
            return "";
        }
        return CommonUtils.getYearDateFromGmt(mTopic);
    }

    @JavascriptInterface
    public String getContent() {
        if (mTopic == null) {
            return "";
        }
        return mTopic.content;
    }

}
