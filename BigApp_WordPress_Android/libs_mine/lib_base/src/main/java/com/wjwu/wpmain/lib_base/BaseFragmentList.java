package com.wjwu.wpmain.lib_base;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

/**
 * Created by wjwu on 2015/9/5.
 */
public abstract class BaseFragmentList extends BaseInitFragment {

    public PullToRefreshListView mPullRefreshListView;
    public ListView mListView;
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
     * 通过mPullRefreshListView进行配置，然后获得真正的acturalList，使用适配器的应该是这个List
     *
     * @param contentView
     */
    private void initPullRefreshListView(View contentView) {
        mPullRefreshListView = (PullToRefreshListView) contentView
                .findViewById(R.id.listview);
        if (mPullRefreshListView != null) {
            mListView = mPullRefreshListView.getRefreshableView();
            mPullRefreshListView.setMode(PullToRefreshBase.Mode.BOTH); // 上下滑动均可
            mPullRefreshListView
                    .setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
                        /**
                         * 执行下拉更新逻辑
                         *
                         * @param refreshView
                         */
                        @Override
                        public void onPullDownToRefresh(
                                PullToRefreshBase<ListView> refreshView) {
                            onPullDown();
                        }

                        /**
                         * 执行上拉分页逻辑，此处不执行
                         *
                         * @param refreshView
                         */
                        @Override
                        public void onPullUpToRefresh(
                                PullToRefreshBase<ListView> refreshView) {
                            if (noNewDatas) { // 没有数据，就不更新
                                mPullRefreshListView.onRefreshComplete();
                                return;
                            }
                            onPullUp();
                        }
                    });
        }
    }

    private void setRefreshListViewVisible(boolean visible) {
        if (mPullRefreshListView != null) {
            if (visible) {
                mPullRefreshListView.setVisibility(View.VISIBLE);
            } else {
                mPullRefreshListView.setVisibility(View.GONE);
            }
        }
    }

    private boolean noNewDatas = false;

    protected void setNoNewDatas(boolean noMoreDatas) {
        noNewDatas = noMoreDatas;
        if (noNewDatas) {
            // 隐藏底部字和图标
            mPullRefreshListView.getLoadingLayoutProxy(false, true).hideAll();
            return;
        }
        mPullRefreshListView.getLoadingLayoutProxy(false, true).showAll();
    }

    public abstract void onPullUp();

    public abstract void onPullDown();

}
