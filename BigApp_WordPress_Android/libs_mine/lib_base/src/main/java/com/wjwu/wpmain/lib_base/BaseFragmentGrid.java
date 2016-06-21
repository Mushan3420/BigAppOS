package com.wjwu.wpmain.lib_base;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;

import com.handmark.pulltorefresh.library.HeaderGridView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.handmark.pulltorefresh.library.PullToRefreshGridViewHeader;

/**
 * Created by wjwu on 2015/9/5.
 */
public abstract class BaseFragmentGrid extends BaseInitFragment {

    public PullToRefreshGridViewHeader mPullToRefreshGridView;
    public HeaderGridView mGridView;
    public boolean mIsInit = true;

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
    public void onDestroyView() {
        super.onDestroyView();
        mFragmentHasDestroyed = true;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initPullRefreshListView(view);
    }

    /**
     * 通过mPullToRefreshGridView进行配置，然后获得真正的acturalList，使用适配器的应该是这个List
     *
     * @param contentView
     */
    private void initPullRefreshListView(View contentView) {
        mPullToRefreshGridView = (PullToRefreshGridViewHeader) contentView
                .findViewById(R.id.gridview);
        if (mPullToRefreshGridView != null) {
            mGridView = mPullToRefreshGridView.getRefreshableView();
            mPullToRefreshGridView.setMode(PullToRefreshBase.Mode.BOTH); // 上下滑动均可
            mPullToRefreshGridView
                    .setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<HeaderGridView>() {
                        /**
                         * 执行下拉更新逻辑
                         *
                         * @param refreshView
                         */
                        @Override
                        public void onPullDownToRefresh(
                                PullToRefreshBase<HeaderGridView> refreshView) {
                            onPullDown();
                        }

                        /**
                         * 执行上拉分页逻辑，此处不执行
                         *
                         * @param refreshView
                         */
                        @Override
                        public void onPullUpToRefresh(
                                PullToRefreshBase<HeaderGridView> refreshView) {
                            if (noNewDatas) { // 没有数据，就不更新
                                mPullToRefreshGridView.onRefreshComplete();
                                return;
                            }
                            onPullUp();
                        }
                    });
        }
    }

    private void setRefreshListViewVisible(boolean visible) {
        if (mPullToRefreshGridView != null) {
            if (visible) {
                mPullToRefreshGridView.setVisibility(View.VISIBLE);
            } else {
                mPullToRefreshGridView.setVisibility(View.GONE);
            }
        }
    }

    private boolean noNewDatas = false;

    protected void setNoNewDatas(boolean noMoreDatas) {
        noNewDatas = noMoreDatas;
        if (noNewDatas) {
            // 隐藏底部字和图标
            mPullToRefreshGridView.getLoadingLayoutProxy(false, true).hideAll();
            return;
        }
        mPullToRefreshGridView.getLoadingLayoutProxy(false, true).showAll();
    }

    public abstract void onPullUp();

    public abstract void onPullDown();

}
