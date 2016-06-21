package com.wjwu.wpmain.uzwp.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.wjwu.wpmain.cache.SpTool;
import com.wjwu.wpmain.lib_base.BaseFragmentActivity;
import com.wjwu.wpmain.uzwp.R;

/**
 * Created by wjwu on 2015/8/31.
 */
public class ActivityLogin extends BaseFragmentActivity implements BaseFragmentActivity.FragmentCallBack {
    private String mCurrentFragmentTag;

    protected int bind = 0;
    protected String platform = null;
    protected String openid = null;
    protected String token = null;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        if (new SpTool(this, SpTool.SP_SETTING).getBoolean("moon", false)) {
            setTheme(R.style.ActivityThemeAnimUpDownMoon);
        } else {
            setTheme(R.style.ActivityThemeAnimUpDown);
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
        mCurrentFragmentTag = fragmentTag;
        Fragment fragment = null;
        if (FragmentLogin.class.getSimpleName()
                .equals(fragmentTag)) {
            fragment = new FragmentLogin();
            if (!back) {
                showFragment(R.id.container_activity, fragment, extras, fragmentTag);
                return;
            }
        }
        if (FragmentForgetPwd.class.getSimpleName().equals(fragmentTag)) {
            fragment = new FragmentForgetPwd();
        } else if (FragmentRegist.class.getSimpleName().equals(
                fragmentTag)) {
            fragment = new FragmentRegist();
        }
        if (extras != null) {
            fragment.setArguments(extras);
        }
        showFragmentRotation(fragment, back);
//        showFragmentRotation(R.id.container_activity, fragment, extras, back, fragmentTag);
    }

    @Override
    public void onBackPressed() {
        System.out.println("----- wenjun mCurrentFragmentTag = "
                + mCurrentFragmentTag);
        if (FragmentLogin.class.getSimpleName()
                .equals(mCurrentFragmentTag)) {
        } else if (FragmentRegist.class.getSimpleName().equals(
                mCurrentFragmentTag)) {
            fragmentChanged(FragmentLogin.class.getSimpleName(), null, true);
            return;
        } else if (FragmentForgetPwd.class.getSimpleName().equals(
                mCurrentFragmentTag)) {
            fragmentChanged(FragmentLogin.class.getSimpleName(), null, true);
            return;
        }
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

    public static void gotoFragmentLogin(Context context) {
        Intent intent = new Intent(context, ActivityLogin.class);
        intent.putExtra(ARG_FRAGMENT_TAG,
                FragmentLogin.class.getSimpleName());
        context.startActivity(intent);
    }

    public static void gotoFragmentRegist(Context context) {
        Intent intent = new Intent(context, ActivityLogin.class);
        intent.putExtra(ARG_FRAGMENT_TAG,
                FragmentRegist.class.getSimpleName());
        context.startActivity(intent);
    }

    public static void gotoFragmentForgetPwd(Context context) {
        Intent intent = new Intent(context, ActivityLogin.class);
        intent.putExtra(ARG_FRAGMENT_TAG,
                FragmentForgetPwd.class.getSimpleName());
        context.startActivity(intent);
    }
}
