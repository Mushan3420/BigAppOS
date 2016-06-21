package com.wjwu.wpmain.user;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wjwu.wpmain.cache.BaseResponse;
import com.wjwu.wpmain.cache.SpTool;
import com.wjwu.wpmain.lib_base.BaseFragmentWithTitleBarRightTxt;
import com.wjwu.wpmain.net.RequestTools;
import com.wjwu.wpmain.net.RequestToolsSimple;
import com.wjwu.wpmain.net.RequestUrl;
import com.wjwu.wpmain.util.CommonUtils;
import com.wjwu.wpmain.util.ResponseListener;
import com.wjwu.wpmain.util.ResponseListenerSimple;
import com.wjwu.wpmain.util.ZLogUtils;
import com.wjwu.wpmain.util.ZToastUtils;
import com.wjwu.wpmain.uzwp.R;
import com.wjwu.wpmain.uzwp.login.ActivityLogin;
import com.wjwu.wpmain.widget.DialogSearching;

import java.net.URLEncoder;
import java.util.HashMap;

import de.greenrobot.event.EventBus;
import event.CommitAnonyEvent;
import event.LoginEvent;

/**
 * Created by wjwu on 2015/8/31.
 */
public class FragmentModifyDescription extends BaseFragmentWithTitleBarRightTxt implements View.OnClickListener {

    private EditText et_description;
    private String user_desc;

    @Override
    public int initContentView() {
        return R.layout.v_fragment_modify_description;
    }

    @Override
    public void findAndBindViews(View contentView) {
        setDefaultImageLeftVisible(true, R.attr.img_nav_back);
        setRightVisible(true, R.string.v_commit);
        setTitleText(R.string.v_user_edit_description);

        user_desc = new SpTool(getActivity(), SpTool.SP_USER).getString("description", "");

        et_description = (EditText) contentView.findViewById(R.id.et_description);
        et_description.setText(user_desc);

        contentView.findViewById(R.id.iv_send).setOnClickListener(this);
    }

    @Override
    public void onViewClick(View v) {
        int vId = v.getId();
        switch (vId) {
            case R.id.tv_right:
            case R.id.iv_send:
                String desc = et_description.getText().toString();
                if (TextUtils.isEmpty(desc)) {
                    ZToastUtils.toastMessage(mContext, R.string.z_toast_input_not_null);
                    return;
                }
                if (desc.equals(user_desc)) {
                    //TODO
                    return;
                }
                sendEdit(desc);
                break;
        }
    }

    private DialogSearching mDialogSearching;

    /***
     * @param content
     */
    private void sendEdit(final String content) {
        final HashMap<String, String> requestObject = new HashMap<>();
        requestObject.put("description", content);
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
                        if (mDialogSearching != null) {
                            mDialogSearching.cancel();
                        }
                        try {
                            if (((BaseResponse<HashMap<String, Object>>) obj).error_code == 0) {
                                ZToastUtils.toastMessage(mContext, R.string.z_toast_modify_success);
                                new SpTool(getActivity(), SpTool.SP_USER).putString("description", user_desc);
                            }
                            return;
                        } catch (Exception e) {
                            e.printStackTrace();
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
