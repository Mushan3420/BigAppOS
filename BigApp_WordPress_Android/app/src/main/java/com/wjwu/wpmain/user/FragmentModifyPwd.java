package com.wjwu.wpmain.user;

import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;
import com.wjwu.wpmain.cache.BaseResponse;
import com.wjwu.wpmain.cache.SpTool;
import com.wjwu.wpmain.lib_base.BaseFragmentWithTitleBarRightTxt;
import com.wjwu.wpmain.net.RequestTools;
import com.wjwu.wpmain.net.RequestUrl;
import com.wjwu.wpmain.util.ResponseListener;
import com.wjwu.wpmain.util.ZToastUtils;
import com.wjwu.wpmain.uzwp.R;
import com.wjwu.wpmain.widget.DialogSearching;

import java.util.HashMap;

import model.User;

/**
 * Created by wjwu on 2015/8/31.
 */
public class FragmentModifyPwd extends BaseFragmentWithTitleBarRightTxt implements View.OnClickListener {

    private EditText et_old_pwd, et_new_pwd, et_new_pwd_2;

    @Override
    public int initContentView() {
        return R.layout.v_fragment_modify_pwd;
    }

    @Override
    public void findAndBindViews(View contentView) {
        setDefaultImageLeftVisible(true, R.attr.img_nav_back);
        setRightVisible(true, R.string.v_commit);
        setTitleText(R.string.v_user_edit_pwd);

        et_old_pwd = (EditText) contentView.findViewById(R.id.et_old_pwd);
        et_new_pwd = (EditText) contentView.findViewById(R.id.et_new_pwd);
        et_new_pwd_2 = (EditText) contentView.findViewById(R.id.et_new_pwd_2);

        contentView.findViewById(R.id.iv_send).setOnClickListener(this);
    }

    @Override
    public void onViewClick(View v) {
        int vId = v.getId();
        switch (vId) {
            case R.id.tv_right:
            case R.id.iv_send:
                String old_pwd = et_old_pwd.getText().toString();
                String new_pwd = et_new_pwd.getText().toString();
                String new_pwd_2 = et_new_pwd_2.getText().toString();
                if (TextUtils.isEmpty(old_pwd) || TextUtils.isEmpty(new_pwd) || TextUtils.isEmpty(new_pwd_2)) {
                    ZToastUtils.toastMessage(mContext, R.string.z_toast_input_not_null);
                    return;
                }
                if (!new_pwd.equals(new_pwd_2)) {
                    ZToastUtils.toastMessage(mContext, R.string.z_toast_pwd_not_same);
                    return;
                }
                if (old_pwd.equals(new_pwd)) {
                    //TODO
                    return;
                }
                sendEdit(new_pwd);
                break;
        }
    }

    private DialogSearching mDialogSearching;

    /***
     * @param content
     */
    private void sendEdit(final String content) {
        final HashMap<String, String> requestObject = new HashMap<>();
        requestObject.put("password", content);
        if (mDialogSearching == null) {
            mDialogSearching = new DialogSearching(getActivity());
        }
        mDialogSearching.setContent(R.string.z_toast_modifying);
        mDialogSearching.show();
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                new RequestTools(new ResponseListener(mContext.getApplicationContext()) {
                    @Override
                    public void onSuccess(Object obj) {
                        try {
                            if (((BaseResponse<HashMap<String, Object>>) obj).error_code == 0) {
                                final HashMap<String, String> requestObject2 = new HashMap<>();
                                requestObject2.put("log", new SpTool(mContext, SpTool.SP_USER).getString("name", ""));
                                requestObject2.put("pwd", content);
                                new RequestTools(new ResponseListener<User>(mContext.getApplicationContext(), null) {
                                    @Override
                                    public void onSuccess(Object obj) {
                                        if (mDialogSearching != null) {
                                            mDialogSearching.cancel();
                                        }
                                        if (obj == null || mContext == null) {//如果数据没有更新，则直接使用缓存数据
                                            return;
                                        }
                                        ZToastUtils.toastMessage(mContext, R.string.z_toast_modify_success);
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
                                }).sendRequest(RequestUrl.login, false, Request.Method.POST, requestObject2, new TypeToken<BaseResponse<User>>() {
                                }, "login");
                            }
                            return;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (mDialogSearching != null) {
                            mDialogSearching.cancel();
                        }
                        ZToastUtils.toastMessage(mContext, R.string.z_toast_modify_fail);
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
                }).sendRequest(RequestUrl.edit_user_info, false, Request.Method.POST, requestObject, new TypeToken<BaseResponse<HashMap<String, Object>>>() {
                }, "edit_user_info");
            }
        });
    }
}
