package com.wjwu.wpmain.uzwp;

import android.app.usage.UsageEvents;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.ScrollView;

import com.wjwu.wpmain.cache.SettingCache;
import com.wjwu.wpmain.lib_base.BaseInitFragment;

import java.lang.reflect.Method;

import de.greenrobot.event.EventBus;
import event.WebTxtChangedEvent;
import model.NavCatalog;

/**
 * Created by wjwu on 2015/9/3.
 */
public class FragmentTabContentCustom extends BaseInitFragment {
    private WebView mWebContent;
    private ProgressBar mPb_line;
    private NavCatalog mNavCatalog;

    //字体更改了，这里也需要同步修改过来，需要有一个广播或者EventBus

    public static FragmentTabContentCustom newInstance(NavCatalog catalog) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("navCatalog", catalog);
        FragmentTabContentCustom fragment = new FragmentTabContentCustom();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.v_fragment_tab_content_custom, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPb_line = (ProgressBar) view.findViewById(R.id.pb_line);
        mWebContent = (WebView) view.findViewById(R.id.webview);

        mNavCatalog = (NavCatalog) getArguments().getSerializable("navCatalog");
        initWebView();
    }

    private void initWebView() {
        if (mNavCatalog == null) {
            return;
        }

        mWebContent.setBackgroundColor(Color.parseColor("#00000000"));
        mWebContent.setWebViewClient(new WebViewViewClient());
        mWebContent.setWebChromeClient(new WebViewChromeClient());
//        mWebContent.getSettings().setJavaScriptEnabled(true);
//        if (ZSDKUtils.hasHoneycomb()) {
//            mWebContent.removeJavascriptInterface("searchBoxJavaBridge_");
//        }
//        mWebContent.setFocusable(false);
        mWebContent.loadUrl(mNavCatalog.link);

        mWebContent.getSettings().setTextSize(SettingCache.getFontSize(getActivity()));
    }

    class WebViewViewClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            mPb_line.setVisibility(View.VISIBLE);
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
