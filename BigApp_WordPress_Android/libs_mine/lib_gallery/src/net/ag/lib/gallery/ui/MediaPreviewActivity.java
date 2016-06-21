package net.ag.lib.gallery.ui;

import java.util.ArrayList;

import net.ag.lib.gallery.R;
import net.ag.lib.gallery.adapter.MediaPreviewAdapter;
import net.ag.lib.gallery.ui.BaseFragmentActivity.FragmentCallBack;
import net.ag.lib.gallery.util.MediaConstants;
import net.ag.lib.gallery.util.MediaInfo;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.viewpagerindicator.CirclePageIndicator;

/***
 * 图片预览
 * 
 * @author AG
 *
 */
@SuppressLint("HandlerLeak")
public class MediaPreviewActivity extends FragmentActivity implements
		View.OnClickListener, OnPageChangeListener, FragmentCallBack {

	private int mPosition = 0;
	/***
	 * 浏览大图模式，一种是0浏览（可以分享全屏）；一种是1预览（可以删除，有title bar）
	 */
	private int mPreview_type = 0;
	private ArrayList<MediaInfo> mSelectedList;

	private MediaPreviewAdapter mAdapter;
	private ViewPager mViewPager;
	private CirclePageIndicator mIndicator;
	private View mV_title_bar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.zg_activity_media_preview);
		init();
	}

	@SuppressWarnings("unchecked")
	private void init() {
		mSelectedList = (ArrayList<MediaInfo>) getIntent()
				.getSerializableExtra(MediaConstants.MEDIA_REQUEST_DATAS);
		mPosition = getIntent().getIntExtra(MediaConstants.MEDIA_POSITION, 0);
		mV_title_bar = findViewById(R.id.titleBar);
		mPreview_type = getIntent().getIntExtra(
				MediaConstants.MEDIA_PREVIEW_TYPE, 0);
		if (mPreview_type == 0) {
			mV_title_bar.setVisibility(View.GONE);
		} else {
			mV_title_bar.setVisibility(View.VISIBLE);
			findViewById(R.id.iv_left).setOnClickListener(this);
			ImageView iv_right = (ImageView) findViewById(R.id.iv_right);
			iv_right.setOnClickListener(this);
			iv_right.setImageResource(R.drawable.zg_default_delete);
			((TextView) findViewById(R.id.title))
					.setText(R.string.zg_content_media_pic_preview);
		}

		mViewPager = (ViewPager) findViewById(R.id.pager);
		mIndicator = (CirclePageIndicator) findViewById(R.id.indicator);

		mIndicator.setOnPageChangeListener(this);
		mViewPager.setPageMargin(getResources().getDimensionPixelOffset(
				R.dimen.zg_viewpage_margin));

		mHandler.sendEmptyMessage(0);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.iv_left) {
			onBackPressed();
		} else if (v.getId() == R.id.iv_right) {
			showDelDialog();
		}
	}

	private void showDelDialog() {
		if (mSelectedList == null || mPosition >= mSelectedList.size()) {
			return;
		}
		if (mSelectedList.size() == 1) {
			mSelectedList.remove(mPosition);
			onBackPressed();
			return;
		}
		mSelectedList.remove(mPosition);
		// TODO删除某一个后，更新界面
		mAdapter.notifyDataSetChanged();
		mIndicator.invalidate();
		// if (mSelectedList.size() > 1) {
		// AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
		// mBuilder.setMessage(R.string.z_media_delete_confirm);
		// mBuilder.setPositiveButton(R.string.z_btn_confirm,
		// new DialogInterface.OnClickListener() {
		// @Override
		// public void onClick(DialogInterface dialog, int which) {
		// mSelectedList.remove(mPosition);
		// }
		// });
		// mBuilder.setNegativeButton(R.string.z_btn_cancel, null);
		// mBuilder.show();
		// } else {
		// mSelectedList.remove(mPosition);
		// // TODO删除某一个后，更新界面
		// mAdapter.notifyDataSetChanged();
		// }
	}

	private Handler mHandler = new Handler() {
		@SuppressWarnings("unchecked")
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				if (mSelectedList != null && mSelectedList.size() > 0) {
					mAdapter = new MediaPreviewAdapter(
							getSupportFragmentManager(), mSelectedList);
					mViewPager.setAdapter(mAdapter);
					mViewPager.setOffscreenPageLimit(1);
					mIndicator.setViewPager(mViewPager);
					mViewPager.setCurrentItem(mPosition);
					if (mAdapter.getCount() > 1) {
						mIndicator.setVisibility(View.VISIBLE);
					}
				}
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	@Override
	public void onPageSelected(int position) {
		mPosition = position;
	}

	@Override
	public void fragmentChanged(String fragmentTag, Bundle extras, boolean back) {
		// 这里只处理返回键
		if (mPreview_type == 0) {// 全屏浏览的时候，单击图片消失；非全屏的时候不消失
			onBackPressed();
		}
	}

	@Override
	public void onBackPressed() {
		if (mPreview_type == 1) {
			Intent data = new Intent();
			data.putExtra(MediaConstants.MEDIA_RESULT_DATAS, mSelectedList);
			setResult(Activity.RESULT_OK, data);
		}
		super.onBackPressed();
		if (mPreview_type == 0) {
			overridePendingTransition(R.anim.v_popup_enter, R.anim.v_popup_exit);
		} else {
			overridePendingTransition(R.anim.v_left_in, R.anim.v_right_out);
		}
	}

	/***
	 * 
	 * @param context
	 * @param selectedList
	 * @param position
	 * @param type_preview
	 *            浏览大图模式，一种是0浏览（可以分享）；一种是1预览（可以删除）
	 */
	public static void gotoPreviewForResult(Context context,
			ArrayList<MediaInfo> selectedList, int position, int type_preview) {
		Intent intent = new Intent(context, MediaPreviewActivity.class);
		intent.putExtra(MediaConstants.MEDIA_REQUEST_DATAS, selectedList);
		intent.putExtra(MediaConstants.MEDIA_POSITION, position);
		intent.putExtra(MediaConstants.MEDIA_PREVIEW_TYPE, type_preview);
		((Activity) context).startActivityForResult(intent,
				MediaConstants.MEDIA_REQUEST_PIC_CODE);
		if (type_preview == 0) {
			((Activity) context).overridePendingTransition(
					R.anim.v_popup_enter, R.anim.v_popup_exit);
		}
	}

	/***
	 * @param fragment
	 * @param selectedList
	 * @param position
	 * @param type_preview
	 *            浏览大图模式，一种是0浏览（可以分享）；一种是1预览（可以删除）
	 */
	public static void gotoPreviewForResult(Fragment fragment,
			ArrayList<MediaInfo> selectedList, int position, int type_preview) {
		Intent intent = new Intent(fragment.getActivity(),
				MediaPreviewActivity.class);
		intent.putExtra(MediaConstants.MEDIA_REQUEST_DATAS, selectedList);
		intent.putExtra(MediaConstants.MEDIA_POSITION, position);
		intent.putExtra(MediaConstants.MEDIA_PREVIEW_TYPE, type_preview);
		fragment.startActivityForResult(intent,
				MediaConstants.MEDIA_REQUEST_PIC_CODE);
		if (type_preview == 0) {
			fragment.getActivity().overridePendingTransition(
					R.anim.v_popup_enter, R.anim.v_popup_exit);
		}
	}

}
