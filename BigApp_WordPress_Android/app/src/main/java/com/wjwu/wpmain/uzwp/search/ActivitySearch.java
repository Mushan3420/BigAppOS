package com.wjwu.wpmain.uzwp.search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;
import com.wjwu.wpmain.cache.BaseResponse;
import com.wjwu.wpmain.cache.SettingCache;
import com.wjwu.wpmain.cache.SpTool;
import com.wjwu.wpmain.lib_base.BaseFragmentActivity;
import com.wjwu.wpmain.net.RequestTools;
import com.wjwu.wpmain.net.RequestUrl;
import com.wjwu.wpmain.util.CommonUtils;
import com.wjwu.wpmain.util.ResponseListener;
import com.wjwu.wpmain.util.ZLogUtils;
import com.wjwu.wpmain.util.ZToastUtils;
import com.wjwu.wpmain.uzwp.R;
import com.wjwu.wpmain.widget.DialogSearching;

import java.net.URLEncoder;
import java.util.ArrayList;

import model.Topic;

/**
 * Created by wjwu on 2015/8/31.
 */
public class ActivitySearch extends BaseFragmentActivity implements BaseFragmentActivity.FragmentCallBack {
    private String mCurrentFragmentTag;
    private Fragment mCurrentFragment;

    private EditText mEt_content;
    private View mIv_search;

    private boolean mIsFirstComming = true;

    private ArrayList<Topic> mTempTopicList = null;
    private ArrayList<Topic> mCachedTopicList = null;

    private String mLastKnownKey = "";

    private DialogSearching mDialogSearching;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        if (new SpTool(this, SpTool.SP_SETTING).getBoolean("moon", false)) {
            setTheme(R.style.ActivityThemeResizeMoon);
        } else {
            setTheme(R.style.ActivityThemeResize);
        }
        setContentView(R.layout.v_activity_search);
        mIv_search = findViewById(R.id.iv_search);
        findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mEt_content = (EditText) findViewById(R.id.et_content);
        mEt_content.setOnTouchListener(new View.OnTouchListener() {
            //按住和松开的标识
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    //在搜索结果页面，点击编辑框的时候，跳转到搜索历史界面
                    if (mCurrentFragmentTag.equals(FragmentSearchResult.class.getSimpleName())) {
                        fragmentChanged(FragmentSearchHistory.class.getSimpleName(), null, false);
                    }
                }
                return false;
            }
        });
        mEt_content.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String key = mEt_content.getText().toString();
                    if (TextUtils.isEmpty(key) || mCurrentFragmentTag.equals(FragmentSearchResult.class.getSimpleName())) {
                        return false;
                    }
                    //TODO 搜索数据后再跳转
                    if (mCurrentFragment != null && mCurrentFragment instanceof FragmentSearchHistory) {
                        ((FragmentSearchHistory) mCurrentFragment).addKey(key);
                    }
                    //TODO show dialog
                    searchTopics(key);
                }
                return false;
            }
        });
        loadFragment();
    }

    private void loadFragment() {
        if (getIntent() != null) {
            mCurrentFragmentTag = getIntent().getStringExtra(ARG_FRAGMENT_TAG);
            Bundle extras = getIntent().getExtras();
            mCurrentFragment = new FragmentSearchHistory();
            showFragment(R.id.container_fragment, mCurrentFragment, extras, FragmentSearchHistory.class.getSimpleName());
        } else {
            super.onBackPressed();
        }
    }

    protected void showKeyboard() {
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                CommonUtils.showSoftKeyBoard(ActivitySearch.this, mEt_content);
            }
        }, 500);
    }

    private void doWithSearchResult(ArrayList<Topic> topics, String key) {
        mDialogSearching.cancel();
        if (topics == null || topics.size() == 0) {
            ZToastUtils.toastMessage(getApplicationContext(), R.string.z_toast_search_result_nothing);
            return;
        }
        mLastKnownKey = key;
        if (mTempTopicList == null) {
            mTempTopicList = new ArrayList<>();
        }
        mTempTopicList.clear();
        mTempTopicList.addAll(topics);
        gotoSearchResultFragment();
    }

    private void gotoSearchResultFragment() {
        mEt_content.setText(mLastKnownKey);
        mEt_content.setSelection(mLastKnownKey.length());
        Bundle extras = new Bundle();
        extras.putSerializable("topics", mTempTopicList);
        extras.putSerializable("key", mLastKnownKey);
        fragmentChanged(FragmentSearchResult.class.getSimpleName(), extras, false);
    }

    protected void setSearchKey(String key) {
        mEt_content.setText(key);
        mEt_content.setSelection(key.length());
    }

    protected void searchTopics(final String key) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        if (mDialogSearching == null) {
            mDialogSearching = new DialogSearching(this);
        }
        mDialogSearching.show();
        mEt_content.setText(key);
        mEt_content.setSelection(key.length());
        String sendContent = key;
        try {
            sendContent = URLEncoder.encode(key, "UTF-8");
        } catch (Exception e) {
            ZLogUtils.logException(e);
        }
        int mode = SettingCache.getImgMode(this);
        new RequestTools(new ResponseListener<ArrayList<Topic>>(getApplicationContext(), null) {
            @Override
            public void onSuccess(Object obj) {
                if (obj == null) {//如果数据没有更新，则直接使用缓存数据
                    doWithSearchResult(mCachedTopicList, key);
                    return;
                }
                doWithSearchResult(((BaseResponse<ArrayList<Topic>>) obj).data, key);
            }

            @Override
            public void onSuccessError() {
                mDialogSearching.cancel();
            }

            @Override
            public void onError(VolleyError error) {
                mDialogSearching.cancel();
            }

            @Override
            public void onCacheData(Object obj, boolean hasNetwork) {
                ArrayList<Topic> temp = ((BaseResponse<ArrayList<Topic>>) obj).data;
                if (mCachedTopicList != null) {
                    mCachedTopicList.clear();
                }
                if (temp != null && temp.size() > 0) {
                    mCachedTopicList = temp;
                }
                if (!hasNetwork) {
                    doWithSearchResult(mCachedTopicList, key);
                }
            }

            @Override
            public void onCacheDataError(boolean hasNetwork) {
                if (!hasNetwork) {
                    mDialogSearching.cancel();
                }
            }
        }).sendRequest(RequestUrl.search + "&img_mod=" + (mode == 1 ? 3 : mode) + "&filter[posts_per_page]=" + 10 + "&page=" + 0
                + "&filter[s]=" + sendContent, true, Request.Method.GET, null, new TypeToken<BaseResponse<ArrayList<Topic>>>() {
        }, "search_key");
    }

    @Override
    public void fragmentChanged(String fragmentTag, Bundle extras, boolean back) {
        if (back) {
            onBackPressed();
            return;
        }
        mCurrentFragmentTag = fragmentTag;
        mCurrentFragment = null;
        if (FragmentSearchResult.class.getSimpleName()
                .equals(fragmentTag)) {
            CommonUtils.hideSoftKeyBoard(getApplicationContext(), mEt_content);
            mCurrentFragment = new FragmentSearchResult();
            showFragment(R.id.container_fragment, mCurrentFragment, extras, fragmentTag);
        } else if (FragmentSearchHistory.class.getSimpleName().equals(fragmentTag)) {
            mCurrentFragment = new FragmentSearchHistory();
            if (mIsFirstComming) {
                mIsFirstComming = false;
            }
            showFragment(R.id.container_fragment, mCurrentFragment, extras, fragmentTag);
        }
    }

    @Override
    public void onBackPressed() {
        if (!mIsFirstComming && FragmentSearchHistory.class.getSimpleName().equals(mCurrentFragmentTag)) {
            //如果是再搜索的话，则返回跳转到之前的FragmentSearch中，数据保存在Activity中
            //TODO
            gotoSearchResultFragment();
//            fragmentChanged(FragmentSearch.class.getSimpleName(), null, false);
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int arg0, int arg1, Intent arg2) {
        super.onActivityResult(arg0, arg1, arg2);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public static void gotoFragmentSearchHistory(Context context) {
        Intent intent = new Intent(context, ActivitySearch.class);
        intent.putExtra(ARG_FRAGMENT_TAG,
                FragmentSearchHistory.class.getSimpleName());
        context.startActivity(intent);
    }
}
