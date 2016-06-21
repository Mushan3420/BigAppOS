package net.ag.lib.gallery.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.ag.lib.gallery.R;
import net.ag.lib.gallery.adapter.AdapterMediaShow;
import net.ag.lib.gallery.adapter.AdapterMediaShow.ViewHolder;
import net.ag.lib.gallery.adapter.MediaCatalogAdapter;
import net.ag.lib.gallery.util.MediaConstants;
import net.ag.lib.gallery.util.MediaInfo;
import net.ag.lib.gallery.util.MediaStoreUtils;
import net.ag.lib.gallery.util.MediaStoreUtils.BaseMediaScannerListener;
import net.ag.lib.gallery.util.TaskState;
import net.ag.lib.gallery.util.ToastUtils;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

/***
 * 媒体文件显示
 * 
 * @author AG
 *
 */
public class MediaShowActivity extends FragmentActivity implements
		View.OnClickListener, OnItemClickListener {

	private int pickMode = MediaConstants.MODE_SINGLE_PICK;
	private int mediaType = MediaConstants.TYPE_PIC;

	private int maxSelectedSize = MediaConstants.MAX_MULTIPLE_PICK;

	private GridView mGridView;
	private AdapterMediaShow mMediaAdapter;
	private ArrayList<MediaInfo> mSelectedList;
	private ArrayList<MediaInfo> mMediaCatalogs;
	private ArrayList<MediaInfo> mMedias;
	private TextView mTV_preview, mTv_right;
	private ImageLoader mImageLoader;
	private TextView mTV_catalog_name;
	private MediaCatalogPopWindow mCatalogPopWindow;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.zg_activity_media_show);
		mTV_catalog_name = (TextView) findViewById(R.id.tv_catalog_name);
		mTV_catalog_name.setOnClickListener(this);
		mTV_catalog_name.setEnabled(false);

		mTV_preview = (TextView) findViewById(R.id.tv_preview);
		mTV_preview.setOnClickListener(this);

		mTv_right = (TextView) findViewById(R.id.tv_right);
		mTv_right.setOnClickListener(this);

		findViewById(R.id.iv_left).setOnClickListener(this);

		initMode();

		mImageLoader = ImageLoader.getInstance();
		mGridView = (GridView) findViewById(R.id.gridview);
		mGridView.setFastScrollEnabled(true);
		PauseOnScrollListener listener = new PauseOnScrollListener(
				mImageLoader, true, true);
		mGridView.setOnScrollListener(listener);
		mMediaAdapter = new AdapterMediaShow(this, mediaType, mSelectedList);
		mGridView.setAdapter(mMediaAdapter);
		mGridView.setOnItemClickListener(this);

		new Thread(new getMediaInfoTask(null)).start();
	}

	@SuppressWarnings("unchecked")
	private void initMode() {
		pickMode = getIntent().getIntExtra(MediaConstants.MEDIA_PICK_MODE,
				MediaConstants.MODE_SINGLE_PICK);
		mediaType = getIntent().getIntExtra(MediaConstants.MEDIA_RESULT_TYPE,
				MediaConstants.TYPE_PIC);
		mSelectedList = (ArrayList<MediaInfo>) getIntent()
				.getSerializableExtra(MediaConstants.MEDIA_REQUEST_DATAS);

		maxSelectedSize = getIntent().getIntExtra(
				MediaConstants.MEDIA_MAX_MULTIPLE_PICK,
				MediaConstants.MAX_MULTIPLE_PICK);

		mTv_right.setText(getRightText());
		TextView tv_title = (TextView) findViewById(R.id.title);
		if (mediaType == MediaConstants.TYPE_PIC) {
			if (pickMode == MediaConstants.MODE_MULTIPLE_PICK) {
				mTv_right.setVisibility(View.VISIBLE);
			} else {
				mTv_right.setVisibility(View.GONE);
			}
			tv_title.setText(R.string.zg_content_media_pic_select);
			mTV_catalog_name.setText(R.string.zg_content_media_all_pic);
		} else if (mediaType == MediaConstants.TYPE_VIDEO) {
			mTv_right.setVisibility(View.GONE);
			tv_title.setText(R.string.zg_content_media_video_select);
			mTV_preview.setVisibility(View.GONE);
			mTV_catalog_name.setText(R.string.zg_content_media_all_video);
		}

	}

	private String getRightText() {
		if (mSelectedList == null || mSelectedList.size() == 0) {
			return getString(R.string.zg_btn_finish);
		}
		return getString(R.string.zg_btn_finish) + "(" + mSelectedList.size()
				+ "/" + maxSelectedSize + ")";
	}

	private boolean addSelect(MediaInfo mediaInfo) {
		boolean results = false;
		if (mSelectedList == null) {
			mSelectedList = new ArrayList<MediaInfo>();
		}
		if (mSelectedList.size() >= maxSelectedSize) {
			ToastUtils.toastMessage(
					getApplicationContext(),
					getString(R.string.zg_toast_over_max_select_size,
							maxSelectedSize));
			return results;
		}
		results = mSelectedList.add(mediaInfo);
		if (results) {
			mMediaAdapter.removeDeletedMediasByFilePath(mediaInfo);
		}
		mTv_right.setText(getRightText());
		mTv_right.setEnabled(true);
		return results;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		MediaInfo _mediaInfo = (MediaInfo) parent.getItemAtPosition(Long
				.valueOf(id).intValue());
		if (mediaType == MediaConstants.TYPE_PIC) {// 图片模式
			if (position == 0) {// 拍照
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				File imageFile = new File(
						MediaStoreUtils
								.getTempCacheDir(getApplicationContext()),
						"tmp.jpg");
				intent.putExtra(MediaStore.EXTRA_OUTPUT,
						Uri.fromFile(imageFile));
				startActivityForResult(intent,
						MediaConstants.MEDIA_REQUEST_CAMERA);
			} else if (pickMode == MediaConstants.MODE_MULTIPLE_PICK) {// 多选模式
				ViewHolder holder = (ViewHolder) view.getTag();
				if (holder.iv_select.getVisibility() == View.VISIBLE
						&& removeSelect(_mediaInfo)) {
					_mediaInfo.select = false;
					holder.iv_select.setVisibility(View.GONE);
				} else if ((holder.iv_select.getVisibility() == View.GONE || holder.iv_select
						.getVisibility() == View.INVISIBLE)
						&& addSelect(_mediaInfo)) {
					_mediaInfo.select = true;
					holder.iv_select.setVisibility(View.VISIBLE);
				}
			} else if (pickMode == MediaConstants.MODE_SINGLE_PICK) {// 单选模式
				addSelect(_mediaInfo);
				// TODO 直接跳转到选择后的界面
				// CreateTopicActivity.gotoCreateTopic(
				// MediaGridActivity.this, mSelectedList);
			}
		} else if (mediaType == MediaConstants.TYPE_VIDEO) {// 视频模式
			if (position == 0) {// 拍摄视屏
				Intent intent = new Intent(MediaShowActivity.this,
						MediaShootActivity.class);
				startActivityForResult(intent,
						MediaConstants.MEDIA_REQUEST_VIDEO_CODE);
			} else {// 选择视频，弹出对话框确认
				// TODO
			}
		}
	}

	private boolean removeSelect(MediaInfo mediaInfo) {
		boolean results = false;
		if (mSelectedList == null) {
			mSelectedList = new ArrayList<MediaInfo>();
		}
		results = mSelectedList.remove(mediaInfo);
		if (results) {
			mMediaAdapter.removeSelectedMediasByFilePath(mediaInfo);
		}
		if (mSelectedList.size() == 0) {
			mTv_right.setText(getRightText());
			mTv_right.setEnabled(false);
		} else {
			mTv_right.setText(getRightText());
			mTv_right.setEnabled(true);
		}
		return results;
	}

	class getMediaInfoTask implements Runnable {

		private MediaInfo mediaInfo;

		public getMediaInfoTask(MediaInfo mediaInfo) {
			this.mediaInfo = mediaInfo;
		}

		@Override
		public void run() {
			if (mediaInfo != null) {
				List<MediaInfo> tmpMeidas = new ArrayList<MediaInfo>();
				if (mMedias != null) {// 在所有图片中，找出某目录下面的所有图片
					File fileList = new File(mediaInfo.filePath);
					File tmpFile;
					for (int i = 0; i < mMedias.size(); i++) {
						tmpFile = new File(mMedias.get(i).filePath);
						if (tmpFile.getParent().equals(fileList.getParent())) {
							tmpMeidas.add(mMedias.get(i));
						}
					}
					Message files = handler.obtainMessage();
					files.what = TaskState.SUCCESS;
					files.arg1 = -2;
					files.obj = tmpMeidas;
					handler.sendMessage(files);
				}
			} else {// 第一次进来
				handler.obtainMessage(TaskState.ISRUNING).sendToTarget();

				// 1.获取媒体所有目录信息
				Message catalogs = handler.obtainMessage();
				catalogs.what = TaskState.SUCCESS;
				catalogs.arg1 = -1;
				catalogs.obj = MediaStoreUtils.getMediaCatalogInfo(
						getContentResolver(), mediaType);
				handler.sendMessage(catalogs);

				// 2.获取所有媒体
				Message message = handler.obtainMessage();
				message.what = TaskState.SUCCESS;
				message.arg1 = mediaType;
				message.obj = mediaType == MediaConstants.TYPE_VIDEO ? MediaStoreUtils
						.getVideoInfo(getContentResolver()) : MediaStoreUtils
						.getImageInfo(getContentResolver());
				handler.sendMessage(message);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case TaskState.ISRUNING:
				ToastUtils.toastMessage(getApplicationContext(),
						R.string.zg_toast_loading);
				break;
			case TaskState.SUCCESS:
				if (msg.arg1 == -1) {
					mMediaCatalogs = (ArrayList<MediaInfo>) msg.obj;
				} else if (msg.arg1 == mediaType) {
					mMedias = (ArrayList<MediaInfo>) msg.obj;
					if (mMedias != null && mMedias.size() > 1) {
						if (mMediaCatalogs == null) {
							mMediaCatalogs = new ArrayList<MediaInfo>();
						}
						// 将所有媒体添加为媒体目录第一位
						mMediaCatalogs.add(0, mMedias.get(0));
						mTV_catalog_name.setEnabled(true);
					}
					mMediaAdapter.setMedias(mMedias);
				} else if (msg.arg1 == -2) {
					List<MediaInfo> medias = (List<MediaInfo>) msg.obj;
					mGridView.setAdapter(mMediaAdapter);
					mMediaAdapter.setMedias(medias);
				}
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mImageLoader.clearMemoryCache();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == MediaConstants.MEDIA_REQUEST_CAMERA
				&& resultCode == RESULT_OK) {// 拍照返回
			File tmp = new File(
					MediaStoreUtils.getTempCacheDir(getApplicationContext()),
					"tmp.jpg");
			File image = getImageFile();
			MediaStoreUtils.copyFile(tmp.getAbsolutePath(),
					image.getAbsolutePath());
			tmp.delete();
			if (image.exists()) {
				MediaStoreUtils.addImage2Gallery(this,
						new BaseMediaScannerListener(image.getAbsolutePath()));
				// 去预览界面
				MediaPreviewCameraActivity.gotoPreviewCameraFroResult(this,
						new MediaInfo(image.getAbsolutePath()));
			} else {
				ToastUtils.toastMessage(getApplicationContext(),
						R.string.zg_toast_media_not_found);
			}
		} else if (resultCode == MediaConstants.MEDIA_RESULT_VIDEO_CODE) {// 视频返回
			setResult(resultCode, data);
			finish();
		} else if (requestCode == MediaConstants.MEDIA_REQUEST_PIC_CODE
				&& resultCode == RESULT_OK && data != null) {// 预览图片返回，预览图片可以删除图片
			ArrayList<MediaInfo> tempList = (ArrayList<MediaInfo>) data
					.getSerializableExtra(MediaConstants.MEDIA_RESULT_DATAS);
			if (tempList == null) {
				return;
			}
			ArrayList<MediaInfo> preList = new ArrayList<MediaInfo>();
			ArrayList<MediaInfo> deletedMedias = new ArrayList<MediaInfo>();
			preList.addAll(mSelectedList);
			for (MediaInfo mediaInfo : preList) {
				if (tempList.contains(mediaInfo)) {
					continue;
				}
				deletedMedias.add(mediaInfo);
				removeSelect(mediaInfo);
			}
			preList.clear();
			preList = null;
			if (deletedMedias.size() > 0) {
				mMediaAdapter.addDeletedMedias(deletedMedias);
			}
			deletedMedias.clear();
			deletedMedias = null;
		} else if (requestCode == MediaConstants.MEDIA_REQUEST_CAMERA_PIC
				&& resultCode == RESULT_OK
				&& data != null
				&& data.getSerializableExtra(MediaConstants.MEDIA_RESULT_DATAS) != null) {
			MediaInfo mediaInfo = (MediaInfo) data
					.getSerializableExtra(MediaConstants.MEDIA_RESULT_DATAS);
			// 拍照后，预览界面，点击完成后返回
			if (mediaInfo == null) {
				return;
			}
			if (mSelectedList == null) {
				mSelectedList = new ArrayList<MediaInfo>();
			}
			mSelectedList.add(mediaInfo);
			Intent newData = new Intent();
			newData.putExtra(MediaConstants.MEDIA_RESULT_DATAS, mSelectedList);
			setResult(RESULT_OK, newData);
			finish();
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.iv_left) {
			finish();
		} else if (v.getId() == R.id.tv_right) {
			Intent data = new Intent();
			data.putExtra(MediaConstants.MEDIA_RESULT_DATAS, mSelectedList);
			setResult(RESULT_OK, data);
			finish();
		} else if (v.getId() == R.id.tv_catalog_name) {// 选择目录
			showFileList(v);
		} else if (v.getId() == R.id.tv_preview) {// 预览
			if (mSelectedList != null && mSelectedList.size() > 0) {
				MediaPreviewActivity.gotoPreviewForResult(this, mSelectedList,
						0, 1);
			}
		}
	}

	public void showFileList(View v) {
		if (mCatalogPopWindow == null) {
			mCatalogPopWindow = new MediaCatalogPopWindow(this, mMediaCatalogs,
					mediaType);
			mCatalogPopWindow.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					MediaInfo info = (MediaInfo) parent
							.getItemAtPosition(position);
					if (position == 0) {// 所有图片
						mTV_catalog_name.setText(MediaCatalogAdapter
								.getMediaText(mediaType, MediaShowActivity.this));
						mGridView.setAdapter(mMediaAdapter);
						mMediaAdapter.setMedias(mMedias);
					} else if (info != null) {// 其他选择的目录
						mTV_catalog_name.setText(new File(info.filePath)
								.getParentFile().getName());
						new Thread(new getMediaInfoTask(info)).start();
					}
				}
			});
		}
		if (mCatalogPopWindow.isShowing()) {
			mCatalogPopWindow.dismiss();
		} else {
			mCatalogPopWindow.show(v);
		}
	}

	private File getImageFile() {
		String name = "AG_" + System.currentTimeMillis() + ".jpg";
		File image = new File(MediaStoreUtils.getOwnImageCacheDir(this), name);
		return image;
	}

	/***
	 * 进入视频选择界面
	 * 
	 * @param context
	 */
	public static void gotoVideoForResult(Fragment fragment, int PickMode) {
		Intent intent = new Intent(fragment.getActivity(),
				MediaShowActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra(MediaConstants.MEDIA_PICK_MODE, PickMode);
		intent.putExtra(MediaConstants.MEDIA_RESULT_TYPE,
				MediaConstants.TYPE_VIDEO);
		fragment.startActivityForResult(intent,
				MediaConstants.MEDIA_RESULT_VIDEO_CODE);
	}

	public static void gotoPicForResult(FragmentActivity activity,
			int PickMode, int maxPicks) {
		gotoPicForResult(activity, PickMode, maxPicks, null);
	}

	/***
	 * 进入图片选择界面
	 * 
	 * @param activity
	 * @param PickMode
	 */
	public static void gotoPicForResult(FragmentActivity activity,
			int PickMode, int maxPicks, ArrayList<MediaInfo> selectedMediaInfos) {
		Intent intent = new Intent(activity, MediaShowActivity.class);
		intent.putExtra(MediaConstants.MEDIA_PICK_MODE, PickMode);
		intent.putExtra(MediaConstants.MEDIA_REQUEST_DATAS, selectedMediaInfos);
		intent.putExtra(MediaConstants.MEDIA_RESULT_TYPE,
				MediaConstants.TYPE_PIC);
		intent.putExtra(MediaConstants.MEDIA_MAX_MULTIPLE_PICK, maxPicks);
		activity.startActivityForResult(intent,
				MediaConstants.MEDIA_REQUEST_PIC_CODE);
	}

	/***
	 * 进入图片选择界面
	 * 
	 * @param activity
	 * @param PickMode
	 */
	public static void gotoPicForResult(Fragment fragment, int PickMode,
			int maxPicks, ArrayList<MediaInfo> selectedMediaInfos) {
		Intent intent = new Intent(fragment.getActivity(),
				MediaShowActivity.class);
		intent.putExtra(MediaConstants.MEDIA_PICK_MODE, PickMode);
		intent.putExtra(MediaConstants.MEDIA_REQUEST_DATAS, selectedMediaInfos);
		intent.putExtra(MediaConstants.MEDIA_RESULT_TYPE,
				MediaConstants.TYPE_PIC);
		intent.putExtra(MediaConstants.MEDIA_MAX_MULTIPLE_PICK, maxPicks);
		fragment.startActivityForResult(intent,
				MediaConstants.MEDIA_REQUEST_PIC_CODE);
	}

}
