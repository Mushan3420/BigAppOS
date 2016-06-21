/*
 * 官网地站:http://www.mob.com
 * 技术支持QQ: 4006852216
 * 官方微信:ShareSDK   （如果发布新版本的话，我们将会第一时间通过微信将版本更新内容推送给您。如果使用过程中有任何问题，也可以通过微信与我们取得联系，我们将会在24小时内给予回复）
 *
 * Copyright (c) 2013年 mob.com. All rights reserved.
 */

package cn.sharesdk.onekeyshare.theme.classic;

import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.wjwu.wpmain.lib_base.R;

import java.util.ArrayList;

import cn.sharesdk.onekeyshare.PlatformListFakeActivity;

import static com.mob.tools.utils.R.getBitmapRes;
import static com.mob.tools.utils.R.getStringRes;

public class PlatformListPage extends PlatformListFakeActivity implements View.OnClickListener {
	// page container
	private FrameLayout flPage;
	// gridview of platform list
	private PlatformGridView grid;
	// cancel button
	private Button btnCancel;
	// sliding up animation
	private Animation animShow;
	// sliding down animation
	private Animation animHide;
	private boolean finishing;
	private LinearLayout llPage;

	public void onCreate() {
		super.onCreate();

		finishing = false;
		initPageView();
		initAnim();
		activity.setContentView(flPage);

		// set the data for platform gridview
		grid.setData(shareParamsMap, silent);
		grid.setHiddenPlatforms(hiddenPlatforms);
		grid.setCustomerLogos(customerLogos);
		grid.setParent(this);
		btnCancel.setOnClickListener(this);

		// display gridviews
		llPage.clearAnimation();
		llPage.startAnimation(animShow);
	}

	private void initPageView() {
		flPage = new FrameLayout(getContext());
		flPage.setOnClickListener(this);
		flPage.setBackgroundDrawable(new ColorDrawable(0x55000000));

		// container of the platform gridview
		llPage = new LinearLayout(getContext()) {
			public boolean onTouchEvent(MotionEvent event) {
				return true;
			}
		};
		llPage.setOrientation(LinearLayout.VERTICAL);
		llPage.setBackgroundDrawable(new ColorDrawable(0xffffffff));
		FrameLayout.LayoutParams lpLl = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
		lpLl.gravity = Gravity.BOTTOM;
		llPage.setLayoutParams(lpLl);
		flPage.addView(llPage);

		// gridview
		grid = new PlatformGridView(getContext());
		grid.setEditPageBackground(getBackgroundView());
		LinearLayout.LayoutParams lpWg = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		grid.setLayoutParams(lpWg);
		llPage.addView(grid);

		View line_view = new View(getContext());
		line_view.setBackgroundDrawable(new ColorDrawable(0xffd5d5d5));

		int lineHeight = (int)((float)0.5 * getContext().getResources().getDisplayMetrics().density + 0.5F);

		LinearLayout.LayoutParams lpLine = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT, lineHeight);
		int dp_10 = com.mob.tools.utils.R.dipToPx(getContext(), 10);
		lpLine.setMargins(0, dp_10, 0, 0);
		line_view.setLayoutParams(lpLine);
		llPage.addView(line_view);

		// cancel button
		btnCancel = new Button(getContext());
		ColorStateList csl=(ColorStateList)activity.getResources().getColorStateList(R.color.z_txt_input);
		btnCancel.setTextColor(csl);
		btnCancel.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
		int resId = getStringRes(getContext(), "cancel");
		if (resId > 0) {
			btnCancel.setText(resId);
		}
		btnCancel.setPadding(0, 0, 0, 0);

		resId = getBitmapRes(getContext(), "classic_platform_corners_bg");
		if(resId > 0){
			btnCancel.setBackgroundResource(resId);
		}else {
		    btnCancel.setBackgroundDrawable(new ColorDrawable(0xffffffff));
		}
		btnCancel.setBackgroundColor(0x00ffffff);

		LinearLayout.LayoutParams lpBtn = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT, com.mob.tools.utils.R.dipToPx(getContext(), 45));
//		int dp_10 = com.mob.tools.utils.R.dipToPx(getContext(), 10);
		lpBtn.setMargins(0, 0, 0, 0);
		btnCancel.setLayoutParams(lpBtn);
		llPage.addView(btnCancel);
	}

	private void initAnim() {
		animShow = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0,
				Animation.RELATIVE_TO_SELF, 0,
				Animation.RELATIVE_TO_SELF, 1,
				Animation.RELATIVE_TO_SELF, 0);
		animShow.setDuration(300);

		animHide = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0,
				Animation.RELATIVE_TO_SELF, 0,
				Animation.RELATIVE_TO_SELF, 0,
				Animation.RELATIVE_TO_SELF, 1);
		animHide.setDuration(300);
	}

	public void onConfigurationChanged(Configuration newConfig) {
		if (grid != null) {
			grid.onConfigurationChanged();
		}
	}

	public boolean onFinish() {
		if (finishing) {
			return super.onFinish();
		}

		if (animHide == null) {
			finishing = true;
			return false;
		}

		finishing = true;
		animHide.setAnimationListener(new Animation.AnimationListener() {
			public void onAnimationStart(Animation animation) {

			}

			public void onAnimationRepeat(Animation animation) {

			}

			public void onAnimationEnd(Animation animation) {
				flPage.setVisibility(View.GONE);
				finish();
			}
		});
		llPage.clearAnimation();
		llPage.startAnimation(animHide);
		//中断finish操作
		return true;
	}

	@Override
	public void onClick(View v) {
		if (v.equals(flPage) || v.equals(btnCancel)) {
			setCanceled(true);
			finish();
		}
	}

	public void onPlatformIconClick(View v, ArrayList<Object> platforms) {
		onShareButtonClick(v, platforms);
	}
}
