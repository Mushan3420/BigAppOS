/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.ag.lib.gallery.ui;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.download.ImageDownloader.Scheme;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import net.ag.lib.gallery.R;
import net.ag.lib.gallery.ui.BaseFragmentActivity.FragmentCallBack;
import net.ag.lib.gallery.util.MediaConstants;
import net.ag.lib.gallery.util.MediaInfo;
import net.ag.lib.gallery.util.ViewerImageLoader;

import uk.co.senab.photoview.PhotoViewAttacher;
import uk.co.senab.photoview.PhotoViewAttacher.OnPhotoTapListener;
import uk.co.senab.photoview.PhotoViewAttacher.OnViewTapListener;

/***
 * 
 * 图片大图展示容器
 * 
 * @author AG
 *
 */
public class MediaDetailFragment extends Fragment implements
		OnLongClickListener, OnViewTapListener, OnPhotoTapListener,
		ImageLoadingListener {
	private MediaInfo mMediaInfo;
	private ImageView mImageView;
	private ViewerImageLoader mImageLoader;
	private ProgressBar mProgress;
	private FragmentCallBack mCallBack;

	public static MediaDetailFragment newInstance(MediaInfo mMediaInfo) {
		final MediaDetailFragment f = new MediaDetailFragment();
		final Bundle args = new Bundle();
		args.putSerializable(MediaConstants.MEDIA_REQUEST_DATAS, mMediaInfo);
		f.setArguments(args);
		return f;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mCallBack = (FragmentCallBack) activity;
		} catch (Exception e) {
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mMediaInfo = (MediaInfo) getArguments().getSerializable(
				MediaConstants.MEDIA_REQUEST_DATAS);
		mImageLoader = ViewerImageLoader.getInstance();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final View v = inflater.inflate(R.layout.zg_fragment_image_detail,
				container, false);
		mProgress = (ProgressBar) v.findViewById(R.id.progressBar);
		mImageView = (ImageView) v.findViewById(R.id.imageview);
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mImageLoader.displayImageWp(getImageUrl(true), mImageView, this);
	}

	private String getImageUrl(boolean display) {
		if (mMediaInfo == null) {
			return "";
		}
		if (mMediaInfo.type == 0) {
			return mMediaInfo.url;
		} else {
			return display ? Scheme.FILE.wrap(mMediaInfo.filePath)
					: mMediaInfo.filePath;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mImageView != null) {
			mImageView.setImageDrawable(null);
		}
	}

	@Override
	public boolean onLongClick(View v) {
		try {
			MediaInfo mInfo = (MediaInfo) mMediaInfo.clone();
			mInfo.filePath = getImageUrl(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public void onViewTap(View arg0, float arg1, float arg2) {
		if (getActivity() != null) {
			mCallBack.fragmentChanged("", null, true);
		}
	}

	@Override
	public void onPhotoTap(View arg0, float arg1, float arg2) {
		if (getActivity() != null) {
			mCallBack.fragmentChanged("", null, true);
		}
	}

	@Override
	public void onLoadingStarted(String imageUri, View view) {
		mProgress.setVisibility(View.VISIBLE);
	}

	@Override
	public void onLoadingFailed(String imageUri, View view,
			FailReason failReason) {
		mImageView.setScaleType(ImageView.ScaleType.CENTER);
		mProgress.setVisibility(View.GONE);
		PhotoViewAttacher photo = new PhotoViewAttacher(mImageView);
		photo.setMaxScale(8);
		photo.setOnPhotoTapListener(this);
		photo.setOnViewTapListener(this);
	}

	@Override
	public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
		mImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
		mProgress.setVisibility(View.GONE);
		PhotoViewAttacher photo = new PhotoViewAttacher(mImageView);
		photo.setMaxScale(10);
		photo.setOnPhotoTapListener(this);
		photo.setOnViewTapListener(this);
	}

	@Override
	public void onLoadingCancelled(String imageUri, View view) {
		mProgress.setVisibility(View.GONE);
	}
}
