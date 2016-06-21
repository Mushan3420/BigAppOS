package com.wjwu.wpmain.uzwp.login;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;
import com.wjwu.wpmain.cache.BaseResponse;
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

import model.User;

/**
 * Created by wjwu on 2015/8/31.
 */
public class FragmentRegist extends BaseInitFragment implements View.OnClickListener {

    private BaseFragmentActivity.FragmentCallBack mCallBack;
    private Context mContext;

    private EditText mEt_username, mEt_pwd, mEt_pwd_again;

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
        return inflater.inflate(R.layout.v_fragment_regist, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mEt_username = (EditText) view.findViewById(R.id.et_username);
        mEt_pwd = (EditText) view.findViewById(R.id.et_pwd);
        mEt_pwd_again = (EditText) view.findViewById(R.id.et_pwd_confirm);
        view.findViewById(R.id.btn_regist).setOnClickListener(this);
        view.findViewById(R.id.iv_close).setOnClickListener(this);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                mEt_username.requestFocus();
                if (mContext != null) {
                    CommonUtils.showSoftKeyBoard(mContext, mEt_username);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        int vId = v.getId();
        switch (vId) {
            case R.id.btn_regist:
                String userName = mEt_username.getText().toString();
                String pwd = mEt_pwd.getText().toString();
                if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(pwd)) {
                    ZToastUtils.toastMessage(mContext.getApplicationContext(), R.string.z_toast_name_pwd_not_null);
                    return;
                }
                if (!pwd.equals(mEt_pwd_again.getText().toString())) {
                    ZToastUtils.toastMessage(mContext.getApplicationContext(), R.string.z_toast_pwd_not_same);
                    return;
                }
                regist(userName, pwd);
                break;
            case R.id.iv_close:
                mCallBack.fragmentChanged(FragmentLogin.class.getSimpleName(), null, true);
                break;
        }
    }

    private DialogSearching mDialogSearching;

    private void regist(final String userName, String pwd) {
        HashMap<String, String> requestObject = new HashMap<>();
        requestObject.put("user_login", userName);
        requestObject.put("password", pwd);
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
        mDialogSearching.setContent(R.string.v_toast_regist_ing);
        mDialogSearching.show();
        new RequestTools(new ResponseListener<User>(mContext.getApplicationContext(), null) {
            @Override
            public void onSuccess(Object obj) {
                if (mDialogSearching != null) {
                    mDialogSearching.cancel();
                }
                if (obj == null || mContext == null) {//如果数据没有更新，则直接使用缓存数据
                    return;
                }
                CommonUtils.hideSoftKeyBoard(mContext.getApplicationContext(), mEt_pwd, mEt_pwd_again, mEt_username);
                ZToastUtils.toastMessage(mContext.getApplicationContext(), R.string.z_toast_regist_success);
                BaseResponse<User> user = (BaseResponse<User>) obj;
                //UID保存么
                Bundle extras = new Bundle();
                extras.putString("username", userName);
                mCallBack.fragmentChanged(FragmentLogin.class.getSimpleName(), extras, true);
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
        }).sendRequest(RequestUrl.regist, false, Request.Method.POST, requestObject, new TypeToken<BaseResponse<User>>() {
        }, "regist");
    }
}
