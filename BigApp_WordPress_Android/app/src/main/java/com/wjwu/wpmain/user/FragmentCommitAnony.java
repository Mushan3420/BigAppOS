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
import com.wjwu.wpmain.lib_base.BaseFragmentWithTitleBarRightTxt;
import com.wjwu.wpmain.net.RequestToolsSimple;
import com.wjwu.wpmain.net.RequestUrl;
import com.wjwu.wpmain.util.CommonUtils;
import com.wjwu.wpmain.util.ResponseListenerSimple;
import com.wjwu.wpmain.util.ZLogUtils;
import com.wjwu.wpmain.util.ZToastUtils;
import com.wjwu.wpmain.uzwp.R;
import com.wjwu.wpmain.uzwp.login.ActivityLogin;
import com.wjwu.wpmain.widget.DialogSearching;

import java.net.URLEncoder;

import de.greenrobot.event.EventBus;
import event.CommitAnonyEvent;
import event.LoginEvent;

/**
 * Created by wjwu on 2015/8/31.
 */
public class FragmentCommitAnony extends BaseFragmentWithTitleBarRightTxt implements View.OnClickListener {

    private EditText mEt_name, mEt_email, mEt_reply;
    private int mTopicId;
    private int mComment_type; //1允许匿名评论，2不允许匿名评论（需要加上邮箱和用户名）

    @Override
    public int initContentView() {
        return R.layout.v_fragment_commit_anony;
    }

    @Override
    public void findAndBindViews(View contentView) {
        setDefaultImageLeftVisible(true, R.attr.img_nav_back);
        setRightVisible(true, R.string.v_login);
        setTitleText(R.string.v_title_edit_commit);

        mEt_name = (EditText) contentView.findViewById(R.id.et_username);
        mEt_email = (EditText) contentView.findViewById(R.id.et_email);
        mEt_reply = (EditText) contentView.findViewById(R.id.et_reply);

        contentView.findViewById(R.id.iv_send).setOnClickListener(this);

        mTopicId = getArguments().getInt("topic_id");
        mComment_type = getArguments().getInt("comment_type");
        if (mComment_type != 2) {
            mEt_name.setHint(getString(R.string.v_hint_username) + getString(R.string.v_hint_option));
            mEt_email.setHint(getString(R.string.v_hint_email) + getString(R.string.v_hint_option));
        }
    }

    @Override
    public void onViewClick(View v) {
        int vId = v.getId();
        switch (vId) {
            case R.id.iv_send:
                String name = mEt_name.getText().toString();
                String email = mEt_email.getText().toString();
                String reply = mEt_reply.getText().toString();
                if (TextUtils.isEmpty(reply)) {
                    ZToastUtils.toastMessage(mContext, R.string.z_toast_input_not_null);
                    return;
                }
                if (mComment_type == 2) {
                    if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email)) {
                        ZToastUtils.toastMessage(mContext, R.string.z_toast_name_email_not_null);
                        return;
                    }
                    if (!CommonUtils.checkEmailEnable(email)) {
                        ZToastUtils.toastMessage(mContext, R.string.z_toast_name_email_format_error);
                        return;
                    }
                }
                sendAddCommitRequest(reply, name, email);
                break;
            case R.id.tv_right:
                ActivityLogin.gotoFragmentLogin(mContext);
                break;
        }
    }

    private DialogSearching mDialogSearching;

    private void sendAddCommitRequest(final String content, final String author, final String email) {
        if (mDialogSearching == null) {
            mDialogSearching = new DialogSearching(getActivity());
        }
        mDialogSearching.setContent(R.string.z_toast_commiting);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                mDialogSearching.show();
            }
        });
        String sendContent = content;
        try {
            sendContent = URLEncoder.encode(content, "UTF-8");
        } catch (Exception e) {
            ZLogUtils.logException(e);
        }
        String author_info = "";
        if (!TextUtils.isEmpty(author)) {
            author_info += "&author=" + author;
        }
        if (!TextUtils.isEmpty(email)) {
            author_info += "&email=" + email;
        }
        new RequestToolsSimple(new ResponseListenerSimple(mContext.getApplicationContext()) {
            @Override
            public void onSuccess(String response) {
                if (mDialogSearching != null) {
                    mDialogSearching.cancel();
                }
                String errorMsg = null;
                if (response != null) {
                    try {
                        BaseResponse<Object> result = new Gson().fromJson(response, new TypeToken<BaseResponse<Object>>() {
                        }.getType());
                        if (result.error_code == 0) {
                            ZToastUtils.toastMessage(mContext.getApplicationContext(), R.string.z_toast_commit_success);
                            EventBus.getDefault().post(new CommitAnonyEvent(false, author, email, content));
                            getActivity().finish();
                            return;
                        }
                        errorMsg = result.error_msg;
                    } catch (Exception e) {
                        ZLogUtils.logException(e);
                    }
                }
                ZToastUtils.toastMessage(mContext.getApplicationContext(), errorMsg == null ? getString(R.string.z_toast_commit_fail) : errorMsg);
            }

            @Override
            public void onError(VolleyError error, boolean hasNetwork) {
                if (mDialogSearching != null) {
                    mDialogSearching.cancel();
                }
            }
        }).sendRequest(RequestUrl.add_commit + "&id=" + mTopicId + author_info + "&comment=" + sendContent, Request.Method.POST, null, "add_commit");
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
        EventBus.getDefault().post(new CommitAnonyEvent(true, null, null, null));
        getActivity().finish();
    }
}
