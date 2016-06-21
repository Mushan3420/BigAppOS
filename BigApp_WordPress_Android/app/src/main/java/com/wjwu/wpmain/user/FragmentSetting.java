package com.wjwu.wpmain.user;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.webkit.WebSettings;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.wjwu.wpmain.cache.SettingCache;
import com.wjwu.wpmain.cache.SpTool;
import com.wjwu.wpmain.doffline.ServiceOfflineDownload;
import com.wjwu.wpmain.lib_base.BaseApplication;
import com.wjwu.wpmain.lib_base.BaseFragmentWithTitleBarSimple;
import com.wjwu.wpmain.net.DataManager;
import com.wjwu.wpmain.util.CommonUtils;
import com.wjwu.wpmain.util.TaskExecutor;
import com.wjwu.wpmain.util.ZToastUtils;
import com.wjwu.wpmain.uzwp.R;
import com.wjwu.wpmain.widget.DialogFontSizeChoice;
import com.wjwu.wpmain.widget.DialogImgMode;
import com.wjwu.wpmain.widget.DialogSearching;

import de.greenrobot.event.EventBus;
import event.MoonEvent;
import event.WebTxtChangedEvent;

/**
 * Created by wjwu on 2015/8/31.
 */
public class FragmentSetting extends BaseFragmentWithTitleBarSimple implements View.OnClickListener {

    private TextView tv_content_cache, tv_img_mode_content;
    private int mImgMode = 3;

    @Override
    public int initContentView() {
        return R.layout.v_fragment_setting;
    }

    @Override
    public void findAndBindViews(View contentView) {
        setDefaultImageLeftVisible(true, R.attr.img_nav_back);
        setTitleText(R.string.v_left_menu_setting);

        View item_clear_cache, item_about, item_offline_download, item_font_size, item_img_mode, item_moon_toggle;
        item_clear_cache = contentView.findViewById(R.id.item_clear_cache);
        ((TextView) item_clear_cache.findViewById(R.id.tv_title)).setText(R.string.v_setting_item_clear_cache);
        tv_content_cache = (TextView) item_clear_cache.findViewById(R.id.tv_content);
        tv_content_cache.setText(DataManager.getMyCacheSize(mContext.getApplicationContext()));
        tv_content_cache.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.z_iv_arrow_right, 0);

        item_offline_download = contentView.findViewById(R.id.item_offline_download);
        ((TextView) item_offline_download.findViewById(R.id.tv_title)).setText(R.string.v_setting_item_offline_download);
        ((TextView) item_offline_download.findViewById(R.id.tv_content)).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.z_iv_arrow_right, 0);

        item_about = contentView.findViewById(R.id.item_about);
        ((TextView) item_about.findViewById(R.id.tv_title)).setText(R.string.v_setting_item_about);
        ((TextView) item_about.findViewById(R.id.tv_content)).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.z_iv_arrow_right, 0);

        item_font_size = contentView.findViewById(R.id.item_font_size);
        ((TextView) item_font_size.findViewById(R.id.tv_title)).setText(R.string.v_setting_item_font_size);

        item_img_mode = contentView.findViewById(R.id.item_img_mode);
        ((TextView) item_img_mode.findViewById(R.id.tv_title)).setText(R.string.v_setting_item_img_mode);
        tv_img_mode_content = (TextView) item_img_mode.findViewById(R.id.tv_content);
        setImgMode(SettingCache.getImgMode(mContext));

        item_moon_toggle = contentView.findViewById(R.id.item_moon_toggle);
        ((TextView) item_moon_toggle.findViewById(R.id.tv_title)).setText(R.string.v_setting_item_moon_toggle);
        ToggleButton moonBtn = (ToggleButton) item_moon_toggle.findViewById(R.id.btn_moon);

        moonBtn.setChecked(new SpTool(mContext, SpTool.SP_SETTING).getBoolean("moon", false));

        moonBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    BaseApplication.sDefaultImageDrawable = R.drawable.z_iv_default_y;
                } else {
                    BaseApplication.sDefaultImageDrawable = R.drawable.z_iv_default;
                }
                new SpTool(mContext, SpTool.SP_SETTING).putBoolean("moon", isChecked);
                setThemeType(isChecked);
                EventBus.getDefault().post(new MoonEvent(isChecked));
            }
        });

        item_clear_cache.setOnClickListener(this);
        item_about.setOnClickListener(this);
        item_offline_download.setOnClickListener(this);
        item_font_size.setOnClickListener(this);
        item_img_mode.setOnClickListener(this);

//        ((TextView) contentView.findViewById(R.id.tv_version)).setText(getString(R.string.v_version, CommonUtils.getVersionName(mContext)));
    }

    private void setImgMode(int mode) {
        mImgMode = mode;
        switch (mode) {
            case 3:
                tv_img_mode_content.setText(R.string.z_img_mode_high);
                break;
            case 2:
                tv_img_mode_content.setText(R.string.z_img_mode_low);
                break;
            case 1:
                tv_img_mode_content.setText(R.string.z_img_mode_none);
                break;
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                mDialogSearching.cancel();
                tv_content_cache.setText(DataManager.getMyCacheSize(mContext.getApplicationContext()));
                ZToastUtils.toastMessage(mContext.getApplicationContext(), R.string.v_toast_clean_cache_success);
//                getActivity().startService(new Intent(getActivity(), ServiceOfflineDownload.class));
            }
        }
    };

    private DialogFontSizeChoice mDialogFontSizeChoice;
    private DialogImgMode mDialogImgMode;
    private DialogSearching mDialogSearching;

    @Override
    public void onViewClick(View v) {
        int vId = v.getId();
        switch (vId) {
            case R.id.item_clear_cache:
                if (mDialogSearching == null) {
                    mDialogSearching = new DialogSearching(mContext);
                }
                mDialogSearching.setContent(R.string.v_toast_clean_cache_ing);
                mDialogSearching.show();
                TaskExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        DataManager.clearMyCache(mContext.getApplicationContext());
                        mHandler.sendEmptyMessage(0);
                    }
                });
                break;
            case R.id.item_offline_download:
                if (!CommonUtils.checkNetworkEnable(mContext.getApplicationContext())) {
                    return;
                }
                if (mContext != null) {
                    mContext.startService(new Intent(mContext, ServiceOfflineDownload.class));
                }
                break;
            case R.id.item_about:
                ActivityUserMore.gotoFragmentAbout(getActivity());
                break;
            case R.id.item_font_size:
                if (mDialogFontSizeChoice == null) {
                    mDialogFontSizeChoice = new DialogFontSizeChoice(mContext, SettingCache.getFontSize(mContext), new DialogFontSizeChoice.OnFontChangedLisener() {
                        @Override
                        public void onFontChanged(WebSettings.TextSize size) {
                            SettingCache.putFontSize(mContext, size);
                            EventBus.getDefault().post(new WebTxtChangedEvent(size));
                        }
                    });
                }
                mDialogFontSizeChoice.show();
                break;
            case R.id.item_img_mode:
                if (mDialogImgMode == null) {
                    mDialogImgMode = new DialogImgMode(mContext, new DialogImgMode.OnModeChangedLisener() {
                        @Override
                        public void onModeChanged(int mode) {
                            SettingCache.putImgMode(mContext, mode);
                            setImgMode(mode);
                        }
                    });
                }
                mDialogImgMode.show(mImgMode);
                break;
        }
    }

    public void setThemeType(boolean moon) {
        if (moon) {
            ColorStateList csl1 = getResources().getColorStateList(R.color.c_bg_title_bar_y);
            getTitleBarLayout().setBackgroundColor(csl1.getDefaultColor());
            ColorStateList csl2 = getResources().getColorStateList(R.color.c_txt_title_bar_y);
            getTitleView().setTextColor(csl2);
        } else {
            ColorStateList csl1 = getResources().getColorStateList(R.color.c_bg_title_bar);
            getTitleBarLayout().setBackgroundColor(csl1.getDefaultColor());
            ColorStateList csl2 = getResources().getColorStateList(R.color.c_txt_title_bar);
            getTitleView().setTextColor(csl2);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mDialogFontSizeChoice != null) {
            mDialogFontSizeChoice.onDestry();
        }
        if (mDialogImgMode != null) {
            mDialogImgMode.onDestry();
        }
    }
}
