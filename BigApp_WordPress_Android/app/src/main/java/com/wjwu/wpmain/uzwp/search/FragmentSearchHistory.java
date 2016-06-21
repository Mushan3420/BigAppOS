package com.wjwu.wpmain.uzwp.search;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.joanzapata.android.BaseAdapterHelper;
import com.joanzapata.android.QuickAdapter;
import com.wjwu.wpmain.cache.BaseResponse;
import com.wjwu.wpmain.cache.SpTool;
import com.wjwu.wpmain.lib_base.BaseFragment;
import com.wjwu.wpmain.net.RequestTools;
import com.wjwu.wpmain.net.RequestUrl;
import com.wjwu.wpmain.util.CommonUtils;
import com.wjwu.wpmain.util.ResponseListener;
import com.wjwu.wpmain.uzwp.R;
import com.wjwu.wpmain.uzwp.detail.ActivityDetailsMore;

import java.util.ArrayList;
import java.util.List;

import model.TopicTag;

/**
 * Created by wjwu on 2015/8/31.
 */
public class FragmentSearchHistory extends BaseFragment {

    private List<String> mSearchKeyList;
    private View mEmptyView, mFootView;
    private ListView mListView;
    private QuickAdapter mAdapter;
    private View mLine_1, mLine_2, mTv_tag;
    private GridView mGv_tags;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mFootView = inflater.inflate(R.layout.v_footview_search_history, null);
        return inflater.inflate(R.layout.v_fragment_search_history, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        ((ActivitySearch) getActivity()).showKeyboard();
        mTv_tag = view.findViewById(R.id.tv_tag);
        mLine_1 = view.findViewById(R.id.v_line_1);
        mLine_2 = view.findViewById(R.id.v_line_2);
        mGv_tags = (GridView) view.findViewById(R.id.gv_tags);

        mGv_tags.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (mAdapterTag != null) {
                    ActivityDetailsMore.gotoFragmentTagContent(mContext, mAdapterTag.getItem(position));
                }
            }
        });

        mListView = (ListView) view.findViewById(R.id.listview);
        mFootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchKeyList.clear();
                mAdapter.clear();
                freshFootView();
            }
        });
        mEmptyView = view.findViewById(R.id.tv_empty);
        mListView.setEmptyView(mEmptyView);
        String keys = new SpTool(getActivity(), SpTool.SP_SEARCH_KEY_HISTORY).getString("keys", null);
        if (keys == null) {
            mSearchKeyList = new ArrayList<>();
        } else {
            mSearchKeyList = new Gson().fromJson(keys, new TypeToken<List<String>>() {
            }.getType());
        }
        setAdapter();
        loadHotTags();
    }

    private void loadHotTags() {
        new RequestTools(new ResponseListener<ArrayList<TopicTag>>(mContext.getApplicationContext()) {
            @Override
            public void onSuccess(Object obj) {
                if (obj == null) {//如果数据没有更新，则直接使用缓存数据
                    return;
                }
                updateTags(((BaseResponse<ArrayList<TopicTag>>) obj).data);
            }

            @Override
            public void onSuccessError() {
            }

            @Override
            public void onError(VolleyError error) {
            }

            @Override
            public void onCacheData(Object obj, boolean hasNetwork) {
                if (obj != null) {
                    updateTags(((BaseResponse<ArrayList<TopicTag>>) obj).data);
                }
            }

            @Override
            public void onCacheDataError(boolean hasNetwork) {
            }
        }).sendRequest(RequestUrl.post_tags, true, Request.Method.GET, null, new TypeToken<BaseResponse<ArrayList<TopicTag>>>() {
        }, "post_tags");
    }

    private QuickAdapter<TopicTag> mAdapterTag = null;

    private void updateTags(ArrayList<TopicTag> list) {
        if (list == null || list.size() == 0) {
            mGv_tags.setVisibility(View.GONE);
            mTv_tag.setVisibility(View.GONE);
            mLine_1.setVisibility(View.GONE);
            mLine_2.setVisibility(View.GONE);
            return;
        }
        mGv_tags.setVisibility(View.VISIBLE);
        mTv_tag.setVisibility(View.VISIBLE);
        mLine_1.setVisibility(View.VISIBLE);
        mLine_2.setVisibility(View.VISIBLE);
        if (mAdapterTag != null) {
            mAdapterTag.replaceAll(list);
            return;
        }
        mAdapterTag = new QuickAdapter<TopicTag>(mContext,
                R.layout.v_layout_item_tag, list) {
            @Override
            protected void convert(final BaseAdapterHelper helper, TopicTag item) {
                helper.setText(R.id.tv_name, CommonUtils.getMuniteMonthFromGmt(item.name));
            }
        };
        mGv_tags.setAdapter(mAdapterTag);
    }


    private void freshFootView() {
        if (mSearchKeyList.size() == 0) {
            mListView.removeFooterView(mFootView);
            return;
        }
        if (mListView.getFooterViewsCount() == 0) {
            mListView.addFooterView(mFootView);
        }
    }

    private void setAdapter() {
        freshFootView();
        if (mAdapter == null) {
            mAdapter = new QuickAdapter<String>(getActivity(),
                    R.layout.v_layout_item_history, mSearchKeyList) {
                @Override
                protected void convert(BaseAdapterHelper helper, String item) {
                    helper.setText(R.id.tv_content, item);
                    HistoryItemClickListener listener = new HistoryItemClickListener(item);
                    helper.getView(R.id.tv_content).setOnClickListener(listener);
                    helper.getView(R.id.iv_history).setOnClickListener(listener);
                    helper.getView(R.id.iv_search_up).setOnClickListener(listener);
                }
            };
        }
        mListView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    private class HistoryItemClickListener implements View.OnClickListener {
        private String item = "";

        public HistoryItemClickListener(String item) {
            if (item == null) {
                item = "";
            }
            this.item = item;
        }

        @Override
        public void onClick(View view) {
            int vId = view.getId();
            switch (vId) {
                case R.id.iv_search_up:
                    ((ActivitySearch) getActivity()).setSearchKey(item);
                    break;
                case R.id.iv_history:
                case R.id.tv_content:
                    ((ActivitySearch) getActivity()).searchTopics(item);
                    break;
            }
        }
    }

    public void addKey(String key) {
        if (!mSearchKeyList.contains(key)) {
            mSearchKeyList.add(key);
            setAdapter();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        new SpTool(getActivity(), SpTool.SP_SEARCH_KEY_HISTORY).putString("keys", new Gson().toJson(mSearchKeyList));
    }

}
