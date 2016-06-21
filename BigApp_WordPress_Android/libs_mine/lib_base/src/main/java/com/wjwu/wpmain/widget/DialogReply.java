package com.wjwu.wpmain.widget;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.PopupWindow;

import com.wjwu.wpmain.lib_base.R;

import model.Topic;

public class DialogReply implements View.OnClickListener {

    private LayoutInflater mInflater;

    private EditText mEt_reply;
    private View mLayout_board;

    private PopupWindow mWindow;
    private InputMethodManager imm;

    private Topic mTopic;
    private SendOnClickListener listener;

    public DialogReply(Context context) {
        this.mInflater = LayoutInflater.from(context);
        this.imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        init();
    }

    private void init() {
        View contentLayout = mInflater.inflate(R.layout.z_dialog_reply, null);
        mLayout_board = contentLayout.findViewById(R.id.layout_board);
        mEt_reply = (EditText) contentLayout.findViewById(R.id.et_reply);
        contentLayout.findViewById(R.id.iv_close).setOnClickListener(this);
        contentLayout.findViewById(R.id.iv_send).setOnClickListener(this);

        mWindow = new PopupWindow(contentLayout, LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        mWindow.setFocusable(true);
        mWindow.setOutsideTouchable(true);
        mWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
                | WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        mWindow.setBackgroundDrawable(new ColorDrawable(0));
        mWindow.setAnimationStyle(R.style.AnimUpDown);
    }

    private void hideSoftInput() {
        imm.hideSoftInputFromWindow(mEt_reply.getWindowToken(), 0);
    }

    private void showSoftInput() {
        mEt_reply.requestFocus();
        imm.showSoftInput(mEt_reply, 0);
    }

    public void dismiss() {
        mWindow.dismiss();
    }

    public boolean isShowing() {
        return mWindow.isShowing();
    }

    public String getReplyText() {
        return mEt_reply.getText().toString().trim();
    }

    public int getDistance(Rect parent) {
        if (mParent != null) {
            View global = (View) mParent.getParent().getParent().getParent()
                    .getParent();
            Rect r = new Rect();
            global.getGlobalVisibleRect(r);
            // System.out.println(r.toShortString());
            int distance = parent.bottom - r.bottom;
            return distance;
        }
        return 0;
    }

    private View mParent;

    public void show(View v, Topic mTopic) {
        if (isShowing()) {
            return;
        }
        mParent = v;
        this.mTopic = mTopic;
        mEt_reply.setText("");
        mWindow.showAtLocation(mParent, Gravity.BOTTOM, 0, 0);
    }

    public void setLayoutBoardHeight(int height) {
        if (height > 0) {
            if (mLayout_board.getVisibility() == View.GONE) {
                LayoutParams p = mLayout_board.getLayoutParams();
                p.height = height;
                mLayout_board.setLayoutParams(p);
                mLayout_board.setVisibility(View.VISIBLE);
            }
        } else {
            dismiss();
        }
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_send) {
            if (listener != null) {
                listener.onClick(v, getReplyText(), mTopic);
            }
        } else if (v.getId() == R.id.iv_close) {
            dismiss();
        }
    }

    public void setSendOnClickListener(SendOnClickListener listener) {
        this.listener = listener;
    }

    public interface SendOnClickListener {
        void onClick(View v, String replyText, Topic mTopic);
    }
}
