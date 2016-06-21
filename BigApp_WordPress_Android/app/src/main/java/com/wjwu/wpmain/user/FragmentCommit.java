package com.wjwu.wpmain.user;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.joanzapata.android.BaseAdapterHelper;
import com.joanzapata.android.QuickAdapter;
import com.wjwu.wpmain.cache.BaseResponse;
import com.wjwu.wpmain.lib_base.BaseFragmentListWithTitleBarSimple;
import com.wjwu.wpmain.net.RequestTools;
import com.wjwu.wpmain.net.RequestToolsSimple;
import com.wjwu.wpmain.net.RequestUrl;
import com.wjwu.wpmain.util.CommonUtils;
import com.wjwu.wpmain.util.ResponseListener;
import com.wjwu.wpmain.util.ResponseListenerSimple;
import com.wjwu.wpmain.util.ZLogUtils;
import com.wjwu.wpmain.util.ZToastUtils;
import com.wjwu.wpmain.uzwp.R;
import com.wjwu.wpmain.widget.DialogSearching;

import java.util.ArrayList;

import model.MyCommit;

/**
 * Created by wjwu on 2015/8/31.
 */
public class FragmentCommit extends BaseFragmentListWithTitleBarSimple implements View.OnClickListener {

    private QuickAdapter<MyCommit> mAdapter = null;

    @Override
    public void findAndBindViews(View contentView) {
        setDefaultImageLeftVisible(true, R.attr.img_nav_back);
        setTitleText(R.string.v_left_menu_commit);

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                position -= 1;
                if (position < 0) {
                    position = 0;
                }
                final MyCommit commit = mAdapter.getItem(position);
                if (commit == null) {
                    return true;
                }
                new AlertDialog.Builder(getActivity())
                        .setNegativeButton(R.string.z_btn_cancel, null)
                        .setMessage(R.string.v_tip_del_commit)
                        .setPositiveButton(R.string.z_btn_confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //TODO
                                sendDelCommitRequest(commit);
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

    private void sendDelCommitRequest(final MyCommit commit) {
        if (mDialogSearching == null) {
            mDialogSearching = new DialogSearching(getActivity());
        }
        mDialogSearching.setContent(R.string.z_toast_deleting);
        mDialogSearching.show();
        new Handler().post(new Runnable() {
            @Override
            public void run() {
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
                                    mAdapter.remove(commit);
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
                }).sendRequest(RequestUrl.del_commit + commit.ID, Request.Method.GET, null, "del_commit");
            }
        });
    }

    private void setAdapter(ArrayList<MyCommit> list) {
        if (mAdapter != null) {
            mAdapter.addAll(list);
            return;
        }
        mAdapter = new QuickAdapter<MyCommit>(getActivity(),
                R.layout.v_layout_item_commit_my, list) {
            @Override
            protected void convert(BaseAdapterHelper helper, MyCommit item) {
                helper.setText(R.id.tv_content, item.content);
                helper.setText(R.id.tv_time, CommonUtils.getOnlyDateFromGmt(item.date_gmt));
                helper.getView(R.id.tv_username).setVisibility(View.GONE);
                Object title = item.post_info.get("title");
                helper.setText(R.id.tv_title, title == null ? "" : title.toString());
                // // helper.getView(R.id.tv_title).setOnClickListener(l)
            }
        };
        mListView.setAdapter(mAdapter);
    }

    private ResponseListener mResponseListener;
    private boolean mRefresh = true;
    private boolean mUseCacheDatas = true;

    private void getMyCommits(boolean isInit, boolean refresh, final int page) {
        mUseCacheDatas = isInit;
        mRefresh = refresh;
        if (mResponseListener == null) {
            mResponseListener = new ResponseListener<ArrayList<MyCommit>>(getActivity().getApplicationContext(), mPullRefreshListView) {
                @Override
                public void onSuccess(Object obj) {
                    if (obj == null) {//如果数据没有更新，则直接使用缓存数据
                        return;
                    }
                    if (mRefresh) {
                        refreshDatas(((BaseResponse<ArrayList<MyCommit>>) obj).data);
                        return;
                    }
                    addDatas(((BaseResponse<ArrayList<MyCommit>>) obj).data);
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
                        refreshDatas(((BaseResponse<ArrayList<MyCommit>>) obj).data);
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
        new RequestTools(mResponseListener).sendRequest(RequestUrl.my_commit + "&filter[pre_page]=" + mPageSize + "&page=" + page, refresh, 1, Request.Method.GET, null, new TypeToken<BaseResponse<ArrayList<MyCommit>>>() {
        }, "my_commit");
    }

    private int mCurrentPage = 1;
    private int mPageSize = 20;

    private void addDatas(ArrayList<MyCommit> list) {
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

    private void refreshDatas(ArrayList<MyCommit> list) {
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
        getMyCommits(false, false, mCurrentPage + 1);
    }

    @Override
    public void onPullDown() {
        // 获取当前系统时间
        // 执行异步任务获取需要更新的数据
        if (mIsInit) {
            mIsInit = false;
            getMyCommits(true, true, 1);
            return;
        }
        getMyCommits(false, true, 1);
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