package com.wjwu.wpmain.uzwp;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.joanzapata.android.BaseAdapterHelper;
import com.joanzapata.android.MultiItemTypeSupport;
import com.joanzapata.android.QuickAdapter;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.wjwu.wpmain.cache.BaseResponse;
import com.wjwu.wpmain.cache.SettingCache;
import com.wjwu.wpmain.lib_base.BaseInitFragment;
import com.wjwu.wpmain.net.RequestTools;
import com.wjwu.wpmain.util.CommonUtils;
import com.wjwu.wpmain.util.ImageLoaderOptions;
import com.wjwu.wpmain.util.ResponseListener;
import com.wjwu.wpmain.util.ZSDKUtils;
import com.wjwu.wpmain.uzwp.detail.ActivityDetails;
import com.wjwu.wpmain.widget.CircleFlowIndicator;
import com.wjwu.wpmain.widget.ZImageSwitcher;

import java.util.ArrayList;
import java.util.List;

import model.Banner;
import model.NavCatalog;
import model.Topic;

/**
 * Created by wjwu on 2015/8/31.
 */
public class FragmentTabContent extends BaseInitFragment {
    private ZImageSwitcher mImageSwitcher;
    private List<Banner> mAdList = new ArrayList<>();
    private ListView mListView;
    private String mBaseTopicUrl = null;
    private QuickAdapter<Topic> mAdapter = null;
    private MultiItemTypeSupport<Topic> mMultiItemTypeSupport = null;
    private ResponseListener mResponseListener;

    private ImageLoader mImageLoader = ImageLoader.getInstance();
    private DisplayImageOptions mOptions = null;

    private int mCurrentPage = 1;
    private int mPageSize = 10;

    private String testTitle = "";
    private boolean mIsInit = true;

    private Context mContext;

    public static FragmentTabContent newInstance(NavCatalog catalog) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("navCatalog", catalog);
        FragmentTabContent fragment = new FragmentTabContent();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.v_fragment_tab_content, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.e("", "wenjun onViewCreated = ");
        initPullRefreshListView(view);
        mListView = mPullRefreshListView.getRefreshableView();
        if (ZSDKUtils.hasGingerbread()) {
            mListView.setOverScrollMode(View.OVER_SCROLL_NEVER); // 可以让List不会滚动超过界限，也没有尽头的亮光效果
        }
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //有两个headview所有要减2
                position -= 1;
                //广告栏再减1
                if (mAdList != null && mAdList.size() > 0) {
                    position -= 1;
                }
                if (position < 0) {
                    position = 0;
                }
                Topic topic = mAdapter.getItem(position);
                if (topic == null) {
                    return;
                }
                ActivityDetails.gotoFragmentDetailsTopic(getActivity(), topic);
            }
        });
        initDatas();
        addHeadView();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //TODO
        mIsInit = false;
        getTopics(true, true, 1);
        //下面这种方式不好呀，快速滑动的时候会导致setRefreshing无效不会调用pulltoFresh了
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                mPullRefreshListView.setRefreshing();
//            }
//        }, 300); //此处时间控制需要把握好，可能会导致setRefreshing()之后，没有走pulltoFresh
    }

    private void initDatas() {
        NavCatalog mNavCatalog = (NavCatalog) getArguments().getSerializable("navCatalog");
        if (mNavCatalog == null) {
            return;
        }
        testTitle = mNavCatalog.name;
        mAdList = mNavCatalog.banner_list;
        mBaseTopicUrl = mNavCatalog.link;
        if (TextUtils.isEmpty(mBaseTopicUrl)) {
            return;
        }
    }

    private void setAdapter(ArrayList<Topic> list) {
        if (mAdapter != null) {
            mAdapter.addAll(list);
            return;
        }
        if (mMultiItemTypeSupport == null) {
            mMultiItemTypeSupport = new MultiItemTypeSupport<Topic>() {
                @Override
                public int getLayoutId(int position, Topic topic) {
                    if (topic != null && topic.featured_image != null) {
                        if (topic.featured_image.size() == 1) {
                            return R.layout.v_layout_item_topic;
                        }
                        if (topic.featured_image.size() > 1) {
                            return R.layout.v_layout_item_topic_img;
                        }
                    }
                    return R.layout.v_layout_item_topic_noimg;
                }

                @Override
                public int getViewTypeCount() {
                    return 3;
                }

                @Override
                public int getItemViewType(int postion, Topic topic) {
                    if (topic != null && topic.featured_image != null) {
                        if (topic.featured_image.size() == 1) {
                            return 2;
                        }
                        if (topic.featured_image.size() > 1) {
                            return 3;
                        }
                    }
                    return 1;
                }
            };
        }

        mOptions = ImageLoaderOptions.getOptionsCachedDisk();
        mAdapter = new QuickAdapter<Topic>(mContext, list,
                mMultiItemTypeSupport) {
            @Override
            protected void convert(BaseAdapterHelper helper, Topic item) {
                helper.setText(R.id.tv_desc, getString(R.string.v_home_item_comment_str, item.views + "", item.comment_num + ""));
                switch (helper.layoutId) {
                    case R.layout.v_layout_item_topic_noimg:
                        helper.setText(R.id.tv_time, CommonUtils.getOnlyDateFromGmt(item));
                        helper.setText(R.id.tv_title, item.title);
                        break;
                    case R.layout.v_layout_item_topic:
                        helper.setText(R.id.tv_time, CommonUtils.getOnlyDateFromGmt(item));
                        helper.setText(R.id.tv_title, item.title);
                        helper.setImageUrl(R.id.iv_img, (img_mode == 1 ? "" : item.featured_image.get(0).source), mImageLoader, mOptions);
                        break;
                    case R.layout.v_layout_item_topic_img:
                        helper.setText(R.id.tv_time, CommonUtils.getOnlyDateFromGmt(item));
                        helper.setText(R.id.tv_title, item.title);
                        helper.setImageUrl(R.id.iv_img_1, (img_mode == 1 ? "" : item.featured_image.get(0).source), mImageLoader, mOptions);
                        helper.setImageUrl(R.id.iv_img_2, (img_mode == 1 ? "" : item.featured_image.get(1).source), mImageLoader, mOptions);
                        helper.setImageUrl(R.id.iv_img_3, (img_mode == 1 ? "" : item.featured_image.get(2).source), mImageLoader, mOptions);
                        break;
                }
            }
        };
        mListView.setAdapter(mAdapter);
    }

    private boolean mRefresh = true;
    private boolean mFirstComming = true;

    private void getTopics(boolean isInit, boolean refresh, int page) {
        if (TextUtils.isEmpty(mBaseTopicUrl)) {
            return;
        }
        mFirstComming = isInit;
        mRefresh = refresh;
        if (mContext == null) {
            return;
        }
        if (mResponseListener == null) {
            mResponseListener = new ResponseListener<ArrayList<Topic>>(mContext.getApplicationContext(), mPullRefreshListView) {
                @Override
                public void onSuccess(Object obj) {
                    if (obj == null) {//如果数据没有更新，则直接使用缓存数据
                        return;
                    }
                    if (mRefresh) {
                        refreshDatas(((BaseResponse<ArrayList<Topic>>) obj).data);
                        return;
                    }
                    addDatas(((BaseResponse<ArrayList<Topic>>) obj).data);
                }

                @Override
                public void onSuccessError() {
                }

                @Override
                public void onError(VolleyError error) {
                }

                @Override
                public void onCacheData(Object obj, boolean hasNetwork) {
                    //下拉刷新才会使用缓存
                    if (mFirstComming && obj != null) { //如果是第一次加载，则先使用缓存
                        refreshDatas(((BaseResponse<ArrayList<Topic>>) obj).data);
                    }
                }

                @Override
                public void onCacheDataError(boolean hasNetwork) {
                }
            };
        }
        if (mResponseListener.isLoading()) {
            return;
        }
        mResponseListener.setIsLoading();
        img_mode = SettingCache.getImgMode(mContext);
        new RequestTools(mResponseListener).sendRequest(mBaseTopicUrl + "&img_mod=" + (img_mode == 1 ? 3 : img_mode) + "&filter[posts_per_page]=" + mPageSize + "&page=" + page, refresh, Request.Method.GET, null, new TypeToken<BaseResponse<ArrayList<Topic>>>() {
        }, testTitle);
    }

    private void addDatas(ArrayList<Topic> list) {
        mCurrentPage++;
        if (list == null || list.size() == 0) {
            setNoNewDatas(true);
            return;
        }
        if (list.size() != mPageSize) {
            setNoNewDatas(true);
        }
        setAdapter(list);
    }

    private void refreshDatas(ArrayList<Topic> list) {
        mCurrentPage = 1;
        if (list == null) {
            list = new ArrayList<>();
        }
        setNoNewDatas(list.size() != mPageSize);
        if (mAdapter != null) {
            mAdapter.replaceAll(list);
            return;
        }
        setAdapter(list);
    }

    private void addHeadView() {
        if (mAdList == null || mAdList.size() == 0) {
            return;
        }
        int size = mAdList.size();
        String[] bannerList = new String[size];
        for (int i = 0; i < size; i++) {
            bannerList[i] = mAdList.get(i).img_url;
        }
        if (mContext == null) {
            return;
        }
        View mFl_ads = LayoutInflater.from(mContext).inflate(
                R.layout.v_layout_ads, null);
        mImageSwitcher = (ZImageSwitcher) mFl_ads.findViewById(R.id.switcher);
        mImageSwitcher.setUri(bannerList);
        mImageSwitcher.setFlowIndicator((CircleFlowIndicator) mFl_ads.findViewById(R.id.indicator));
        mImageSwitcher.startSwitching();
        mImageSwitcher
                .setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        //TODO 点击事件
                        Banner banner = mAdList.get(position);
                        if (banner == null) {
                            return;
                        }
                        ActivityDetails.gotoFragmentDetailsBanner(getActivity(), banner.link, 1, banner.type);
                    }
                });
        mListView.addHeaderView(mFl_ads);
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
        if (mImageSwitcher == null) {
            return;
        }
        if (hidden) {
            mImageSwitcher.stopSwitching();
        } else {
            mImageSwitcher.startSwitching();
        }
    }


    private PullToRefreshListView mPullRefreshListView;

    /**
     * 通过mPullRefreshListView进行配置，然后获得真正的acturalList，使用适配器的应该是这个List
     *
     * @param contentView
     */
    private void initPullRefreshListView(View contentView) {
        mPullRefreshListView = (PullToRefreshListView) contentView
                .findViewById(R.id.listview);
        if (mPullRefreshListView != null) {
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
                            // 获取当前系统时间
                            // 执行异步任务获取需要更新的数据
                            // TODO
                            if (mIsInit) {
                                mIsInit = false;
                                getTopics(true, true, 1);
                                return;
                            }
                            getTopics(false, true, 1);
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
                            getTopics(false, false, mCurrentPage + 1);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
