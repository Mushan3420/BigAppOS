package net.ag.lib.gallery.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.ag.lib.gallery.R;
import net.ag.lib.gallery.util.MediaConstants;
import net.ag.lib.gallery.util.MediaInfo;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.media.MediaRecorder;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

/***
 * 拍摄视频
 * 
 * @author AG
 *
 */
@SuppressLint("InlinedApi")
public class MediaShootActivity extends FragmentActivity implements
		OnClickListener, SurfaceHolder.Callback, MediaScannerConnectionClient {

	private SurfaceView mSurfaceView;
	private SurfaceHolder mSurfaceHolder;
	private TextView mFacing;
	private ImageView mShoot;
	private MediaRecorder mMediaRecorder;
	private Camera camera;

	private int Model = 0; // 0 照片 1视频 2 视频和照片
	private ToggleButton toggle_model;
	private ToggleButton toggle_facing;
	private boolean SHOOTMODEL = true; // 视频 or 拍照
	private boolean isRecording = false;
	private boolean isCamOpen = false;
	private int defaultCam = CameraInfo.CAMERA_FACING_BACK;
	private int defaultVideoWidth = 640;
	private int defaultVideoHeight = 480;
	private int defaultVideoFrameRate = 25;
	private int defaultOrientation = 90;

	private int mVideoWidth = -1;
	private int mVideoHeight = -1;
	private int mPreviewWidth = -1;
	private int mPreviewHeight = -1;
	private int mVideoFrameRate = -1;

	private File video;
	private File picture;

	private ArrayList<CameraInfo> mCameras = new ArrayList<CameraInfo>();
	private Chronometer mChronometer;
	private MediaScannerConnection mConnection;

	private boolean sendVideo = true;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.zg_activity_video_shoot);
		mFacing = (TextView) findViewById(R.id.tv_facing);
		mSurfaceView = (SurfaceView) findViewById(R.id.surfaceview_shoot);
		mShoot = (ImageView) findViewById(R.id.img_shoot);
		mChronometer = (Chronometer) findViewById(R.id.video_chronometer);
		mFacing.setOnClickListener(this);
		mShoot.setOnClickListener(this);

		initToggle();
		findViewById(R.id.iv_left).setOnClickListener(this);

		SurfaceHolder mHolder = mSurfaceView.getHolder();
		mHolder.addCallback(this);
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	public void initToggle() {
		Model = getIntent().getIntExtra("Model", 1);

		toggle_model = (ToggleButton) findViewById(R.id.toggle_model);
		toggle_facing = (ToggleButton) findViewById(R.id.toggle_facing);
		toggle_model.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				SHOOTMODEL = isChecked;
			}
		});
		toggle_facing.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				switchCamera();
			}
		});

		if (Model == 0) {
			toggle_model.setVisibility(View.GONE);
			SHOOTMODEL = false;
		} else if (Model == 1) {
			toggle_model.setVisibility(View.GONE);
			SHOOTMODEL = true;
		} else if (Model == 2) {
			toggle_model.setVisibility(View.VISIBLE);
			SHOOTMODEL = false;
			toggle_model.setChecked(SHOOTMODEL);
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.img_shoot) {
			if (SHOOTMODEL) {
				if (!isRecording) {
					initMediaRecorder();
					startShoot();
				} else {
					stopShoot();
				}
			} else {
				takePicture();
			}
		} else if (v.getId() == R.id.tv_facing) {
			switchCamera();
		} else if (v.getId() == R.id.iv_left) {
			finish();
		}
	}

	@SuppressLint("NewApi")
	public void initMediaRecorder() {
		try {
			video = new File(getDCIM(), "TomatoTown_"
					+ DateFormat.format("yyyyMMdd_hhmmss",
							System.currentTimeMillis()).toString() + ".mp4");
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (video == null) {
			Toast.makeText(this, "空间不足", Toast.LENGTH_SHORT).show();
			// TODO
			return;
		}

		mMediaRecorder = new MediaRecorder();
		mMediaRecorder.reset();
		if (camera != null) {
			camera.unlock();
			mMediaRecorder.setCamera(camera);
		}

		mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
		mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
		mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
		mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
		mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
		mMediaRecorder.setAudioChannels(1);
		mMediaRecorder.setAudioSamplingRate(44100);
		mMediaRecorder.setAudioEncodingBitRate(64);

		if (mVideoWidth != -1 && mVideoHeight != -1)
			mMediaRecorder.setVideoSize(mVideoWidth, mVideoHeight);
		if (mVideoFrameRate != -1)
			mMediaRecorder.setVideoFrameRate(mVideoFrameRate);
		mMediaRecorder.setVideoEncodingBitRate(1 * 768 * 1024);
		mMediaRecorder.setOrientationHint(defaultOrientation);
		mMediaRecorder.setMaxDuration(MediaConstants.MAX_DURATION_MS);
		mMediaRecorder.setOutputFile(video.getAbsolutePath());
		mMediaRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());
		mMediaRecorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {
			@Override
			public void onError(MediaRecorder mr, int what, int extra) {
				Log.d("MediaRecorder", "onError what " + what + " extra "
						+ extra);
			}
		});
		// mMediaRecorder.setProfile(CamcorderProfile
		// .get(CamcorderProfile.QUALITY_HIGH));
		mMediaRecorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
			@Override
			public void onInfo(MediaRecorder mr, int what, int extra) {
				Log.d("MediaRecorder", "onInfo what " + what + " extra "
						+ extra);
				if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
					stopShoot();
				}
			}
		});
	}

	public void takePicture() {
		camera.autoFocus(new Camera.AutoFocusCallback() {

			@Override
			public void onAutoFocus(boolean success, Camera camera) {
				try {
					picture = new File(getDCIM(), "TomatoTown_"
							+ DateFormat.format("yyyyMMdd_hhmmss",
									System.currentTimeMillis()).toString()
							+ ".jpg");
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (picture == null) {
					Toast.makeText(MediaShootActivity.this, "空间不足",
							Toast.LENGTH_SHORT).show();
					// TODO
					return;
				}
				if (success) {
					camera.takePicture(null, null, new PictureCallback() {

						@Override
						public void onPictureTaken(byte[] data, Camera camera) {
							try {
								FileOutputStream fos = new FileOutputStream(
										picture);
								Bitmap p = BitmapFactory.decodeByteArray(data,
										0, data.length);
								p.compress(Bitmap.CompressFormat.JPEG, 100, fos);
								if (fos != null) {
									fos.close();
								}
								SendPictureDialog();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});
				}
			}
		});
	}

	public void startShoot() {
		if (isRecording)
			return;
		try {
			if (mMediaRecorder != null) {
				// TODO
				mShoot.setImageResource(R.drawable.ic_launcher);
				isRecording = true;
				mMediaRecorder.prepare();
				mMediaRecorder.start();
				mChronometer.setVisibility(View.VISIBLE);
				mChronometer.setBase(SystemClock.elapsedRealtime());
				mChronometer.start();
			}
		} catch (Exception e) {
			e.printStackTrace();
			isRecording = false;
		}
	}

	public void stopShoot() {
		if (!isRecording)
			return;
		try {
			if (mMediaRecorder != null) {
				// TODO
				mShoot.setImageResource(R.drawable.ic_launcher);
				isRecording = false;
				mChronometer.stop();
				mMediaRecorder.stop();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			if (camera != null) {
				isCamOpen = false;
				camera.stopPreview();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		showSendVideoDialog();
	}

	public void switchCamera() {
		if (mCameras.size() > 1) {
			if (defaultCam == CameraInfo.CAMERA_FACING_BACK) {
				defaultCam = CameraInfo.CAMERA_FACING_FRONT;
				mSurfaceView.setVisibility(View.GONE);
				mSurfaceView.setVisibility(View.VISIBLE);
			} else if (defaultCam == CameraInfo.CAMERA_FACING_FRONT) {
				defaultCam = CameraInfo.CAMERA_FACING_BACK;
				mSurfaceView.setVisibility(View.GONE);
				mSurfaceView.setVisibility(View.VISIBLE);
			}
		}
	}

	@SuppressLint("NewApi")
	public void openCamera() {
		try {
			mCameras.clear();
			int numberOfCameras = Camera.getNumberOfCameras();
			if (numberOfCameras == 1) {
				mFacing.setVisibility(View.GONE);
				toggle_facing.setVisibility(View.GONE);
			} else if (numberOfCameras > 1) {
				mFacing.setVisibility(View.VISIBLE);
				toggle_facing.setVisibility(View.VISIBLE);
			}

			for (int i = 0; i < numberOfCameras; i++) {
				CameraInfo cameraInfo = new CameraInfo();
				Camera.getCameraInfo(i, cameraInfo);
				mCameras.add(cameraInfo);
			}

			if (camera == null) {
				openCameraByType();
			}

			if (defaultCam == CameraInfo.CAMERA_FACING_BACK) {
				mFacing.setText("前置");
			} else if (defaultCam == CameraInfo.CAMERA_FACING_FRONT) {
				mFacing.setText("后置");
			}

			if (camera != null) {
				isCamOpen = true;
				initVideoSize();
				initFrameRates();
				camera.setDisplayOrientation(90);
				camera.stopPreview();
				camera.setPreviewDisplay(mSurfaceHolder);
				camera.startPreview();
			} else {
				Toast.makeText(this, "摄像头打开失败！", Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(this, "摄像头打开失败！", Toast.LENGTH_SHORT).show();
		}
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void initVideoSize() {
		CameraSizeComparator sizeComparator = new CameraSizeComparator();
		boolean hasdef = false;
		Camera.Parameters mParameters = camera.getParameters();
		List<Size> list = mParameters.getSupportedVideoSizes();
		List<Size> list1 = mParameters.getSupportedPreviewSizes();
		if (list != null && list.size() > 0) {
			Collections.sort(list, sizeComparator);
			for (int i = 0; i < list.size(); i++) {
				Size size = list.get(i);
				if (size.width == defaultVideoWidth
						&& size.height == defaultVideoHeight) {
					mVideoWidth = defaultVideoWidth;
					mVideoHeight = defaultVideoHeight;
					hasdef = true;
				}
			}

			if (!hasdef) {
				int pos = list.size() / 2;
				if (pos >= list.size())
					pos = list.size() - 1;
				Size size = list.get(pos);
				mVideoWidth = size.width;
				mVideoHeight = size.height;
			}
		}

		if (defaultCam == CameraInfo.CAMERA_FACING_FRONT && list1 != null
				&& list1.size() > 0) {
			Collections.sort(list1, sizeComparator);
			for (int i = 0; i < list1.size(); i++) {
				Size size = list1.get(i);
				if (size.width == defaultVideoWidth
						&& size.height == defaultVideoHeight) {
					mPreviewWidth = defaultVideoWidth;
					mPreviewHeight = defaultVideoHeight;
					hasdef = true;
				}
			}
			if (!hasdef) {
				int pos = list1.size() / 2;
				if (pos >= list1.size())
					pos = list1.size() - 1;
				Size size = list1.get(pos);
				mPreviewWidth = size.width;
				mPreviewHeight = size.height;
			}
			if (mPreviewWidth > 0 && mPreviewHeight > 0) {
				mParameters.setPreviewSize(mPreviewWidth, mPreviewHeight);
				camera.setParameters(mParameters);
			}
		}
	}

	@SuppressWarnings("deprecation")
	public void initFrameRates() {
		boolean hasdef = false;
		Camera.Parameters mParameters = camera.getParameters();
		List<Integer> list = mParameters.getSupportedPreviewFrameRates();
		if (list == null || list.size() == 0)
			return;
		for (int i = 0; i < list.size(); i++) {
			int mFrameRate = list.get(i);
			if (mFrameRate == defaultVideoFrameRate) {
				mVideoFrameRate = defaultVideoFrameRate;
				hasdef = true;
			}
		}

		if (!hasdef) {
			int pos = list.size() / 2;
			if (pos >= list.size())
				pos = list.size() - 1;
			mVideoFrameRate = list.get(pos);
		}
	}

	@SuppressLint("NewApi")
	public void openCameraByType() {
		try {
			if (defaultCam == CameraInfo.CAMERA_FACING_BACK) {
				camera = Camera.open(CameraInfo.CAMERA_FACING_BACK);
				CameraInfo cameraInfo = new CameraInfo();
				Camera.getCameraInfo(CameraInfo.CAMERA_FACING_BACK, cameraInfo);
				defaultCam = cameraInfo.facing;
				defaultOrientation = cameraInfo.orientation;
			} else if (defaultCam == CameraInfo.CAMERA_FACING_FRONT) {
				camera = Camera.open(CameraInfo.CAMERA_FACING_FRONT);
				CameraInfo cameraInfo = new CameraInfo();
				Camera.getCameraInfo(CameraInfo.CAMERA_FACING_FRONT, cameraInfo);
				defaultCam = cameraInfo.facing;
				defaultOrientation = cameraInfo.orientation;
			}
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(this, "摄像头打开失败！", Toast.LENGTH_SHORT).show();
		}
	}

	public File getDCIM() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			File mCamera = new File(
					Environment
							.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
					"Camera");
			if (!mCamera.exists()) {
				mCamera.mkdirs();
			}
			return mCamera;
		}
		return null;
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mSurfaceHolder = holder;
		openCamera();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		mSurfaceHolder = holder;
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		mSurfaceHolder = null;
		release();
	}

	public void showSendVideoDialog() {
		if (video == null) {
			Log.e("sendVideo", "video is not null!");
			return;
		}
		AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
		mBuilder.setMessage("发送视频");
		mBuilder.setCancelable(false);
		mBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				sendVideo = true;
				sendMedia();
			}
		});
		mBuilder.setNegativeButton("失败", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				showSaveDialog();
			}
		});
		mBuilder.show();
	}

	public void showSaveDialog() {
		AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
		mBuilder.setMessage("保存" + (SHOOTMODEL ? "视频" : "图片"));
		mBuilder.setCancelable(false);
		mBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				sendVideo = false;
				sendMedia();
			}
		});
		mBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				if (video != null)
					video.delete();
				if (picture != null)
					picture.delete();
				setResult(RESULT_CANCELED);
				finish();
			}
		});
		mBuilder.show();
	}

	public void SendPictureDialog() {
		if (picture == null) {
			Log.e("sendVideo", "video is not null!");
			return;
		}
		AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
		mBuilder.setMessage("发送图片");
		mBuilder.setCancelable(false);
		mBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				sendVideo = true;
				sendMedia();
			}
		});
		mBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				showSaveDialog();
			}
		});
		mBuilder.show();
	}

	public void sendMedia() {
		mConnection = new MediaScannerConnection(this, this);
		mConnection.connect();
	}

	public int getDuration() {
		String str = mChronometer.getText().toString();
		String[] duration = str.split(":");
		if (duration.length == 2) {
			int m = Integer.parseInt(duration[0]) * 60 * 1000;
			int s = Integer.parseInt(duration[1]) * 1000;
			return m + s;
		}
		if (duration.length == 3) {
			int h = Integer.parseInt(duration[0]) * 60 * 60 * 1000;
			int m = Integer.parseInt(duration[1]) * 60 * 1000;
			int s = Integer.parseInt(duration[2]) * 1000;
			return h + m + s;
		}
		return 0;
	}

	public void release() {
		try {
			if (mMediaRecorder != null) {
				if (isRecording) {
					// TODO
					mShoot.setImageResource(R.drawable.ic_launcher);
					mChronometer.setVisibility(View.GONE);
					isRecording = false;
					mChronometer.stop();
					mMediaRecorder.stop();
					if (video != null && video.exists())
						video.delete();
				}
				mMediaRecorder.release();
				mMediaRecorder = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			if (camera != null) {
				if (isCamOpen) {
					isCamOpen = false;
					camera.stopPreview();
				}
				camera.release();
				camera = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onMediaScannerConnected() {
		mConnection.scanFile(
				SHOOTMODEL ? video.getAbsolutePath() : picture
						.getAbsolutePath(), SHOOTMODEL ? "video/mp4"
						: "image/jpeg");
	}

	@Override
	public void onScanCompleted(String path, Uri uri) {
		mConnection.disconnect();
		if (sendVideo) {
			Intent result = getIntent();
			result.putExtra(MediaConstants.MEDIA_RESULT_TYPE,
					SHOOTMODEL ? MediaConstants.TYPE_VIDEO
							: MediaConstants.TYPE_PIC);
			ArrayList<MediaInfo> list = new ArrayList<MediaInfo>();
			MediaInfo info = new MediaInfo();
			info.filePath = SHOOTMODEL ? video.getAbsolutePath() : picture
					.getAbsolutePath();
			info.duration = SHOOTMODEL ? getDuration() : 0;
			list.add(info);
			result.putExtra(MediaConstants.MEDIA_RESULT_DATAS, list);
			setResult(MediaConstants.MEDIA_RESULT_VIDEO_CODE, result);
		} else {
			setResult(RESULT_CANCELED);
		}
		finish();
	}

	public class CameraSizeComparator implements Comparator<Camera.Size> {
		public int compare(Size lhs, Size rhs) {
			if (lhs.width == rhs.width) {
				return 0;
			} else if (lhs.width > rhs.width) {
				return 1;
			} else {
				return -1;
			}
		}

	}

}
