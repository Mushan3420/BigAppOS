package com.wjwu.wpmain.uzwp.detail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.wjwu.wpmain.cache.SpTool;
import com.wjwu.wpmain.lib_base.BaseFragmentActivity;
import com.wjwu.wpmain.uzwp.R;

import model.TopicTag;

/**
 * Created by wjwu on 2015/8/31.
 */
public class ActivityDetailsMore extends BaseFragmentActivity implements BaseFragmentActivity.FragmentCallBack {
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
        if (FragmentTagContent.class.getSimpleName().equals(fragmentTag)) {
            fragment = new FragmentTagContent();
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

    public static void gotoFragmentTagContent(Context context, TopicTag topicTag) {
        if (topicTag == null) {
            return;
        }
        Intent intent = new Intent(context, ActivityDetailsMore.class);
        Bundle extras = new Bundle();
        extras.putString(ARG_FRAGMENT_TAG,
                FragmentTagContent.class.getSimpleName());
        extras.putSerializable("topicTag", topicTag);
        intent.putExtras(extras);
        context.startActivity(intent);
    }
}
