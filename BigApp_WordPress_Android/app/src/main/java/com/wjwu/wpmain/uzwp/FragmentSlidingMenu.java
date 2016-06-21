package com.wjwu.wpmain.uzwp;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.wjwu.wpmain.cache.SpTool;
import com.wjwu.wpmain.lib_base.BaseApplication;
import com.wjwu.wpmain.lib_base.BaseInitFragment;
import com.wjwu.wpmain.user.ActivityUser;
import com.wjwu.wpmain.util.ImageLoaderOptions;
import com.wjwu.wpmain.util.ZToastUtils;
import com.wjwu.wpmain.uzwp.login.ActivityLogin;

import de.greenrobot.event.EventBus;
import event.LoginEvent;


public class FragmentSlidingMenu extends BaseInitFragment implements View.OnClickListener {
    private TextView mTv_username;
    private View v_line_bottom, tv_logout;
    private ImageView mIv_user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.v_fragment_sliding_menu, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTv_username = (TextView) view.findViewById(R.id.tv_username);
        mIv_user = (ImageView) view.findViewById(R.id.iv_user);
        mTv_username.setOnClickListener(this);
//        view.findViewById(R.id.tv_home).setOnClickListener(this);
        view.findViewById(R.id.tv_collect).setOnClickListener(this);
        view.findViewById(R.id.tv_setting).setOnClickListener(this);
        view.findViewById(R.id.tv_commit).setOnClickListener(this);
        view.findViewById(R.id.tv_history_read).setOnClickListener(this);
        mIv_user.setOnClickListener(this);
        tv_logout = view.findViewById(R.id.tv_logout);
        tv_logout.setOnClickListener(this);
        v_line_bottom = view.findViewById(R.id.v_line_bottom);
        freshLoginStatus(null);
    }

    private void freshLoginStatus(String userName) {
        ImageLoader mImageLoader = ImageLoader.getInstance();
        DisplayImageOptions options = ImageLoaderOptions.getOptionsCachedDisk(true);
        if (userName == null) {
            userName = new SpTool(getActivity(), SpTool.SP_USER).getString("userName", null);
        }
        if (userName == null) {
            v_line_bottom.setVisibility(View.GONE);
            tv_logout.setVisibility(View.GONE);
            mTv_username.setText(getString(R.string.v_left_menu_login));
            return;
        }
        mImageLoader.displayImage(new SpTool(getActivity(), SpTool.SP_USER).getString("avatar", null), mIv_user, options);
        v_line_bottom.setVisibility(View.VISIBLE);
        tv_logout.setVisibility(View.VISIBLE);
        mTv_username.setText(userName);
    }

    @Override
    public void onClick(View v) {
        int vId = v.getId();
        switch (vId) {
            case R.id.iv_user:
            case R.id.tv_username:
                if (BaseApplication.getUserId() == null) {
                    ActivityLogin.gotoFragmentLogin(getActivity());
                    return;
                }
                ActivityUser.gotoFragmentFragmentUser(getActivity());
                break;
//            case R.id.tv_home:
//                ((ActivityMainSliding) getActivity()).getSlidingMenu().showContent();
//                break;
            case R.id.tv_collect:
                hideMenu();
                if (BaseApplication.getUserId() == null) {
                    ActivityUser.gotoFragmentCollectAnony(getActivity());
//                    ActivityLogin.gotoFragmentLogin(getActivity());
                    return;
                }
                ActivityUser.gotoFragmentCollect(getActivity());
                break;
            case R.id.tv_commit:
                if (BaseApplication.getUserId() == null) {
                    ActivityLogin.gotoFragmentLogin(getActivity());
                    return;
                }
                hideMenu();
                ActivityUser.gotoFragmentCommit(getActivity());
                break;
            case R.id.tv_history_read:
                hideMenu();
                ActivityUser.gotoFragmentHistoryRead(getActivity());
                break;
            case R.id.tv_setting:
                hideMenu();
                ActivityUser.gotoFragmentSetting(getActivity());
                break;
            case R.id.tv_logout:
                MainApplication.clearLoginInfo();
                freshLoginStatus(null);
                ZToastUtils.toastMessage(getActivity().getApplicationContext(), R.string.z_toast_loginout_success);
                return;
            //TODO 目前不用调用任何接口
//                String logout_url = new SpTool(getActivity(), SpTool.SP_USER).getString("logout_url", "");
//                if (TextUtils.isEmpty(logout_url)) {
//                    ZToastUtils.toastUnknownError(getActivity());
//                    return;
//                }
//                new RequestTools(new ResponseListener(getActivity()) {
//                    @Override
//                    public void onSuccess(Object obj) {
//                        BaseApplication.clearLoginInfo();
//                    }
//
//                    @Override
//                    public void onSuccessError() {
//                    }
//
//                    @Override
//                    public void onError(VolleyError error) {
//                    }
//
//                    @Override
//                    public void onCacheData(Object obj, boolean hasNetwork) {
//                    }
//
//                    @Override
//                    public void onCacheDataError(boolean hasNetwork) {
//                    }
//                }).sendRequest(logout_url, false, Request.Method.GET, null, null, "logout");
//                break;
        }
    }


    private void hideMenu() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    ((ActivityMainSliding) getActivity()).getSlidingMenu().showContent();
                } catch (Exception e) {
                }
            }
        }, 200);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void onEventMainThread(LoginEvent event) {
        freshLoginStatus(event.user.nice_name);
    }
}
