package com.handmark.pulltorefresh.library;

import android.animation.TimeInterpolator;
import android.annotation.SuppressLint;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ScrollView;

import com.handmark.pulltorefresh.library.CustomScrollView;
import com.handmark.pulltorefresh.library.CustomScrollView.OnScrollChangedListener;
import com.wjwu.wpmain.util.ZSDKUtils;

/**
 * An action stolen from ActionBar which happens to float
 */
public class FloatingActionScroll {
	// 是否隐藏了
	private boolean _hide;

	// 监听的列表界面
	private CustomScrollView _absListView;
	private TimeInterpolator _interpolator;
	// 左右两个悬浮按钮的图片资源
	private View mBtnLeft, mBtnRight, mViewMsg;

	// 悬浮按钮消失和出现时间
	private long _duration = 200;

	private Delegate mDelegate = new Delegate();

	public FloatingActionScroll(CustomScrollView absListView, View leftView,
			View rightView, View viewMsg) {
		mBtnLeft = leftView;
		mBtnRight = rightView;
		mViewMsg = viewMsg;
		_absListView = absListView;
		_interpolator = new AccelerateDecelerateInterpolator();
		// Start listening if any
		listenTo(_absListView);
	}

	public void listenTo(CustomScrollView absListView) {
		if (absListView == null && _absListView != null) {
			_absListView.setScrollChangedListener(null);
		}
		_absListView = absListView;
		if (_absListView != null) {
			/**
			 * 为ScrollView设置OnScrollListner
			 */
			_absListView.setScrollChangedListener(mDelegate);
		}
	}

	public void onDestroy() {
		listenTo(null);
	}

	private void onDirectionChanged(boolean goingDown) {
		leHide(goingDown, true);
	}

	private void leHide(final boolean hide, final boolean animated) {
		leHide(mBtnLeft, mBtnRight, mViewMsg, hide, animated, false);
	}

	@SuppressLint("NewApi")
	private void leHide(final View viewLeft, final View viewRight,
			final View viewMsg, final boolean hide, final boolean animated,
			final boolean deferred) {
		if (_hide != hide || deferred) {
			_hide = hide;
			final int height = viewLeft.getHeight();
			int marginBottom = 0;
			final ViewGroup.LayoutParams layoutParams = viewLeft
					.getLayoutParams();
			if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
				marginBottom = ((ViewGroup.MarginLayoutParams) layoutParams).bottomMargin;
			}
			final int translationY = _hide ? height + marginBottom : 0;
			if (animated) {
				if (ZSDKUtils.hasHoneycombMR1()) {
					viewLeft.animate().setInterpolator(_interpolator)
							.setDuration(_duration).translationY(translationY);
					viewRight.animate().setInterpolator(_interpolator)
							.setDuration(_duration).translationY(translationY);
					viewMsg.animate().setInterpolator(_interpolator)
							.setDuration(_duration).translationY(translationY);
				}
			} else {
				viewLeft.setTranslationY(translationY);
				viewRight.setTranslationY(translationY);
				viewMsg.setTranslationY(translationY);
			}
		}
	}

	/**
	 * OnScrollListner实例，
	 */
	public class Delegate implements OnScrollChangedListener {
		@Override
		public void onScrollChanged(ScrollView scrollView, int l, int t,
				int oldl, int oldt) {
			if (l < oldl || t <= oldt) {
				onDirectionChanged(false);
			} else {
				onDirectionChanged(true);
			}
		}
	}
}
