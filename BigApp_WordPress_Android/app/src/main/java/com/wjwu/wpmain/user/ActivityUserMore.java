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
public class ActivityUserMore extends BaseFragmentActivity implements BaseFragmentActivity.FragmentCallBack {
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
        if (FragmentAbout.class.getSimpleName()
                .equals(fragmentTag)) {
            fragment = new FragmentAbout();
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

    public static void gotoFragmentAbout(Context context) {
        Intent intent = new Intent(context, ActivityUserMore.class);
        intent.putExtra(ARG_FRAGMENT_TAG,
                FragmentAbout.class.getSimpleName());
        context.startActivity(intent);
    }

}
