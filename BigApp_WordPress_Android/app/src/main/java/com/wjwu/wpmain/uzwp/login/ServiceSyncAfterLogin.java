package com.wjwu.wpmain.uzwp.login;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.android.volley.Request;
import com.google.gson.Gson;
import com.wjwu.wpmain.cache.DbTableAnonyCollect;
import com.wjwu.wpmain.net.RequestToolsSimple;
import com.wjwu.wpmain.net.RequestUrl;
import com.wjwu.wpmain.util.CommonUtils;

import java.util.ArrayList;
import java.util.HashMap;

import model.Topic;

/**
 * Created by wjwu on 2015/10/19.
 */
public class ServiceSyncAfterLogin extends IntentService {
    public ServiceSyncAfterLogin() {
        super("ServiceSyncAfterLogin");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (!CommonUtils.checkNetworkEnable(getApplicationContext(), false)) {
            return;
        }
        ArrayList<Topic> topicList = new DbTableAnonyCollect(getContentResolver()).getAllAnonyCollects();
        Log.e("", "wenjun ServiceSyncAfterLogin topicList = " + (topicList == null ? null : topicList.size()));
        if (topicList == null || topicList.size() < 1) {
            return;
        }
        StringBuffer sb_topic_ids = new StringBuffer();
        for (Topic topic : topicList) {
            if (topic != null) {
                sb_topic_ids.append(topic.ID + ",");
            }
        }
        sb_topic_ids.deleteCharAt(sb_topic_ids.length() - 1);
        Log.e("", "wenjun ServiceSyncAfterLogin sb_topic_ids = " + sb_topic_ids.toString());
        HashMap<String, String> requestObject = new HashMap<>();
        requestObject.put("wp_bigapp_favorite_posts", sb_topic_ids.toString());
        Object obj = new RequestToolsSimple(null).makeSyncRequest(RequestUrl.sync_collect_anony, Request.Method.POST, requestObject, "sync_collect_anony");
        boolean result = new DbTableAnonyCollect(getContentResolver()).clear();
        Log.e("", "wenjun ServiceSyncAfterLogin clear db result = " + result + ", obj = " + (obj == null ? null : new Gson().toJson(obj)));
    }
}
