package com.wjwu.wpmain.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;

import com.wjwu.wpmain.lib_base.R;

public class DialogFontSizeChoice implements View.OnClickListener {
    private Dialog dialog;
    private OnFontChangedLisener mLisener;
    private View mTv_small, mTv_normal, mTv_large, mTv_largest;

    public DialogFontSizeChoice(Context context, WebSettings.TextSize currentTextSize, OnFontChangedLisener lisener) {
        mLisener = lisener;
        init(context, currentTextSize);
    }

    private void init(Context context, WebSettings.TextSize currentTextSize) {
        View view = LayoutInflater.from(context).inflate(
                R.layout.z_dialog_font_choice, null);
        dialog = new Dialog(context, R.style.Dialog_General);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(view);

        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity = Gravity.BOTTOM;
        lp.width = LayoutParams.MATCH_PARENT;
        window.setWindowAnimations(R.style.AnimUpDown);

        view.findViewById(R.id.btn_finish).setOnClickListener(this);
        mTv_normal = view.findViewById(R.id.tv_normal);
        mTv_normal.setOnClickListener(this);
        mTv_small = view.findViewById(R.id.tv_small);
        mTv_small.setOnClickListener(this);
        mTv_large = view.findViewById(R.id.tv_big);
        mTv_large.setOnClickListener(this);
        mTv_largest = view.findViewById(R.id.tv_max);
        mTv_largest.setOnClickListener(this);
        if (currentTextSize == WebSettings.TextSize.SMALLER) {
            mTv_small.setSelected(true);
        } else if (currentTextSize == WebSettings.TextSize.NORMAL) {
            mTv_normal.setSelected(true);
        } else if (currentTextSize == WebSettings.TextSize.LARGER) {
            mTv_large.setSelected(true);
        } else if (currentTextSize == WebSettings.TextSize.LARGEST) {
            mTv_largest.setSelected(true);
        }
    }

    /***
     * 显示对话框
     */
    public void show() {
        if (dialog != null) {
            dialog.show();
        }
    }

    public void onDestry() {
        if (dialog != null) {
            dialog.dismiss();
        }
        dialog = null;
        mLisener = null;
    }

    @Override
    public void onClick(View v) {
        int vId = v.getId();
        if (mLisener == null) {
            return;
        }
        if (vId == R.id.btn_finish) {
            if (dialog != null) {
                dialog.dismiss();
            }
        } else if (vId == R.id.tv_small) {
            mLisener.onFontChanged(WebSettings.TextSize.SMALLER);
        } else if (vId == R.id.tv_normal) {
            mLisener.onFontChanged(WebSettings.TextSize.NORMAL);
        } else if (vId == R.id.tv_big) {
            mLisener.onFontChanged(WebSettings.TextSize.LARGER);
        } else if (vId == R.id.tv_max) {
            mLisener.onFontChanged(WebSettings.TextSize.LARGEST);
        }
        setViewSelected(v);
    }

    private void setViewSelected(View v) {
        mTv_small.setSelected(false);
        mTv_normal.setSelected(false);
        mTv_large.setSelected(false);
        mTv_largest.setSelected(false);
        v.setSelected(true);
    }

    public interface OnFontChangedLisener {
        void onFontChanged(WebSettings.TextSize size);
    }
}
