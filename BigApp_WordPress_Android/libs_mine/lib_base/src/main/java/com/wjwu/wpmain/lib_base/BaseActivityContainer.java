package com.wjwu.wpmain.lib_base;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

/***
 * 一级界面中跳转
 *
 * @author macy
 */
public abstract class BaseActivityContainer extends BaseFragmentActivity
        implements BaseFragmentActivity.FragmentCallBack {
    private String mCurrentFragmentTag;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
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
        mCurrentFragmentTag = fragmentTag;
        if (back) {
            onBackPressed();
            return;
        }
        showFragment(R.id.container_activity,
                returnCurrentFragmentByTag(fragmentTag), extras, fragmentTag);
    }

    public abstract Fragment returnCurrentFragmentByTag(String fragmentTag);

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
}
