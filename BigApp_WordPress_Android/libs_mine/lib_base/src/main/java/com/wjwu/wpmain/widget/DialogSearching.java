package com.wjwu.wpmain.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.wjwu.wpmain.lib_base.R;
import com.wjwu.wpmain.util.ScreenTools;

public class DialogSearching {
    private Dialog dialog;
    private TextView mEt_content;

    public DialogSearching(Context context) {
        init(context);
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(
                R.layout.z_dialog_searching, null);
        mEt_content = (TextView) view.findViewById(R.id.tv_content);
        dialog = new Dialog(context, R.style.DialogStyle);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(view);

        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = ScreenTools.getScreenParams(context).width / 3;
        lp.height = lp.width;
        window.setBackgroundDrawableResource(R.drawable.z_shape_searching);
        window.setWindowAnimations(R.style.AnimEnterExit);
    }

    public DialogSearching setContent(int resId) {
        if (mEt_content != null) {
            mEt_content.setText(resId);
        }
        return this;
    }

    /***
     * 显示对话框
     */
    public void show() {
        if (dialog != null) {
            dialog.show();
        }
    }

    public void cancel() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }
}
