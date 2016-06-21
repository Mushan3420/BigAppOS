package net.ag.lib.gallery.ui;

import java.util.ArrayList;

import net.ag.lib.gallery.R;
import net.ag.lib.gallery.adapter.AdapterGridAdd;
import net.ag.lib.gallery.util.MediaConstants;
import net.ag.lib.gallery.util.MediaInfo;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.GridView;
import android.widget.TextView;

/***
 * 添加图片界面
 * 
 * @author macy
 *
 */
public class MediaAddActivity extends BaseFragmentActivity implements
		OnClickListener, OnItemClickListener, OnItemSelectedListener {

	private GridView mGridView;
	private AdapterGridAdd mAdapter;
	private ArrayList<MediaInfo> mMediaInfoList;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.zg_activity_media_add);
		mGridView = (GridView) findViewById(R.id.gridview);
		((TextView) findViewById(R.id.title))
				.setText(R.string.zg_content_media_pic_select);
		findViewById(R.id.iv_left).setOnClickListener(this);
		TextView tv_right = (TextView) findViewById(R.id.tv_right);
		tv_right.setOnClickListener(this);
		mGridView.setOnItemClickListener(this);
		mGridView.setOnItemSelectedListener(this);
		mAdapter = new AdapterGridAdd(this);
		mGridView.setAdapter(mAdapter);
		mMediaInfoList = (ArrayList<MediaInfo>) getIntent()
				.getSerializableExtra(MediaConstants.MEDIA_REQUEST_DATAS);
		if (mMediaInfoList == null) {
			mMediaInfoList = new ArrayList<MediaInfo>();
		}
		mAdapter.setVideos(mMediaInfoList, 6);

	}

	@Override
	public void onClick(View v) {
		int vId = v.getId();
		if (vId == R.id.iv_left) {
			setResult(RESULT_CANCELED);
			finish();
		} else if (vId == R.id.tv_right) {
			Intent data = new Intent();
			data.putExtra(MediaConstants.MEDIA_RESULT_DATAS, mMediaInfoList);
			setResult(RESULT_OK, data);
			finish();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == MediaConstants.MEDIA_REQUEST_PIC_CODE
				&& resultCode == RESULT_OK && data != null
				&& data.hasExtra(MediaConstants.MEDIA_RESULT_DATAS)) {
			mMediaInfoList = (ArrayList<MediaInfo>) data
					.getSerializableExtra(MediaConstants.MEDIA_RESULT_DATAS);
			if (mMediaInfoList == null) {
				mMediaInfoList = new ArrayList<MediaInfo>();
			}
			mAdapter.setVideos(mMediaInfoList, 6);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		MediaInfo mediaInfo = (MediaInfo) parent.getItemAtPosition(position);
		if (mediaInfo == null) {
			return;
		}
		if ("Add".equals(mediaInfo.filePath)) {
			MediaShowActivity.gotoPicForResult(this,
					MediaConstants.MODE_MULTIPLE_PICK, 6, mMediaInfoList);
		} else {
			MediaPreviewActivity.gotoPreviewForResult(this, mMediaInfoList,
					position, 1);
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
	}

	/***
	 * 进入图片界面
	 * 
	 * @param activity
	 * @param PickMode
	 */
	public static void gotoPicAddForResult(FragmentActivity activity,
			ArrayList<MediaInfo> selectedMediaInfos) {
		Intent intent = new Intent(activity, MediaAddActivity.class);
		intent.putExtra(MediaConstants.MEDIA_REQUEST_DATAS, selectedMediaInfos);
		activity.startActivityForResult(intent,
				MediaConstants.MEDIA_REQUEST_PIC_CODE);
	}

	/***
	 * 进入图片界面
	 * 
	 * @param activity
	 * @param PickMode
	 */
	public static void gotoPicAddForResult(Fragment fragment,
			ArrayList<MediaInfo> selectedMediaInfos) {
		Intent intent = new Intent(fragment.getActivity(),
				MediaAddActivity.class);
		intent.putExtra(MediaConstants.MEDIA_REQUEST_DATAS, selectedMediaInfos);
		fragment.startActivityForResult(intent,
				MediaConstants.MEDIA_REQUEST_PIC_CODE);
	}
}
