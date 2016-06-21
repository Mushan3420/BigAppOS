package com.wjwu.wpmain.uzwp.detail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.wjwu.wpmain.cache.SpTool;
import com.wjwu.wpmain.lib_base.BaseFragmentActivity;
import com.wjwu.wpmain.uzwp.R;

import model.Topic;

/**
 * Created by wjwu on 2015/8/31.
 */
public class ActivityDetails extends BaseFragmentActivity implements BaseFragmentActivity.FragmentCallBack {
    private String mCurrentFragmentTag;

    //0不允许评论，1允许匿名评论，2不允许匿名评论，3必须是注册用户评论
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        if (new SpTool(this, SpTool.SP_SETTING).getBoolean("moon", false)) {
            setTheme(R.style.ActivityThemeMoon);
        } else {
            setTheme(R.style.ActivityTheme);
        }
        setContentView(R.layout.z_activity_container);
        loadFragment();
    }

    private void loadFragment() {
        if (getIntent() != null) {
            mCurrentFragmentTag = getIntent().getStringExtra(ARG_FRAGMENT_TAG);
            Bundle extras = getIntent().getExtras();
            fragmentChanged(mCurrentFragmentTag, extras, false);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void fragmentChanged(String fragmentTag, Bundle extras, boolean back) {
        if (back) {
            onBackPressed();
            return;
        }
        mCurrentFragmentTag = fragmentTag;
        Fragment fragment = null;
        if (FragmentDetailsCommon.class.getSimpleName().equals(fragmentTag)) {
            fragment = new FragmentDetailsCommon();
        }
        showFragment(R.id.container_activity, fragment, extras, fragmentTag);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int arg0, int arg1, Intent arg2) {
        super.onActivityResult(arg0, arg1, arg2);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /***
     * @param link        获取topic的link
     * @param link_type   1代表获取topic基本信息的url, 2代表获取topic详情的Url
     * @param banner_type "1"外链, "2"内容
     */
    public static void gotoFragmentDetailsBanner(Context context, String link, int link_type, String banner_type) {
        if (TextUtils.isEmpty(link)) {
            return;
        }
        Intent intent = new Intent(context, ActivityDetails.class);
        Bundle extras = new Bundle();
        extras.putString(ARG_FRAGMENT_TAG,
                FragmentDetailsCommon.class.getSimpleName());
        extras.putSerializable("link", link);
        extras.putSerializable("link_type", link_type);
        extras.putSerializable("banner_type", banner_type);
        intent.putExtras(extras);
        context.startActivity(intent);
    }

    /***
     * @param link        获取topic的link
     * @param link_type   1代表获取topic基本信息的url, 2代表获取topic详情的Url
     * @param banner_type "1"外链, "2"内容
     */
    public static void gotoFragmentDetailsBannerForResult(Fragment fragment, String link, int link_type, String banner_type, int requestCode) {
        if (TextUtils.isEmpty(link)) {
            return;
        }
        Intent intent = new Intent(fragment.getActivity(), ActivityDetails.class);
        Bundle extras = new Bundle();
        extras.putString(ARG_FRAGMENT_TAG,
                FragmentDetailsCommon.class.getSimpleName());
        extras.putSerializable("link", link);
        extras.putSerializable("link_type", link_type);
        extras.putSerializable("banner_type", banner_type);
        intent.putExtras(extras);
        fragment.startActivityForResult(intent, requestCode);
    }

    public static void gotoFragmentDetailsTopic(Context context, Topic topic) {
        if (topic == null) {
            return;
        }
        gotoFragmentDetailsBanner(context, topic.link, 2, "2");
    }

    public static void gotoFragmentDetailsTopicForResult(Fragment fragment, Topic topic, int requestCode) {
        if (topic == null) {
            return;
        }
        gotoFragmentDetailsBannerForResult(fragment, topic.link, 2, "2", requestCode);
    }
}
