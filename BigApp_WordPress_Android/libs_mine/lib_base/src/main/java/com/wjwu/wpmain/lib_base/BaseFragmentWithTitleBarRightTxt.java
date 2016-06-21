package com.wjwu.wpmain.lib_base;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.wjwu.wpmain.util.ThemeAttrTools;

/***
 * 带有标题栏的Fragment
 *
 * @author AG
 */
public abstract class BaseFragmentWithTitleBarRightTxt extends BaseInitFragment implements
        OnClickListener {

    private View mDecoratedView;
    protected TextView mTv_title;
    protected ImageView mIv_left;
    protected TextView mTv_right;
    private ProgressBar mProgressBar;

    /***
     * 如果fragment已经destory了，那么子线程中返回后，不做UI处理，防止出现异常
     */
    public boolean mFragmentHasDestroyed = false;

    protected BaseFragmentActivity.FragmentCallBack mCallBack;
    protected Context mContext;

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
        mCallBack = null;
        mContext = null;
        super.onDetach();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.z_fragment_with_title_bar_right_txt,
                container, false);
        FrameLayout view_container = (FrameLayout) contentView
                .findViewById(R.id.container_fragment);
        setCustomContentView(view_container, initContentView());
        return contentView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        View rl_title_bar = view.findViewById(R.id.layout_title_bar);
        mTv_title = (TextView) rl_title_bar.findViewById(R.id.tv_title);
        mIv_left = (ImageView) rl_title_bar.findViewById(R.id.iv_left);
        mTv_right = (TextView) rl_title_bar.findViewById(R.id.tv_right);
        mProgressBar = (ProgressBar) rl_title_bar.findViewById(R.id.progressBar);
        mIv_left.setVisibility(View.GONE);
        mIv_left.setOnClickListener(this);
        mTv_right.setOnClickListener(this);
        findAndBindViews(view);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mFragmentHasDestroyed = true;
    }

    public void setProgressBarVisible(boolean visible) {
        if (mProgressBar == null) {
            return;
        }
        if (visible) {
            mProgressBar.setVisibility(View.VISIBLE);
            return;
        }
        mProgressBar.setVisibility(View.GONE);
    }

    public boolean getProgressIsVisible() {
        if (mProgressBar == null) {
            return false;
        }
        return mProgressBar.getVisibility() == View.VISIBLE;
    }


    /***
     * 设置内容布局
     *
     * @param layoutId layout id
     */
    private void setCustomContentView(FrameLayout view_container, int layoutId) {
        View localView = View.inflate(getActivity(), layoutId, null);
        if (localView != null) {
            if (this.mDecoratedView != null) {
                view_container.removeView(mDecoratedView);
            }
            view_container.addView(localView);
            localView.setId(layoutId);
            mDecoratedView = localView;
        }
    }

    /***
     * 设置标题栏左边图标是否可见
     *
     * @param visible true or false
     */
    public void setDefaultImageLeftVisible(boolean visible) {
        setDefaultImageLeftVisible(visible, 0);
    }

    /***
     * 设置标题栏左边图标是否可见, 如果可见使用给定的图片资源
     *
     * @param visible    true or false
     * @param drawableId 图片资源 （0为默认）
     */
    public void setDefaultImageLeftVisible(boolean visible, int drawableId) {
        if (!visible) {
            mIv_left.setVisibility(View.GONE);
            return;
        }
        mIv_left.setVisibility(View.VISIBLE);
        if (drawableId > 0) {
            mIv_left.setImageResource(ThemeAttrTools.getValueOfColorAttr(mContext, drawableId));
        }
    }

    /***
     * 设置标题栏右边图标是否可见, 如果可见使用给定的图片资源
     *
     * @param visible true or false
     * @param resId   文本资源 （0为默认）
     */
    public void setRightVisible(boolean visible, int resId) {
        if (!visible) {
            mTv_right.setVisibility(View.GONE);
            return;
        }
        mTv_right.setVisibility(View.VISIBLE);
        if (resId > 0) {
            mTv_right.setText(resId);
        }
    }

    /***
     * 设置标题内容
     *
     * @param resid 标题资源文件ID
     */
    public void setTitleText(int resid) {
        mTv_title.setText(resid);
    }

    /***
     * 设置标题内容
     *
     * @param title 标题内容
     */
    public void setTitleText(String title) {
        mTv_title.setText(title);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_left) {
            if (mCallBack != null) {
                mCallBack.fragmentChanged(getTag(), null, true);
            }
        } else {
            onViewClick(v);
        }
    }

    /***
     * 初始化ContentView
     *
     * @return layoutId
     */
    public abstract int initContentView();

    /***
     * 处理布局控件
     */
    public abstract void findAndBindViews(View contentView);

    /***
     * view点击事件
     *
     * @param v view
     */
    public abstract void onViewClick(View v);

}
