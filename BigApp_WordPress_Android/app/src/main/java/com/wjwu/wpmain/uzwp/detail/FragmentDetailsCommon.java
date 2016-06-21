package com.wjwu.wpmain.uzwp.detail;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.ViewTreeObserver;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.joanzapata.android.BaseAdapterHelper;
import com.joanzapata.android.QuickAdapter;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.wjwu.wpmain.cache.BaseResponse;
import com.wjwu.wpmain.cache.DbTableAnonyCollect;
import com.wjwu.wpmain.cache.DbTableHistoryRead;
import com.wjwu.wpmain.cache.SettingCache;
import com.wjwu.wpmain.cache.SpTool;
import com.wjwu.wpmain.lib_base.BaseApplication;
import com.wjwu.wpmain.lib_base.BaseFragmentWithTitleBarSimple;
import com.wjwu.wpmain.net.CustomConfigure;
import com.wjwu.wpmain.net.RequestTools;
import com.wjwu.wpmain.net.RequestToolsSimple;
import com.wjwu.wpmain.net.RequestUrl;
import com.wjwu.wpmain.share.DialogShare;
import com.wjwu.wpmain.user.ActivityUser;
import com.wjwu.wpmain.util.CommonUtils;
import com.wjwu.wpmain.util.ImageLoaderOptions;
import com.wjwu.wpmain.util.ResponseListener;
import com.wjwu.wpmain.util.ResponseListenerSimple;
import com.wjwu.wpmain.util.TaskExecutor;
import com.wjwu.wpmain.util.ThemeAttrTools;
import com.wjwu.wpmain.util.ZLogUtils;
import com.wjwu.wpmain.util.ZToastUtils;
import com.wjwu.wpmain.uzwp.R;
import com.wjwu.wpmain.uzwp.login.ActivityLogin;
import com.wjwu.wpmain.widget.DialogFontSizeChoice;
import com.wjwu.wpmain.widget.DialogReply;
import com.wjwu.wpmain.widget.DialogSearching;

import net.ag.lib.gallery.ui.MediaPreviewActivity;
import net.ag.lib.gallery.util.MediaInfo;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import de.greenrobot.event.EventBus;
import event.CommitAnonyEvent;
import event.WebTxtChangedEvent;
import model.Author;
import model.HistoryRead;
import model.MyCommit;
import model.Topic;
import model.TopicTag;

/**
 * Created by wjwu on 2015/9/3.
 */
public class FragmentDetailsCommon extends BaseFragmentWithTitleBarSimple {
    private Topic mTopicNew;
    //没有topic只有link,分享时，直接分享此link
    private String mOnlyLink = null;
    private WebView mWebContent;
    private ProgressBar mPb_line;
    private View mLl_bottom, mLl_line, mIv_collect, mTv_newest_commit, mLl_line_inner, mLl_comment;
    private TextView mTv_end;
    private ListView mListView;
    private ScrollView mScrollView;
    private View mTv_edit, mIv_msg, mIv_share;
    private GridView mGv_tags;

    private boolean _isBottomBarVisible = true;
    private TextView mTv_show_orgin;

    /***
     * @param link            获取topic的link
     * @param titlebarVisible
     * @param link_type       1代表获取topic基本信息的url, 2代表获取topic详情的Url
     * @return
     */
    public static FragmentDetailsCommon newInstance(String link, boolean titlebarVisible, boolean bottombarVisible, int link_type) {
        Bundle extras = new Bundle();
        extras.putString("link", link);
        extras.putBoolean("titlebarVisible", titlebarVisible);
        extras.putBoolean("bottombarVisible", bottombarVisible);
        extras.putInt("link_type", link_type);
        extras.putString("banner_type", "2");
        FragmentDetailsCommon fragment = new FragmentDetailsCommon();
        fragment.setArguments(extras);
        return fragment;
    }

    @Override
    public int initContentView() {
        return R.layout.v_fragment_detail;
    }

    @Override
    public void findAndBindViews(final View contentView) {
        setDefaultImageRightVisible(true, R.attr.img_nav_font);
        setDefaultImageLeftVisible(true, R.attr.img_nav_back);
        mScrollView = (ScrollView) contentView.findViewById(R.id.scrollview);
        mPb_line = (ProgressBar) contentView.findViewById(R.id.pb_line);
        mLl_bottom = contentView.findViewById(R.id.ll_bottom);
        mLl_line = contentView.findViewById(R.id.ll_line);
        mTv_newest_commit = contentView.findViewById(R.id.tv_newest_commit);
        mLl_comment = contentView.findViewById(R.id.ll_comment);
        mLl_line_inner = contentView.findViewById(R.id.ll_line_inner);
        mTv_end = (TextView) contentView.findViewById(R.id.tv_end);
        mTv_end.setOnClickListener(this);
        mListView = (ListView) contentView.findViewById(R.id.listview);
        mIv_collect = mLl_bottom.findViewById(R.id.iv_collect);
        mIv_collect.setOnClickListener(this);
        mTv_edit = mLl_bottom.findViewById(R.id.tv_edit);
        mTv_edit.setOnClickListener(this);
        mIv_msg = mLl_bottom.findViewById(R.id.iv_msg);
        mIv_msg.setOnClickListener(this);
        mIv_share = mLl_bottom.findViewById(R.id.iv_share);
        mIv_share.setOnClickListener(this);
        mWebContent = (WebView) contentView.findViewById(R.id.webview);
        mTv_show_orgin = (TextView) contentView.findViewById(R.id.tv_show_orgin);
        mGv_tags = (GridView) contentView.findViewById(R.id.gv_tags);
        mTv_show_orgin.setOnClickListener(this);

        Bundle extras = getArguments();
        if (extras != null) {
            setTitleBarVisible(extras.getBoolean("titlebarVisible", true));
            _isBottomBarVisible = extras.getBoolean("bottombarVisible", true);
            if (!_isBottomBarVisible) {
                mTv_show_orgin.setBackgroundColor(Color.parseColor(ThemeAttrTools.getValueOfColorStrAttr(mContext, R.attr.s_web_bg)));
                mTv_show_orgin.setTextColor(Color.parseColor(ThemeAttrTools.getValueOfColorStrAttr(mContext, R.attr.s_web_txt_c)));
                mTv_show_orgin.getPaint().setUnderlineText(true);
                mLl_comment.setVisibility(View.GONE);
                mLl_bottom.setVisibility(View.GONE);
                mLl_line.setVisibility(View.GONE);
            }
            setTitleBarVisible(extras.getBoolean("titlebarVisible", true));
            setTitleBarVisible(extras.getBoolean("titlebarVisible", true));
            // link_type 1代表获取topic基本信息的url, 2代表获取topic详情的Url
            int linkType = extras.getInt("link_type");
            if (linkType == 1) {
                //banner_type "1"外链, "2"内容
                if ("1".equals(extras.getString("banner_type"))) {
                    mOnlyLink = extras.getString("link");
                    loadWebUrl(mOnlyLink);
                    return;
                }
            }
            initWebView(extras.getString("link"));
        }

        mListView.setFocusable(false);
        mGv_tags.setFocusable(false);
        mGv_tags.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (mAdapterTag != null) {
                    ActivityDetailsMore.gotoFragmentTagContent(mContext, mAdapterTag.getItem(position));
                }
            }
        });
//        mWebContent.setFocusable(false); //导致网页中的输入框无法输入内容

        mDisplay = getActivity().getWindowManager().getDefaultDisplay();

        contentView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @SuppressWarnings("deprecation")
                    @Override
                    public void onGlobalLayout() {
                        if (mDialogReply == null || !mDialogReply.isShowing()) {
                            return;
                        }
                        Rect r = new Rect();
                        contentView.getWindowVisibleDisplayFrame(r);
                        int height = mDisplay.getHeight() - r.bottom;
                        mDialogReply.setLayoutBoardHeight(height);
                        // mListView.smoothScrollBy(-distance, 0);
                        // mListView.smoothScrollByOffset(distance);
                    }
                });
    }

    private Display mDisplay;

    private void initWebView(String link) {
        if (TextUtils.isEmpty(link)) {
            return;
        }
        if (mContext == null) {
            return;
        }
        img_mode = SettingCache.getImgMode(mContext);
        new RequestTools(new ResponseListener<Topic>(mContext.getApplicationContext(), null) {
            @Override
            public void onSuccess(Object obj) {
                if (obj == null) {//如果数据没有更新，则直接使用缓存数据
                    return;
                }
                loadWeb((BaseResponse<Topic>) obj);
            }

            @Override
            public void onSuccessError() {
            }

            @Override
            public void onError(VolleyError error) {
            }

            @Override
            public void onCacheData(Object obj, boolean hasNetwork) {
                loadWeb((BaseResponse<Topic>) obj);
            }

            @Override
            public void onCacheDataError(boolean hasNetwork) {
            }
        }).sendRequest(link + "&img_mod=" + (img_mode == 1 ? 3 : img_mode), true, Request.Method.GET, null, new TypeToken<BaseResponse<Topic>>() {
        }, "link_detail");
    }

    private String modifiedTime = "";

    private void loadWeb(BaseResponse<Topic> topic) {
        if (topic == null) {
            return;
        }

        if (topic.data == null) {
            return;
        }

        mTopicNew = topic.data;
        if (BaseApplication.getUserId() == null) {
            if (new DbTableAnonyCollect(mContext.getContentResolver()).checkIfExits(mTopicNew.ID)) {//如果是匿名收藏,则通过本地判断
                mIv_collect.setSelected(true);
            } else {
                mIv_collect.setSelected(false);
            }
        } else {
            mIv_collect.setSelected(mTopicNew.is_favorited);
        }

        if (_isBottomBarVisible) {
            updateTags(mTopicNew.terms == null ? null : mTopicNew.terms.post_tag);
        }

        //如果修改时间相等的话，就直接使用缓存
        if (!TextUtils.isEmpty(mTopicNew.modified) && modifiedTime.equals(mTopicNew.modified)) {
            return;
        }

        if (mContext != null) {
            new DbTableHistoryRead(mContext.getContentResolver()).saveOrReplace(new HistoryRead(mTopicNew.ID, mTopicNew.title, mTopicNew.link));
        }

        modifiedTime = mTopicNew.modified;
        initWebView();
        updateTopicContentByImageMode();
    }

    private QuickAdapter<TopicTag> mAdapterTag = null;

    private void updateTags(ArrayList<TopicTag> list) {
        if (list == null || list.size() == 0) {
            mGv_tags.setVisibility(View.GONE);
            return;
        }
        mGv_tags.setVisibility(View.VISIBLE);
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

    private void updateTopicContentByImageMode() {
        if (img_mode == 1) {
            TaskExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    Document doc = Jsoup.parse(mTopicNew.content);
                    Elements es = doc.getElementsByTag("img");
                    //解析html将其img标签的src值改成图片路径
                    for (Element e : es) {
                        String imgUrl = e.attr("src");
                        if (TextUtils.isEmpty(imgUrl) || imgUrl.endsWith("gif")) {
                            continue;
                        }
                        try {
                            if (new SpTool(mContext, SpTool.SP_SETTING).getBoolean("moon", false)) {
                                e.attr("src", "file:///android_asset/z_iv_default_load_y.png");
                                e.attr("ori_ing", "file:///android_asset/z_iv_default_loading_y.png");
                            } else {
                                e.attr("src", "file:///android_asset/z_iv_default_load.png");
                                e.attr("ori_ing", "file:///android_asset/z_iv_default_loading.png");
                            }
//                            e.attr("src", "");
//                            e.attr("alt", v_webview_click_download_img);
                            e.attr("ori_link", imgUrl);
//                            Element parent = e.parent();
//                            if (parent.hasAttr("href")) {
//                                parent.attr("href", "javascript:void(0)");
//                            }
//                            e.attr("onclick", "load_img(this)");
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }
                    Topic tmpTopic = mTopicNew;
                    tmpTopic.content = doc.body().html();
                    Message msg = new Message();
                    msg.what = 10;
                    msg.obj = tmpTopic;
                    mHandler.sendMessage(msg);
                }
            });
            return;
        }
        loadWEBJSURL(mTopicNew);
    }


    private void loadWebUrl(String link) {
        if (TextUtils.isEmpty(link)) {
            return;
        }
        initWebView();
        if (mOnlyLink != null && mContext != null) {
            mScrollView.setBackgroundColor(Color.parseColor(ThemeAttrTools.getValueOfColorStrAttr(mContext, R.attr.c_bg_topic_desc_link_str)));
        }
        mWebContent.loadUrl(link);
    }

    private void initWebView() {
        mWebContent.setBackgroundColor(Color.parseColor("#00000000"));
        mWebContent.setWebViewClient(new WebViewViewClient());
        mWebContent.setWebChromeClient(new WebViewChromeClient());
        WebSettings settings = mWebContent.getSettings();

        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setUseWideViewPort(true);//关键点
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setTextSize(SettingCache.getFontSize(mContext));
    }

    private ResponseListener mResponseListener;
    private boolean isCached = false;

    private void loadCommits(int page, boolean cached) {
        isCached = cached;
        if (mContext == null) {
            return;
        }
        if (mResponseListener == null) {
            mResponseListener = new ResponseListener<ArrayList<MyCommit>>(mContext.getApplicationContext(), null) {
                @Override
                public void onSuccess(Object obj) {
                    if (obj == null) {//如果数据没有更新，则直接使用缓存数据
                        return;
                    }
                    loadMoreDatas(((BaseResponse<ArrayList<MyCommit>>) obj).data, isCached);
                }

                @Override
                public void onSuccessError() {
                    mTv_end.setText(R.string.z_toast_loading_fail);
                    mTv_end.setClickable(true);
                }

                @Override
                public void onError(VolleyError error) {
                    mTv_end.setText(R.string.z_toast_loading_fail);
                    mTv_end.setClickable(true);
                }

                @Override
                public void onCacheData(Object obj, boolean hasNetwork) {
                    loadMoreDatas(((BaseResponse<ArrayList<MyCommit>>) obj).data, isCached);
                }

                @Override
                public void onCacheDataError(boolean hasNetwork) {
                }

                @Override
                public void useCacheNotAndNoNetwork() {
                    mTv_end.setText(R.string.z_toast_loading_fail);
                    mTv_end.setClickable(true);
                }
            };
        }
        if (mResponseListener.isLoading()) {
            return;
        }
        mResponseListener.setIsLoading();
        mTv_end.setText(R.string.z_toast_loading);
        mTv_end.setClickable(false);
        new RequestTools(mResponseListener).sendRequest(RequestUrl.topic_commit + mTopicNew.ID + "&filter[pre_page]=" + mPageSize + "&page=" + page, cached, Request.Method.GET, null, new TypeToken<BaseResponse<ArrayList<MyCommit>>>() {
        }, "topic_commit");
    }

    private QuickAdapter<MyCommit> mAdapter = null;
    private ImageLoader mImageLoader = ImageLoader.getInstance();
    private DisplayImageOptions mOptions = null;

    private void setAdapter(ArrayList<MyCommit> list) {
        if (mAdapter != null) {
            mAdapter.addAll(list);
            return;
        }
        mOptions = ImageLoaderOptions.getOptionsCachedDisk(true);
        mAdapter = new QuickAdapter<MyCommit>(mContext,
                R.layout.v_layout_item_commit, list) {
            @Override
            protected void convert(final BaseAdapterHelper helper, MyCommit item) {
                helper.setText(R.id.tv_time, CommonUtils.getMuniteMonthFromGmt(item.date_gmt));
                helper.setText(R.id.tv_username, item.author == null ? "none" : item.author.name);
                helper.setText(R.id.tv_content, item.content);
                helper.setImageUrl(R.id.iv_user, (img_mode == 1 ? "" : (item.author == null ? null : item.author.avatar)), mImageLoader, mOptions);
            }
        };
        mListView.setAdapter(mAdapter);
    }

    private int mCurrentPage = 0;
    private int mPageSize = 10;

    private void loadMoreDatas(ArrayList<MyCommit> list, boolean isRefresh) {
        if (list == null) {
            list = new ArrayList<>();
        }
        if (isRefresh) {
            mCurrentPage = 1;
            if (mAdapter != null) {
                mAdapter.replaceAll(list);
                return;
            }
        }
        mCurrentPage++;
        if (list.size() == 0) {
            if (mAdapter == null || mAdapter.getCount() == 0) {
                mTv_end.setVisibility(View.VISIBLE);
                mTv_end.setText(R.string.v_detail_no_more_commit);
                mTv_end.setClickable(false);
            } else {
                mTv_end.setVisibility(View.GONE);
            }
        } else if (list.size() == mPageSize) {
            mTv_end.setClickable(true);
            mTv_end.setText(R.string.v_detail_show_all_commit);
            mTv_end.setVisibility(View.VISIBLE);
        } else {
            mTv_end.setVisibility(View.GONE);
        }
        setAdapter(list);
    }

    private void addData(MyCommit myCommit) {
        if (mAdapter != null) {
            mAdapter.add(0, myCommit);
        }
        if (mAdapter.getCount() == 0) {
            mTv_end.setText(R.string.v_detail_show_all_commit);
        }
        mScrollView.scrollTo(mLl_comment.getLeft(), mLl_comment.getTop());
    }

    private DialogFontSizeChoice dialogFontSizeChoice;
    private DialogReply mDialogReply;

    private void showDialogReply(View v) {
        if (mDialogReply == null) {
            mDialogReply = new DialogReply(mContext);
            mDialogReply.setSendOnClickListener(new DialogReply.SendOnClickListener() {
                @Override
                public void onClick(View v, String replyText, Topic mTopic) {
                    if (TextUtils.isEmpty(mDialogReply.getReplyText())) {
                        ZToastUtils.toastMessage(mContext, R.string.z_toast_input_not_null);
                        return;
                    }
                    sendAddCommitRequest(mDialogReply.getReplyText(), null, null);
                }
            });
        }
        mDialogReply.show(v, mTopicNew);
    }

    @Override
    public void onViewClick(View v) {
        int vId = v.getId();
        switch (vId) {
            case R.id.iv_right:
                if (dialogFontSizeChoice == null) {
                    dialogFontSizeChoice = new DialogFontSizeChoice(mContext, mWebContent.getSettings().getTextSize(), new DialogFontSizeChoice.OnFontChangedLisener() {
                        @Override
                        public void onFontChanged(WebSettings.TextSize size) {
                            mWebContent.getSettings().setTextSize(size);
                        }
                    });
                }
                dialogFontSizeChoice.show();
                break;
            case R.id.tv_edit:
                if (mTopicNew == null) {
                    return;
                }
                if (mTopicNew.comment_type == 0) {
                    ZToastUtils.toastMessage(mContext.getApplicationContext(), R.string.z_toast_not_allow_commit);
                    return;
                }
                if (BaseApplication.getUserId() == null) {
                    if (mTopicNew.comment_type == 1 || mTopicNew.comment_type == 2) {
                        ActivityUser.gotoFragmentCommitAnony(mContext, mTopicNew.ID, mTopicNew.comment_type);
                        return;
                    }
                    ActivityLogin.gotoFragmentLogin(getActivity());
//                    ZToastUtils.toastMessage(mContext.getApplicationContext(), R.string.z_toast_not_allow_anony_commit);
                    return;
                }
                if (mTopicNew.comment_type == 3) {
                    if (BaseApplication.getUserId().equals("-1")) { //TODO 考虑第三方登录的情况
                        ZToastUtils.toastMessage(mContext.getApplicationContext(), R.string.z_toast_not_allow_3rd_party);
                        return;
                    }
                }
                showDialogReply(v);
                break;
            case R.id.iv_msg:
                if (mTopicNew == null) {
                    return;
                }
                mScrollView.scrollTo(mLl_comment.getLeft(), mLl_comment.getTop());
                break;
            case R.id.iv_collect:
                if (mTopicNew == null) {
                    return;
                }
                if (BaseApplication.getUserId() == null) {
                    //TODO 匿名收藏
                    if (v.isSelected()) {
                        if (new DbTableAnonyCollect(mContext.getContentResolver()).delete(mTopicNew.ID)) {
                            getActivity().setResult(Activity.RESULT_OK);
                            ZToastUtils.toastMessage(mContext.getApplicationContext(), R.string.z_toast_cancel_success);
                            mIv_collect.setSelected(false);
                            return;
                        }
                        ZToastUtils.toastMessage(mContext.getApplicationContext(), R.string.z_toast_cancel_fail);
                    } else {
                        if (new DbTableAnonyCollect(mContext.getContentResolver()).saveOrReplace(mTopicNew)) {
                            getActivity().setResult(Activity.RESULT_OK);
                            ZToastUtils.toastMessage(mContext.getApplicationContext(), R.string.z_toast_collect_add_success);
                            mIv_collect.setSelected(true);
                            return;
                        }
                        ZToastUtils.toastMessage(mContext.getApplicationContext(), R.string.z_toast_collect_add_fail);
                    }
                    return;
                }
                if (v.isSelected()) {
                    sendDelCollectRequest();
                } else {
                    sendAddCollectRequest();
                }
                break;
            case R.id.iv_share:
                showShareDialog();
                break;
            case R.id.tv_end:
                loadCommits(mCurrentPage + 1, false);
                break;
            case R.id.tv_show_orgin:
                //TODO
                if (mTopicNew != null) {
                    ActivityDetails.gotoFragmentDetailsTopic(mContext, mTopicNew);
                }
                break;
        }
    }

    private DialogShare mDialogShare;


    private void showShareDialog() {
        String url = mOnlyLink;
        String title = getString(R.string.share);
        String desc = getString(R.string.share);
        String imageUrl = null;
        if (mTopicNew != null) {
            desc = mTopicNew.title;
            imageUrl = mTopicNew.imageUrl_1;
            url = CustomConfigure.BASE_URL + "?p=" + mTopicNew.ID;
        }
        if (mDialogShare == null) {
            mDialogShare = new DialogShare(getActivity(), title, url, desc, imageUrl, new PlatformActionListener() {
                @Override
                public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                    mHandler.sendEmptyMessage(0);
                }

                @Override
                public void onError(Platform platform, int i, Throwable throwable) {
                    if ("WechatClientNotExistException".equals(throwable.getClass().getSimpleName()) || "WechatTimelineNotSupportedException".equals(throwable.getClass().getSimpleName())) {
                        mHandler.sendEmptyMessage(1);
                        return;
                    }
                    mHandler.sendEmptyMessage(2);
                }

                @Override
                public void onCancel(Platform platform, int i) {
                    mHandler.sendEmptyMessage(3);
                }
            });
        }
        mDialogShare.show();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    ZToastUtils.toastMessage(mContext.getApplicationContext(), getString(R.string.share_completed));
                    break;
                case 1:
                    ZToastUtils.toastMessage(mContext.getApplicationContext(), getString(R.string.wechat_client_inavailable));
                    break;
                case 2:
                    ZToastUtils.toastMessage(mContext.getApplicationContext(), getString(R.string.share_failed));
                    break;
                case 3:
                    ZToastUtils.toastMessage(mContext.getApplicationContext(), getString(R.string.share_canceled));
                    break;
                case 10:
                    loadWEBJSURL((Topic) msg.obj);
                    break;
            }
        }
    };

    private void loadWEBJSURL(Topic topic) {
        mWebContent.addJavascriptInterface(new JsInterfaceTopicDetails(topic) {
            @JavascriptInterface
            @Override
            public void onImageClick(String imageUrl) {
                super.onImageClick(imageUrl);
                MediaInfo info = new MediaInfo();
                info.url = imageUrl;
                info.type = 0;
                ArrayList<MediaInfo> list = new ArrayList<MediaInfo>();
                list.add(info);
                MediaPreviewActivity.gotoPreviewForResult(mContext, list, 0, 0);
            }
        }, "topic");
        mWebContent.loadUrl("file:///android_asset/html/detail_topic.html");
    }

    private DialogSearching mDialogSearching;

    private void sendAddCollectRequest() {
        if (mDialogSearching == null) {
            mDialogSearching = new DialogSearching(getActivity());
        }
        mDialogSearching.setContent(R.string.z_toast_collecting);
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
                            getActivity().setResult(Activity.RESULT_OK);
                            ZToastUtils.toastMessage(mContext.getApplicationContext(), R.string.z_toast_commit_success);
                            mIv_collect.setSelected(true);
                            return;
                        }
                        errorMsg = result.error_msg;
                    } catch (Exception e) {
                        ZLogUtils.logException(e);
                    }
                }
                ZToastUtils.toastMessage(mContext.getApplicationContext(), errorMsg == null ? getString(R.string.z_toast_commit_fail) : errorMsg);
            }

            @Override
            public void onError(VolleyError error, boolean hasNetwork) {
                if (mDialogSearching != null) {
                    mDialogSearching.cancel();
                }
            }
        }).sendRequest(RequestUrl.add_collect + mTopicNew.ID, Request.Method.GET, null, "add_collect");
    }

    private void sendAddCommitRequest(final String content, String author, String email) {
        if (mDialogSearching == null) {
            mDialogSearching = new DialogSearching(getActivity());
        }
        mDialogSearching.setContent(R.string.z_toast_commiting);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                mDialogSearching.show();
            }
        });
        String sendContent = content;
        try {
            sendContent = URLEncoder.encode(content, "UTF-8");
        } catch (Exception e) {
            ZLogUtils.logException(e);
        }
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
                            addCommitSuccess(content, new SpTool(mContext, SpTool.SP_USER).getString("userName", ""));
                            return;
                        }
                        errorMsg = result.error_msg;
                    } catch (Exception e) {
                        ZLogUtils.logException(e);
                    }
                }
                ZToastUtils.toastMessage(mContext.getApplicationContext(), errorMsg == null ? getString(R.string.z_toast_commit_fail) : errorMsg);
            }

            @Override
            public void onError(VolleyError error, boolean hasNetwork) {
                if (mDialogSearching != null) {
                    mDialogSearching.cancel();
                }
            }
        }).sendRequest(RequestUrl.add_commit + "&comment=" + sendContent + "&id=" + mTopicNew.ID + (author == null ? "" : "&author=" + author + "&email=" + email), Request.Method.POST, null, "add_commit");
    }

    private void addCommitSuccess(String content, String author) {
        ZToastUtils.toastMessage(mContext.getApplicationContext(), R.string.z_toast_commit_success);
        MyCommit myCommit = new MyCommit();
        myCommit.author = new Author();
        myCommit.author.name = author;
        myCommit.date_gmt = CommonUtils.gennerateGmtTime();
        myCommit.content = content;
        addData(myCommit);
        mScrollView.scrollTo(mLl_comment.getLeft(), mLl_comment.getTop());
    }

    private void sendDelCollectRequest() {
        if (mDialogSearching == null) {
            mDialogSearching = new DialogSearching(getActivity());
        }
        mDialogSearching.setContent(R.string.z_toast_canceling);
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
                            getActivity().setResult(Activity.RESULT_OK);
                            ZToastUtils.toastMessage(mContext.getApplicationContext(), R.string.z_toast_cancel_success);
                            mIv_collect.setSelected(false);
                            return;
                        }
                        errorMsg = result.error_msg;
                    } catch (Exception e) {
                        ZLogUtils.logException(e);
                    }
                }
                ZToastUtils.toastMessage(mContext.getApplicationContext(), errorMsg == null ? getString(R.string.z_toast_cancel_fail) : errorMsg);
            }

            @Override
            public void onError(VolleyError error, boolean hasNetwork) {
                if (mDialogSearching != null) {
                    mDialogSearching.cancel();
                }
            }
        }).sendRequest(RequestUrl.del_collect + mTopicNew.ID, Request.Method.GET, null, "del_collect");
    }


    private void onLoadFinish() {
        if (mTopicNew == null) {
            return;
        }
        if (_isBottomBarVisible) { //比如首页中，不显示
            mLl_bottom.setVisibility(View.VISIBLE);
            mLl_line.setVisibility(View.VISIBLE);
            loadCommits(1, true);
        } else {
            mTv_show_orgin.setVisibility(View.VISIBLE);
        }
        if (mTopicNew.comment_type == 0) {//0代表不能评论，1允许匿名评论，2不允许匿名评论，3必须是注册用户评论
            return;
        }
        mTv_newest_commit.setVisibility(View.VISIBLE);
        mLl_line_inner.setVisibility(View.VISIBLE);
        mListView.setVisibility(View.VISIBLE);
    }

    class WebViewViewClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            mLl_bottom.setVisibility(View.GONE);
            mLl_line_inner.setVisibility(View.GONE);
            mLl_line.setVisibility(View.GONE);
            mTv_newest_commit.setVisibility(View.GONE);
            mListView.setVisibility(View.GONE);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            mPb_line.setMax(100);
            mPb_line.setVisibility(View.VISIBLE);
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            onLoadFinish();
            if (mContext == null) {
                return;
            }
            String colorBg = ThemeAttrTools.getValueOfColorStrAttr(mContext, R.attr.s_web_bg);
            String colorTxt = ThemeAttrTools.getValueOfColorStrAttr(mContext, R.attr.s_web_txt_c);
            view.loadUrl("javascript:load_theme(\"" + colorBg + "\"" + "," + "\"" + colorTxt + "\")");
//            view.loadUrl("javascript:function() {document.getElementsByTagName('body')[0].style.webkitTextFillColor= " + "'#ff0000" + "'}");
//            view.loadUrl("javascript:function() {document.getElementsByTagName('body')[0].style.background= " + "'ff00ff" + "'}");
        }
    }

    class WebViewChromeClient extends WebChromeClient {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            mPb_line.setProgress(newProgress);
            if (newProgress == 100) {
                mPb_line.setVisibility(View.GONE);
            }
            super.onProgressChanged(view, newProgress);
        }
    }

    public void onEventMainThread(WebTxtChangedEvent event) {
        mWebContent.getSettings().setTextSize(event.textSize);
    }

    public void onEventMainThread(CommitAnonyEvent event) {
        if (event.isLogin) {
            showDialogReply(mLl_bottom);
            return;
        }
        addCommitSuccess(event.content, event.author);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (dialogFontSizeChoice != null) {
            dialogFontSizeChoice.onDestry();
        }
    }
}
