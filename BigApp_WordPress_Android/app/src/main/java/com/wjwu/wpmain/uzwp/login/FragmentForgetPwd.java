package com.wjwu.wpmain.uzwp.login;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wjwu.wpmain.lib_base.BaseFragmentActivity;
import com.wjwu.wpmain.lib_base.BaseInitFragment;
import com.wjwu.wpmain.uzwp.R;

/**
 * Created by wjwu on 2015/8/31.
 */
public class FragmentForgetPwd extends BaseInitFragment implements View.OnClickListener {

    private BaseFragmentActivity.FragmentCallBack mCallBack;
    private Context mContext;

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
        view.findViewById(R.id.btn_regist).setOnClickListener(this);
        view.findViewById(R.id.iv_close).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int vId = v.getId();
        switch (vId) {
            case R.id.btn_regist:
                break;
            case R.id.iv_close:
                mCallBack.fragmentChanged(FragmentLogin.class.getSimpleName(), null, true);
                break;
        }
    }
}
