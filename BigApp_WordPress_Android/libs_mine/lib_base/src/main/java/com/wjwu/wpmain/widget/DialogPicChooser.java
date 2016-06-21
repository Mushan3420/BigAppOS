package com.wjwu.wpmain.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;

import com.wjwu.wpmain.lib_base.R;

public class DialogPicChooser implements View.OnClickListener {
    private Dialog dialog;
    private OnModeChangedLisener mLisener;

    public DialogPicChooser(Context context, OnModeChangedLisener lisener) {
        mLisener = lisener;
        init(context);
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(
                R.layout.z_dialog_pic_chooser, null);
        dialog = new Dialog(context, R.style.Dialog_General);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(view);

        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity = Gravity.BOTTOM;
        lp.width = LayoutParams.MATCH_PARENT;
        window.setWindowAnimations(R.style.AnimUpDown);

        view.findViewById(R.id.btn_cancel).setOnClickListener(this);
        view.findViewById(R.id.tv_camera).setOnClickListener(this);
        view.findViewById(R.id.tv_gallery).setOnClickListener(this);
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
        if (dialog != null) {
            dialog.dismiss();
        }
        if (vId == R.id.tv_camera) {
            mLisener.onModeChanged(1);
        } else if (vId == R.id.tv_gallery) {
            mLisener.onModeChanged(2);
        }
    }

    public interface OnModeChangedLisener {
        /***
         * @param mode 1拍照，2相册
         */
        void onModeChanged(int mode);
    }
}
