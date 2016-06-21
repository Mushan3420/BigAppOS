package com.wjwu.wpmain.user;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.joanzapata.android.BaseAdapterHelper;
import com.joanzapata.android.MultiItemTypeSupport;
import com.joanzapata.android.QuickAdapter;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.wjwu.wpmain.cache.BaseResponse;
import com.wjwu.wpmain.cache.SettingCache;
import com.wjwu.wpmain.lib_base.BaseFragmentListWithTitleBarSimple;
import com.wjwu.wpmain.net.RequestTools;
import com.wjwu.wpmain.net.RequestToolsSimple;
import com.wjwu.wpmain.net.RequestUrl;
import com.wjwu.wpmain.util.CommonUtils;
import com.wjwu.wpmain.util.ImageLoaderOptions;
import com.wjwu.wpmain.util.ResponseListener;
import com.wjwu.wpmain.util.ResponseListenerSimple;
import com.wjwu.wpmain.util.ZLogUtils;
import com.wjwu.wpmain.util.ZToastUtils;
import com.wjwu.wpmain.uzwp.R;
import com.wjwu.wpmain.uzwp.detail.ActivityDetails;
import com.wjwu.wpmain.widget.DialogSearching;

import java.util.ArrayList;

import model.Topic;

/**
 * Created by wjwu on 2015/8/31.
 */
public class FragmentCollect extends BaseFragmentListWithTitleBarSimple implements View.OnClickListener {

    private QuickAdapter<Topic> mAdapter = null;
    private MultiItemTypeSupport<Topic> mMultiItemTypeSupport = null;
    private ImageLoader mImageLoader = ImageLoader.getInstance();
    private DisplayImageOptions mOptions = null;

    @Override
    public void findAndBindViews(View contentView) {
        setDefaultImageLeftVisible(true, R.attr.img_nav_back);
        setTitleText(R.string.v_left_menu_collect);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                position -= 1;
                if (position < 0) {
                    position = 0;
                }
                Topic topic = mAdapter.getItem(position);
                if (topic != null) {
                    ActivityDetails.gotoFragmentDetailsTopicForResult(FragmentCollect.this, topic, 1001);
                }
            }
        });

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                position -= 1;
                if (position < 0) {
                    position = 0;
                }
                final Topic topic = mAdapter.getItem(position);
                if (topic == null) {
                    return true;
                }
                new AlertDialog.Builder(getActivity())
                        .setNegativeButton(R.string.z_btn_cancel, null)
                        .setMessage(R.string.v_tip_del_collect)
                        .setPositiveButton(R.string.z_btn_confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //TODO
                                sendDelCollectRequest(topic);
                            }
                        }).create().show();
                return true;
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mPullRefreshListView.setRefreshing();
            }
        }, 200); //此处时间控制需要把握好，可能会导致setRefreshing()之后，没有走pulltoFresh
    }

    private DialogSearching mDialogSearching;

    private void sendDelCollectRequest(final Topic topic) {
        if (mDialogSearching == null) {
            mDialogSearching = new DialogSearching(getActivity());
        }
        mDialogSearching.setContent(R.string.z_toast_deleting);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                mDialogSearching.show();
            }
        });
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
                            mAdapter.remove(topic);
                            ZToastUtils.toastMessage(mContext.getApplicationContext(), R.string.z_toast_del_success);
                            return;
                        }
                        errorMsg = result.error_msg;
                    } catch (Exception e) {
                        ZLogUtils.logException(e);
                    }
                }
                ZToastUtils.toastMessage(mContext.getApplicationContext(), errorMsg == null ? getString(R.string.z_toast_del_fail) : errorMsg);
            }

            @Override
            public void onError(VolleyError error, boolean hasNetwork) {
                if (mDialogSearching != null) {
                    mDialogSearching.cancel();
                }
            }
        }).sendRequest(RequestUrl.del_collect + topic.ID, Request.Method.GET, null, "del_collect");
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
        mAdapter = new QuickAdapter<Topic>(getActivity(), list,
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

    private ResponseListener mResponseListener;
    private boolean mRefresh = true;
    private boolean mUseCacheDatas = true;

    private void getTopics(boolean isInit, boolean refresh, final int page) {
        mUseCacheDatas = isInit;
        mRefresh = refresh;
        if (mResponseListener == null) {
            mResponseListener = new ResponseListener<ArrayList<Topic>>(getActivity().getApplicationContext(), mPullRefreshListView) {
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
                    if (mUseCacheDatas && obj != null) { //如果是第一次加载，则先使用缓存
                        mUseCacheDatas = false;
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
        new RequestTools(mResponseListener).sendRequest(RequestUrl.my_collect + "&img_mod=" + (img_mode == 1 ? 3 : img_mode) + "&filter[pre_page]=" + mPageSize + "&page=" + page, refresh, 1, Request.Method.GET, null, new TypeToken<BaseResponse<ArrayList<Topic>>>() {
        }, "my_collect");
    }

    private int mCurrentPage = 1;
    private int mPageSize = 10;

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

    @Override
    public void onPullUp() {
        getTopics(false, false, mCurrentPage + 1);
    }

    @Override
    public void onPullDown() {
        // 获取当前系统时间
        // 执行异步任务获取需要更新的数据
        if (mIsInit) {
            mIsInit = false;
            getTopics(true, true, 1);
            return;
        }
        getTopics(false, true, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == Activity.RESULT_OK) {
            getTopics(false, true, 1);
        }
    }

    @Override
    public void onViewClick(View v) {
        int vId = v.getId();
        switch (vId) {
            case R.id.iv_close:
                break;
        }
    }
}