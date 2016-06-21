package com.wjwu.wpmain.user;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.AdapterView;

import com.joanzapata.android.BaseAdapterHelper;
import com.joanzapata.android.QuickAdapter;
import com.wjwu.wpmain.cache.DbTableHistoryRead;
import com.wjwu.wpmain.lib_base.BaseFragmentListNoHeadWithTitleBarSimple;
import com.wjwu.wpmain.uzwp.R;
import com.wjwu.wpmain.uzwp.detail.ActivityDetails;

import java.util.ArrayList;

import model.HistoryRead;

/**
 * Created by wjwu on 2015/8/31.
 */
public class FragmentHistoryRead extends BaseFragmentListNoHeadWithTitleBarSimple implements View.OnClickListener {

    private QuickAdapter<HistoryRead> mAdapter = null;

    @Override
    public void findAndBindViews(View contentView) {
        setDefaultImageLeftVisible(true, R.attr.img_nav_back);
        setRightVisible(true, R.string.z_btn_clear);
        setTitleText(R.string.v_left_menu_history_read);

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                position -= 1;
                if (position < 0) {
                    position = 0;
                }
                final HistoryRead historyRead = mAdapter.getItem(position);
                if (historyRead != null && !historyRead.isNewDay) {
                    new AlertDialog.Builder(getActivity())
                            .setNegativeButton(R.string.z_btn_cancel, null)
                            .setMessage(R.string.v_tip_del_history_read)
                            .setPositiveButton(R.string.z_btn_confirm, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //TODO
                                    delHistoryRead(historyRead);
//                                    HistoryRead historyReadTime = mAdapter.getItem(historyRead.timePosition);
//                                    Log.e("", "wenjun position = " + historyRead.timePosition + ", historyReadTime = " + historyReadTime);
//                                    if (historyReadTime != null && historyReadTime.newDaySize > 0) {
//                                        historyReadTime.newDaySize -= 1;
//                                    }
//                                    mAdapter.remove(historyRead);
                                }
                            }).create().show();
                }
                return true;
            }
        });
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                position -= 1;
                if (position < 0) {
                    position = 0;
                }
                HistoryRead historyRead = mAdapter.getItem(position);
                if (historyRead == null || historyRead.isNewDay) {
                    return;
                }
                ActivityDetails.gotoFragmentDetailsBanner(mContext, historyRead.link, 2, "2");
            }
        });
        ArrayList<HistoryRead> list = new DbTableHistoryRead(mContext.getContentResolver()).getLatelyHistorys();
        if (list == null) {
            list = new ArrayList<>();
        }
        setAdapter(list);
    }

    private void delHistoryRead(HistoryRead obj) {
        if (mAdapter != null && obj != null) {
            new DbTableHistoryRead(mContext.getContentResolver()).delete(obj._id);
            ArrayList<HistoryRead> list = new DbTableHistoryRead(mContext.getContentResolver()).getLatelyHistorys();
            mAdapter.replaceAll(list);
        }
    }

    private void setAdapter(ArrayList<HistoryRead> list) {
        if (mAdapter != null) {
            mAdapter.addAll(list);
            return;
        }
        mAdapter = new QuickAdapter<HistoryRead>(getActivity(),
                R.layout.v_layout_item_history_read, list) {
            @Override
            protected void convert(BaseAdapterHelper helper, HistoryRead item) {
                if (item.isNewDay) {
                    helper.getView(R.id.tv_content).setVisibility(View.GONE);
                    helper.getView(R.id.tv_time).setVisibility(View.VISIBLE);
                    helper.setText(R.id.tv_time, getString(R.string.v_history_read_item_tiem, item.createDate, item.newDaySize));
                    return;
                }
                helper.getView(R.id.tv_content).setVisibility(View.VISIBLE);
                helper.getView(R.id.tv_time).setVisibility(View.GONE);
                helper.setText(R.id.tv_content, item.title);
            }
        };
        mListView.setAdapter(mAdapter);
    }

//    private int mCurrentPage = 1;
//    private int mPageSize = 20;
//
//    private void addDatas(ArrayList<MyCommit> list) {
//        mCurrentPage++;
//        if (list == null || list.size() == 0) {
//            setNoNewDatas(true);
//            return;
//        }
//        if (list.size() != mPageSize) {
//            setNoNewDatas(true);
//        }
//        setAdapter(list);
//    }
//
//    private void refreshDatas(ArrayList<MyCommit> list) {
//        mCurrentPage = 1;
//        if (list == null) {
//            list = new ArrayList<>();
//        }
//        setNoNewDatas(list.size() != mPageSize);
//        if (mAdapter != null) {
//            mAdapter.replaceAll(list);
//            return;
//        }
//        setAdapter(list);
//    }

    @Override
    public void onPullUp() {
    }

    @Override
    public void onPullDown() {
    }

    @Override
    public void onViewClick(View v) {
        int vId = v.getId();
        switch (vId) {
            case R.id.tv_right:
                if (mContext != null && mAdapter.getCount() > 0) {
                    new AlertDialog.Builder(getActivity())
                            .setNegativeButton(R.string.z_btn_cancel, null)
                            .setMessage(R.string.v_tip_clear_history_read)
                            .setPositiveButton(R.string.z_btn_confirm, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    new DbTableHistoryRead(mContext.getContentResolver()).clear();
                                    mAdapter.clear();
                                }
                            }).create().show();
                }
                break;
        }
    }
}