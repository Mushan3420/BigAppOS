package com.wjwu.wpmain.lib_base;

import android.content.Context;
import android.support.v4.app.Fragment;

/**
 * Created by wjwu on 2015/9/2.
 */
public class BaseFragment extends BaseInitFragment {
    protected BaseFragmentActivity.FragmentCallBack mCallBack;
    protected Context mContext;

    /***
     * 如果fragment已经destory了，那么子线程中返回后，不做UI处理，防止出现异常
     */
    protected boolean mFragmentHasDestroyed = false;

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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mFragmentHasDestroyed = true;
    }

}
