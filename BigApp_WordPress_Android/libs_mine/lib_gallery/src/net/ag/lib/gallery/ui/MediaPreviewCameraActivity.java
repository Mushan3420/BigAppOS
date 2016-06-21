package net.ag.lib.gallery.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.download.ImageDownloader.Scheme;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import net.ag.lib.gallery.R;
import net.ag.lib.gallery.util.MediaConstants;
import net.ag.lib.gallery.util.MediaInfo;
import net.ag.lib.gallery.util.ViewerImageLoader;

import uk.co.senab.photoview.PhotoViewAttacher;

/***
 * 拍照完成后的界面
 *
 * @author macy
 */
public class MediaPreviewCameraActivity extends FragmentActivity implements
        OnClickListener, ImageLoadingListener {

    private MediaInfo mMediaInfo;
    private ImageView mImageView;
    private ProgressBar mProgress;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.zg_activity_media_camera_preview);
        findViewById(R.id.iv_left).setOnClickListener(this);
        findViewById(R.id.tv_right).setOnClickListener(this);
        ((TextView) findViewById(R.id.title))
                .setText(R.string.zg_content_media_pic_preview);

        mProgress = (ProgressBar) findViewById(R.id.progressBar);
        mImageView = (ImageView) findViewById(R.id.imageview);

        mMediaInfo = (MediaInfo) getIntent().getExtras().getSerializable(
                MediaConstants.MEDIA_REQUEST_DATAS);

        ViewerImageLoader.getInstance().displayImage(
                Scheme.FILE.wrap(mMediaInfo.filePath), mImageView, this);
    }

    @Override
    public void onClick(View v) {
        int vId = v.getId();
        if (vId == R.id.iv_left) {
            onBackPressed();
        } else if (vId == R.id.tv_right) {
            Intent data = new Intent();
            data.putExtra(MediaConstants.MEDIA_RESULT_DATAS, mMediaInfo);
            setResult(RESULT_OK, data);
            onBackPressed();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onLoadingStarted(String arg0, View arg1) {
        mProgress.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoadingFailed(String imageUri, View view,
                                FailReason failReason) {
        mImageView.setScaleType(ImageView.ScaleType.CENTER);
        mProgress.setVisibility(View.GONE);
        PhotoViewAttacher photo = new PhotoViewAttacher(mImageView);
        photo.setMaxScale(8);
    }

    @Override
    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
        mImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        mProgress.setVisibility(View.GONE);
        PhotoViewAttacher photo = new PhotoViewAttacher(mImageView);
        photo.setMaxScale(10);
    }

    @Override
    public void onLoadingCancelled(String imageUri, View view) {
        mProgress.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mImageView != null) {
            mImageView.setImageDrawable(null);
        }
    }

    public static void gotoPreviewCameraFroResult(Context context, MediaInfo mediaInfo) {
        Intent intent = new Intent(context, MediaPreviewCameraActivity.class);
        intent.putExtra(MediaConstants.MEDIA_REQUEST_DATAS, mediaInfo);
        ((Activity) context).startActivityForResult(intent,
                MediaConstants.MEDIA_REQUEST_CAMERA_PIC);
    }

    public static void gotoPreviewCameraFroResult(Fragment fragment, MediaInfo mediaInfo) {
        Intent intent = new Intent(fragment.getActivity(), MediaPreviewCameraActivity.class);
        intent.putExtra(MediaConstants.MEDIA_REQUEST_DATAS, mediaInfo);
        fragment.startActivityForResult(intent,
                MediaConstants.MEDIA_REQUEST_CAMERA_PIC);
    }

}
