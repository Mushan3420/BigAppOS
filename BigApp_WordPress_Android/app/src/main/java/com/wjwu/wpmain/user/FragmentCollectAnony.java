package com.wjwu.wpmain.user;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;

import com.joanzapata.android.BaseAdapterHelper;
import com.joanzapata.android.MultiItemTypeSupport;
import com.joanzapata.android.QuickAdapter;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.wjwu.wpmain.cache.DbTableAnonyCollect;
import com.wjwu.wpmain.lib_base.BaseFragmentListNoHeadWithTitleBarSimple;
import com.wjwu.wpmain.util.CommonUtils;
import com.wjwu.wpmain.util.ImageLoaderOptions;
import com.wjwu.wpmain.util.ZToastUtils;
import com.wjwu.wpmain.uzwp.R;
import com.wjwu.wpmain.uzwp.detail.ActivityDetails;

import java.util.ArrayList;

import model.Topic;

/**
 * Created by wjwu on 2015/8/31.
 */
public class FragmentCollectAnony extends BaseFragmentListNoHeadWithTitleBarSimple implements View.OnClickListener {

    private QuickAdapter<Topic> mAdapter = null;
    private MultiItemTypeSupport<Topic> mMultiItemTypeSupport = null;
    private ImageLoader mImageLoader = ImageLoader.getInstance();
    private DisplayImageOptions mOptions = null;

    @Override
    public void findAndBindViews(View contentView) {
        setDefaultImageLeftVisible(true, R.attr.img_nav_back);
        setRightVisible(true, R.string.z_btn_clear);
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
                    ActivityDetails.gotoFragmentDetailsTopicForResult(FragmentCollectAnony.this, topic, 1001);
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
                                if (new DbTableAnonyCollect(mContext.getContentResolver()).delete(topic.ID)) {
                                    ArrayList<Topic> list = new DbTableAnonyCollect(mContext.getContentResolver()).getAllAnonyCollects();
                                    if (list == null) {
                                        list = new ArrayList<>();
                                    }
                                    mAdapter.replaceAll(list);
                                    ZToastUtils.toastMessage(mContext.getApplicationContext(), R.string.z_toast_cancel_success);
                                } else {
                                    ZToastUtils.toastMessage(mContext.getApplicationContext(), R.string.z_toast_cancel_fail);
                                }
                            }
                        }).create().show();
                return true;
            }
        });

        setContent();
    }

    private void setContent() {
        ArrayList<Topic> list = new DbTableAnonyCollect(mContext.getContentResolver()).getAllAnonyCollects();
        if (list == null) {
            list = new ArrayList<>();
        }
        setAdapter(list);
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

    @Override
    public void onPullUp() {
    }

    @Override
    public void onPullDown() {
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == Activity.RESULT_OK) {
            ArrayList<Topic> list = new DbTableAnonyCollect(mContext.getContentResolver()).getAllAnonyCollects();
            if (list == null) {
                list = new ArrayList<>();
            }
            mAdapter.replaceAll(list);
        }
    }

    @Override
    public void onViewClick(View v) {
        int vId = v.getId();
        switch (vId) {
            case R.id.tv_right:
                if (mContext != null && mAdapter.getCount() > 0) {
                    new AlertDialog.Builder(getActivity())
                            .setNegativeButton(R.string.z_btn_cancel, null)
                            .setMessage(R.string.v_tip_clear_annony_collect)
                            .setPositiveButton(R.string.z_btn_confirm, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    new DbTableAnonyCollect(mContext.getContentResolver()).clear();
                                    mAdapter.clear();
                                }
                            }).create().show();
                }
                break;
        }
    }
}