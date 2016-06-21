package com.handmark.pulltorefresh.library;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;

public class CustomScrollView extends ScrollView {

	private OnScrollChangedListener mListener;
	private View mContentView;

	public CustomScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public CustomScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CustomScrollView(Context context) {
		super(context);
	}

	public void setScrollChangedListener(OnScrollChangedListener listener) {
		mListener = listener;
		if (listener != null) {
			mContentView = getChildAt(0);
		}
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		if (mContentView != null
				&& mContentView.getMeasuredHeight() <= getScrollY()
						+ getHeight()) {
			l = 0;
			oldl = 1;
			// 滑动到最底了
		} else if (getScrollY() == 0) {
			// 滑动到最高了
			l = 0;
			oldl = 1;
		}
		if (mListener != null) {
			mListener.onScrollChanged(this, l, t, oldl, oldt);
		}
	}

	public interface OnScrollChangedListener {
		public void onScrollChanged(ScrollView scrollView, int l, int t,
									int oldl, int oldt);
	}
}
