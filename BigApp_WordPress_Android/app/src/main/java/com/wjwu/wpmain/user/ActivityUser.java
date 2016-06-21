package com.wjwu.wpmain.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.wjwu.wpmain.cache.SpTool;
import com.wjwu.wpmain.lib_base.BaseFragmentActivity;
import com.wjwu.wpmain.uzwp.R;

import event.MoonEvent;

/**
 * Created by wjwu on 2015/8/31.
 */
public class ActivityUser extends BaseFragmentActivity implements BaseFragmentActivity.FragmentCallBack {
    private String mCurrentFragmentTag;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        if (new SpTool(this, SpTool.SP_SETTING).getBoolean("moon", false)) {
            setTheme(R.style.ActivityThemeMoon);
        } else {
            setTheme(R.style.ActivityTheme);
        }
        setContentView(R.layout.z_activity_container);
        setmContainer_activity(findViewById(R.id.container_activity));
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
        System.out.println("----- wenjun fragmentTag = " + fragmentTag
                + ", back = " + back + ", extras = " + extras);
        if (back) {
            onBackPressed();
            return;
        }
        mCurrentFragmentTag = fragmentTag;
        Fragment fragment = null;
        if (FragmentCollect.class.getSimpleName()
                .equals(fragmentTag)) {
            fragment = new FragmentCollect();
        } else if (FragmentCollectAnony.class.getSimpleName()
                .equals(fragmentTag)) {
            fragment = new FragmentCollectAnony();
        } else if (FragmentCommit.class.getSimpleName()
                .equals(fragmentTag)) {
            fragment = new FragmentCommit();
        } else if (FragmentSetting.class.getSimpleName()
                .equals(fragmentTag)) {
            fragment = new FragmentSetting();
        } else if (FragmentCommitAnony.class.getSimpleName()
                .equals(fragmentTag)) {
            fragment = new FragmentCommitAnony();
        } else if (FragmentHistoryRead.class.getSimpleName()
                .equals(fragmentTag)) {
            fragment = new FragmentHistoryRead();
        } else if (FragmentUser.class.getSimpleName()
                .equals(fragmentTag)) {
            fragment = new FragmentUser();
        } else if (FragmentModifyDescription.class.getSimpleName()
                .equals(fragmentTag)) {
            fragment = new FragmentModifyDescription();
        } else if (FragmentModifyPwd.class.getSimpleName()
                .equals(fragmentTag)) {
            fragment = new FragmentModifyPwd();
        }
        showFragment(R.id.container_activity, fragment, extras, fragmentTag);
    }

    @Override
    public void onBackPressed() {
        System.out.println("----- wenjun mCurrentFragmentTag = "
                + mCurrentFragmentTag);
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int arg0, int arg1, Intent arg2) {
        super.onActivityResult(arg0, arg1, arg2);
    }

    @Override
    public void onEventMainThread(MoonEvent event) {
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

    public static void gotoFragmentCollect(Context context) {
        Intent intent = new Intent(context, ActivityUser.class);
        intent.putExtra(ARG_FRAGMENT_TAG,
                FragmentCollect.class.getSimpleName());
        context.startActivity(intent);
    }

    public static void gotoFragmentCollectAnony(Context context) {
        Intent intent = new Intent(context, ActivityUser.class);
        intent.putExtra(ARG_FRAGMENT_TAG,
                FragmentCollectAnony.class.getSimpleName());
        context.startActivity(intent);
    }

    public static void gotoFragmentCommit(Context context) {
        Intent intent = new Intent(context, ActivityUser.class);
        intent.putExtra(ARG_FRAGMENT_TAG,
                FragmentCommit.class.getSimpleName());
        context.startActivity(intent);
    }

    public static void gotoFragmentSetting(Context context) {
        Intent intent = new Intent(context, ActivityUser.class);
        intent.putExtra(ARG_FRAGMENT_TAG,
                FragmentSetting.class.getSimpleName());
        context.startActivity(intent);
    }

    public static void gotoFragmentHistoryRead(Context context) {
        Intent intent = new Intent(context, ActivityUser.class);
        intent.putExtra(ARG_FRAGMENT_TAG,
                FragmentHistoryRead.class.getSimpleName());
        context.startActivity(intent);
    }

    public static void gotoFragmentFragmentUser(Context context) {
        Intent intent = new Intent(context, ActivityUser.class);
        intent.putExtra(ARG_FRAGMENT_TAG,
                FragmentUser.class.getSimpleName());
        context.startActivity(intent);
    }

    public static void gotoFragmentCommitAnony(Context context, int topicId, int comment_type) {
        Intent intent = new Intent(context, ActivityUser.class);
        intent.putExtra("topic_id", topicId);
        intent.putExtra("comment_type", comment_type);
        intent.putExtra(ARG_FRAGMENT_TAG,
                FragmentCommitAnony.class.getSimpleName());
        context.startActivity(intent);
    }

    public static void gotoFragmentModifyDescription(Context context) {
        Intent intent = new Intent(context, ActivityUser.class);
        intent.putExtra(ARG_FRAGMENT_TAG,
                FragmentModifyDescription.class.getSimpleName());
        context.startActivity(intent);
    }

    public static void gotoFragmentModifyPwd(Context context) {
        Intent intent = new Intent(context, ActivityUser.class);
        intent.putExtra(ARG_FRAGMENT_TAG,
                FragmentModifyPwd.class.getSimpleName());
        context.startActivity(intent);
    }
}
