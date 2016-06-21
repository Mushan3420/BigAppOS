package com.wjwu.wpmain.lib_base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.wjwu.wpmain.util.ThemeAttrTools;

/***
 * 带有标题栏的Fragment
 *
 * @author AG
 */
public abstract class BaseFragmentListWithTitleBarSimple extends BaseFragmentList implements
        OnClickListener {

    protected TextView mTv_title;
    protected ImageView mIv_left, mIv_right;
    private ProgressBar mProgressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.z_fragment_list_with_title_bar_simple,
                container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        View rl_title_bar = view.findViewById(R.id.layout_title_bar);
        mTv_title = (TextView) rl_title_bar.findViewById(R.id.tv_title);
        mIv_left = (ImageView) rl_title_bar.findViewById(R.id.iv_left);
        mIv_right = (ImageView) rl_title_bar.findViewById(R.id.iv_right);
        mProgressBar = (ProgressBar) rl_title_bar.findViewById(R.id.progressBar);
        mIv_left.setVisibility(View.GONE);
        mIv_left.setOnClickListener(this);
        mIv_right.setOnClickListener(this);
        findAndBindViews(view);
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
     * 设置标题栏右边图标是否可见
     *
     * @param visible true or false
     */
    public void setDefaultImageRightVisible(boolean visible) {
        setDefaultImageRightVisible(visible, 0);
    }

    /***
     * 设置标题栏右边图标是否可见, 如果可见使用给定的图片资源
     *
     * @param visible    true or false
     * @param drawableId 图片资源 （0为默认）
     */
    public void setDefaultImageRightVisible(boolean visible, int drawableId) {
        if (!visible) {
            mIv_right.setVisibility(View.GONE);
            return;
        }
        mIv_right.setVisibility(View.VISIBLE);
        if (drawableId > 0) {
            mIv_right.setImageResource(ThemeAttrTools.getValueOfColorAttr(mContext, drawableId));
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
