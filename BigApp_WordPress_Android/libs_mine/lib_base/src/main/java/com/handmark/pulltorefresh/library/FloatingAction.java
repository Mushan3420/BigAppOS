package com.handmark.pulltorefresh.library;

import android.animation.TimeInterpolator;
import android.annotation.SuppressLint;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

import com.wjwu.wpmain.util.ZSDKUtils;

/**
 * An action stolen from ActionBar which happens to float
 */
public class FloatingAction {
	// 是否隐藏了
	private boolean _hide;

	// 监听的列表界面
	private AbsListView _absListView;
	private TimeInterpolator _interpolator;
	// 左右两个悬浮按钮的图片资源
	private View mBtnLeft, mBtnRight;

	// 悬浮按钮消失和出现时间
	private long _duration = 200;

	private Delegate mDelegate = new Delegate();

	public FloatingAction(AbsListView absListView, View leftView,
			View rightView) {
		mBtnLeft = leftView;
		mBtnRight = rightView;
		_absListView = absListView;
		_interpolator = new AccelerateDecelerateInterpolator();
		// Start listening if any
		listenTo(_absListView);
	}

	public void listenTo(AbsListView absListView) {
		_absListView = absListView;
		if (_absListView != null) {
			/**
			 * 为ListView设置OnScrollListner
			 */
			mDelegate.reset();
			_absListView.setOnScrollListener(mDelegate);
		}
	}

	public void onDestroy() {
		listenTo(null);
	}

	private void onDirectionChanged(boolean goingDown) {
		leHide(goingDown, true);
	}

	public void hide() {
		hide(true);
	}

	public void hide(boolean animate) {
		leHide(true, animate);
	}

	public void show() {
		show(true);
	}

	public void show(boolean animate) {
		leHide(false, animate);
	}

	private void leHide(final boolean hide, final boolean animated) {
		leHide(mBtnLeft, mBtnRight, hide, animated, false);
	}

	@SuppressLint("NewApi")
	private void leHide(final View viewLeft, final View viewRight,
			final boolean hide, final boolean animated, final boolean deferred) {
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
				}
			} else {
				viewLeft.setTranslationY(translationY);
				viewRight.setTranslationY(translationY);
			}
		}
	}

	/**
	 * OnScrollListner实例，
	 */
	public class Delegate implements OnScrollListener {
		private static final int DIRECTION_CHANGE_THRESHOLD = 1;
		private int mPrevPosition;
		private int mPrevTop;
		private boolean mUpdated;

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			final View topChild = view.getChildAt(0);
			int firstViewTop = 0;
			if (topChild != null) {
				firstViewTop = topChild.getTop();
			}
			boolean goingDown;
			boolean changed = true;
			if (mPrevPosition == firstVisibleItem) {
				final int topDelta = mPrevTop - firstViewTop;
				goingDown = firstViewTop < mPrevTop;
				changed = Math.abs(topDelta) > DIRECTION_CHANGE_THRESHOLD;
			} else {
				goingDown = firstVisibleItem > mPrevPosition;
			}
			if (changed && mUpdated) {
				onDirectionChanged(goingDown);
			}
			mPrevPosition = firstVisibleItem;
			mPrevTop = firstViewTop;
			mUpdated = true;
		}

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			// No-op
		}

		public void reset() {
			mPrevPosition = 0;
			mPrevTop = 0;
			mUpdated = false;
		}
	}
}
