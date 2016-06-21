package com.wjwu.wpmain.uzwp.login;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;
import com.wjwu.wpmain.cache.BaseResponse;
import com.wjwu.wpmain.cache.FileCache;
import com.wjwu.wpmain.cache.SpTool;
import com.wjwu.wpmain.lib_base.BaseFragmentActivity;
import com.wjwu.wpmain.lib_base.BaseInitFragment;
import com.wjwu.wpmain.net.RequestTools;
import com.wjwu.wpmain.net.RequestUrl;
import com.wjwu.wpmain.util.CommonUtils;
import com.wjwu.wpmain.util.ResponseListener;
import com.wjwu.wpmain.util.ZToastUtils;
import com.wjwu.wpmain.uzwp.R;
import com.wjwu.wpmain.widget.DialogSearching;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.PlatformDb;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import de.greenrobot.event.EventBus;
import event.LoginEvent;
import model.ConfInfo;
import model.User;

/**
 * Created by wjwu on 2015/8/31.
 */
public class FragmentLogin extends BaseInitFragment implements View.OnClickListener {

    private BaseFragmentActivity.FragmentCallBack mCallBack;
    private Context mContext;

    private EditText mEt_username;
    private EditText mEt_pwd;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        try {
            mCallBack = (BaseFragmentActivity.FragmentCallBack) getActivity();
        } catch (Exception e) {
            // e.printStackTrace();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallBack = null;
        mContext = null;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.v_fragment_login, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mEt_username = (EditText) view.findViewById(R.id.et_username);
        mEt_pwd = (EditText) view.findViewById(R.id.et_pwd);
        View tv_regist = view.findViewById(R.id.tv_regist);
        tv_regist.setOnClickListener(this);
        view.findViewById(R.id.tv_forgetpwd).setOnClickListener(this);
        view.findViewById(R.id.btn_login).setOnClickListener(this);
        view.findViewById(R.id.iv_wechat).setOnClickListener(this);
        view.findViewById(R.id.iv_qq).setOnClickListener(this);
        view.findViewById(R.id.iv_sina).setOnClickListener(this);
        view.findViewById(R.id.iv_close).setOnClickListener(this);

        ConfInfo info = FileCache.getConf();
        if (info != null && !"1".equals(info.users_can_register)) {
            tv_regist.setVisibility(View.GONE);
        }

        if (getArguments() != null) {
            String userName = getArguments().getString("username");
            if (!TextUtils.isEmpty(userName)) {
                mEt_username.setText(userName);
                mEt_pwd.requestFocus();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.showSoftKeyBoard(mContext, mEt_pwd);
                    }
                }, 200);
            }
        }
    }

    @Override
    public void onClick(View v) {
        int vId = v.getId();
        switch (vId) {
            case R.id.tv_regist:
                mCallBack.fragmentChanged(FragmentRegist.class.getSimpleName(), null, false);
                break;
            case R.id.tv_forgetpwd:
                //TODO
//                mCallBack.fragmentChanged(FragmentForgetPwd.class.getSimpleName(), null, false);
                break;
            case R.id.iv_wechat:
                ShareSDK.initSDK(mContext);
                authorize(ShareSDK.getPlatform(Wechat.NAME), "wechat");
                break;
            case R.id.iv_qq:
                ShareSDK.initSDK(mContext);
                authorize(ShareSDK.getPlatform(QZone.NAME), "qq");
                break;
            case R.id.iv_sina:
                ShareSDK.initSDK(mContext);
                authorize(ShareSDK.getPlatform(SinaWeibo.NAME), "sina");
                break;
            case R.id.iv_close:
                getActivity().onBackPressed();
                break;
            case R.id.btn_login:
                String userName = mEt_username.getText().toString();
                String pwd = mEt_pwd.getText().toString();
                if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(pwd)) {
                    ZToastUtils.toastMessage(mContext.getApplicationContext(), R.string.z_toast_name_pwd_not_null);
                    return;
                }
                login(userName, pwd);
                break;
        }
    }

    private void loginSuccess(User user) {
        mContext.startService(new Intent(mContext, ServiceSyncAfterLogin.class));
        Log.e("", "wenjun ServiceSyncAfterLogin loginSuccess = ");
        new SpTool(mContext, SpTool.SP_USER).putString("userId", user.id + "");
        new SpTool(mContext, SpTool.SP_USER).putString("name", user.username);
        new SpTool(mContext, SpTool.SP_USER).putString("userName", user.nice_name);
        new SpTool(mContext, SpTool.SP_USER).putString("avatar", user.avatar);
        new SpTool(mContext, SpTool.SP_USER).putString("description", user.description);
        new SpTool(mContext, SpTool.SP_USER).putString("logout_url", user.logout_url);
        new SpTool(mContext, SpTool.SP_USER).putString("email", user.email);
        ZToastUtils.toastMessage(mContext.getApplicationContext(), R.string.z_toast_login_success);
        EventBus.getDefault().post(new LoginEvent(user));
        getActivity().finish();
    }

    private void login(String userName, String pwd) {
        final HashMap<String, String> requestObject = new HashMap<>();
        requestObject.put("log", userName);
        requestObject.put("pwd", pwd);
        try {
            if (((ActivityLogin) getActivity()).bind == 1) {
                requestObject.put("bind", ((ActivityLogin) getActivity()).bind + "");
                requestObject.put("platform", ((ActivityLogin) getActivity()).platform);
                requestObject.put("openid", ((ActivityLogin) getActivity()).openid);
                requestObject.put("token", ((ActivityLogin) getActivity()).token);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mDialogSearching == null) {
            mDialogSearching = new DialogSearching(getActivity());
        }
        mDialogSearching.setContent(R.string.v_toast_login_ing);
        mDialogSearching.show();
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                new RequestTools(new ResponseListener<User>(mContext.getApplicationContext(), null) {
                    @Override
                    public void onSuccess(Object obj) {
                        if (mDialogSearching != null) {
                            mDialogSearching.cancel();
                        }
                        if (obj == null || mContext == null) {//如果数据没有更新，则直接使用缓存数据
                            return;
                        }
                        loginSuccess(((BaseResponse<User>) obj).data);
                    }

                    @Override
                    public void onSuccessError() {
                        if (mDialogSearching != null) {
                            mDialogSearching.cancel();
                        }
                    }

                    @Override
                    public void onError(VolleyError error) {
                        if (mDialogSearching != null) {
                            mDialogSearching.cancel();
                        }
                    }

                    @Override
                    public void useCacheNotAndNoNetwork() {
                        if (mDialogSearching != null) {
                            mDialogSearching.cancel();
                        }
                    }

                    @Override
                    public void onCacheData(Object obj, boolean hasNetwork) {
                    }

                    @Override
                    public void onCacheDataError(boolean hasNetwork) {
                    }
                }).sendRequest(RequestUrl.login, false, Request.Method.POST, requestObject, new TypeToken<BaseResponse<User>>() {
                }, "login");
            }
        });
    }

    private String mPlatFormName = "";

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    Platform platform = (Platform) msg.obj;
                    String platformName = "";
                    if (Wechat.NAME.equals(platform.getName())) {
                        platformName = "wechat";
                    } else if (QZone.NAME.equals(platform.getName())) {
                        platformName = "qq";
                    } else if (SinaWeibo.NAME.equals(platform.getName())) {
                        platformName = "sina";
                    }
                    checkBind(platform.getDb(), platformName);
                    break;
                case 1:
                    ZToastUtils.toastMessage(mContext.getApplicationContext(), getString(R.string.wechat_client_inavailable));
                    break;
                case 2:
                    ZToastUtils.toastMessage(mContext.getApplicationContext(), getString(R.string.v_toast_third_sso_fail));
                    break;
                case 3:
                    ZToastUtils.toastMessage(mContext.getApplicationContext(), getString(R.string.v_toast_third_sso_cancel));
                    break;
            }
        }
    };

    //执行授权,获取用户信息
    //文档：http://wiki.mob.com/Android_%E8%8E%B7%E5%8F%96%E7%94%A8%E6%88%B7%E8%B5%84%E6%96%99
    private void authorize(final Platform plat, final String platformName) {
        //判断指定平台是否已经完成授权
        if (plat.isAuthValid()) {
            String userId = plat.getDb().getUserId();
            if (userId != null) {
                checkBind(plat.getDb(), platformName);
                return;
            }
        }
        plat.setPlatformActionListener(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int action, HashMap<String, Object> hashMap) {
                Log.e("", "wenjun authorize onComplete action = " + action);
                if (action == Platform.ACTION_USER_INFOR) {
                    Message msg = new Message();
                    msg.obj = platform;
                    msg.what = 0;
                    mHandler.sendMessage(msg);
                }
            }

            @Override
            public void onError(Platform platform, int action, Throwable throwable) {
                Log.e("", "wenjun authorize onError action = " + action);
                throwable.printStackTrace();
                if ("WechatClientNotExistException".equals(throwable.getClass().getSimpleName()) || "WechatTimelineNotSupportedException".equals(throwable.getClass().getSimpleName())) {
                    mHandler.sendEmptyMessage(1);
                    return;
                }
                mHandler.sendEmptyMessage(2);
            }

            @Override
            public void onCancel(Platform platform, int action) {
                mHandler.sendEmptyMessage(3);
            }
        });
        // true不使用SSO授权，false使用SSO授权
        plat.SSOSetting(false);
        plat.showUser(null);
    }

    private DialogSearching mDialogSearching;

    private void checkBind(final PlatformDb db, final String platformName) {
        if (db == null) {
            return;
        }
        final HashMap<String, String> requestObject = new HashMap<>();
        requestObject.put("platform", platformName);
        requestObject.put("openid", db.getUserId());
        requestObject.put("token", db.getToken());
        if (mDialogSearching == null) {
            mDialogSearching = new DialogSearching(getActivity());
        }
        mDialogSearching.setContent(R.string.v_toast_login_ing);
        mDialogSearching.show();
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                new RequestTools(new ResponseListener(mContext.getApplicationContext()) {
                    @Override
                    public void onSuccess(Object obj) {
                        if (mDialogSearching != null) {
                            mDialogSearching.cancel();
                        }
                        HashMap<String, Object> response = ((BaseResponse<HashMap<String, Object>>) obj).data;
                        if ((int) Double.parseDouble(response.get("hasbind") + "") == 1) {//已经绑定
                            User user = new User();
                            user.id = Integer.parseInt(response.get("uid") + "");
                            user.username = (String) response.get("username");
                            user.nice_name = (String) response.get("nice_name");
                            user.description = (String) response.get("description");
                            user.avatar = (String) response.get("avatar");
                            user.logout_url = "";
                            loginSuccess(user);
                            return;
                        }
                        if (mContext == null) {
                            return;
                        }
                        try {
                            ((ActivityLogin) getActivity()).bind = 1;
                            ((ActivityLogin) getActivity()).platform = platformName;
                            ((ActivityLogin) getActivity()).openid = db.getUserId();
                            ((ActivityLogin) getActivity()).token = db.getToken();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //TODO 未绑定时，提示用户去登录或者去注册
                        new AlertDialog.Builder(mContext)
                                .setMessage(R.string.v_tip_third_login_bind)
                                .setPositiveButton(R.string.v_login_verb, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        mEt_username.requestFocus();
                                        if (mContext != null) {
                                            CommonUtils.showSoftKeyBoard(mContext, mEt_username);
                                        }
                                    }
                                })
                                .setNegativeButton(R.string.v_regist_verb, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        mCallBack.fragmentChanged(FragmentRegist.class.getSimpleName(), null, false);
                                    }
                                })
                                .create().show();
                    }

                    @Override
                    public void onSuccessError() {
                        if (mDialogSearching != null) {
                            mDialogSearching.cancel();
                        }
                    }

                    @Override
                    public void onError(VolleyError error) {
                        if (mDialogSearching != null) {
                            mDialogSearching.cancel();
                        }
                    }

                    @Override
                    public void useCacheNotAndNoNetwork() {
                        if (mDialogSearching != null) {
                            mDialogSearching.cancel();
                        }
                    }

                    @Override
                    public void onCacheData(Object obj, boolean hasNetwork) {
                    }

                    @Override
                    public void onCacheDataError(boolean hasNetwork) {
                    }
                }).sendRequest(RequestUrl.third_login_check, false, Request.Method.POST, requestObject, new TypeToken<BaseResponse<HashMap<String, Object>>>() {
                }, "third_login_check");
            }
        });
    }
}
