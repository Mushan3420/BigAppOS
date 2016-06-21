package com.wjwu.wpmain.lib_base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.wjwu.wpmain.util.ThemeAttrTools;
import com.wjwu.wpmain.widget.CircleFlowIndicator;
import com.wjwu.wpmain.widget.ZImageSwitcher;

import java.util.List;

/***
 * 带有标题栏的Fragment
 *
 * @author AG
 */
public abstract class BaseFragmentWithTitleBar extends BaseFragment implements
        OnClickListener {

    private View mDecoratedView;
    protected TextView mTv_title;
    protected ImageView mIv_left, mIv_right;

    private View mFl_ads;
    private ZImageSwitcher mImageSwitcher;
    private CircleFlowIndicator mCircleFlowIndicator;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.z_fragment_with_title_bar,
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
        mIv_right = (ImageView) rl_title_bar.findViewById(R.id.iv_right);
        mIv_left.setVisibility(View.GONE);
        mIv_left.setOnClickListener(this);
        mIv_right.setOnClickListener(this);
        mFl_ads = view.findViewById(R.id.fl_ads);
        initSwitcher();
        findAndBindViews(view);
    }

    private void initSwitcher() {
        List<String> adUrls = getAdUrls();
        if (adUrls == null || adUrls.size() == 0) {
            mFl_ads.setVisibility(View.GONE);
            return;
        }
        mFl_ads.setVisibility(View.VISIBLE);
        mImageSwitcher = (ZImageSwitcher) mFl_ads.findViewById(R.id.switcher);
        mCircleFlowIndicator = (CircleFlowIndicator) mFl_ads.findViewById(R.id.indicator);

        mImageSwitcher.setUri(adUrls.toArray(new String[0]));
        mImageSwitcher.setFlowIndicator(mCircleFlowIndicator);
        mImageSwitcher.startSwitching();
        mImageSwitcher
                .setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        //TODO 点击事件
                    }
                });
    }

    public List<String> getAdUrls() {
        return null;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mImageSwitcher != null) {
            mImageSwitcher.startSwitching();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mImageSwitcher != null) {
            mImageSwitcher.stopSwitching();
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            mImageSwitcher.stopSwitching();
        } else {
            mImageSwitcher.startSwitching();
        }
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
