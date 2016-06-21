package net.ag.lib.gallery.adapter;

import java.util.ArrayList;
import java.util.List;

import net.ag.lib.gallery.R;
import net.ag.lib.gallery.util.MediaInfo;
import net.ag.lib.gallery.util.ViewerImageLoader;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.nostra13.universalimageloader.core.download.ImageDownloader.Scheme;

public class AdapterGridAdd extends BaseAdapter {

	private List<MediaInfo> mMediaInfoList;
	private LayoutInflater inflate;
	private ViewerImageLoader mImageLoader;
	private Resources mRes;

	public AdapterGridAdd(Context context) {
		mRes = context.getResources();
		this.inflate = LayoutInflater.from(context);
		this.mImageLoader = ViewerImageLoader.getInstance();
		this.mMediaInfoList = new ArrayList<MediaInfo>();
	}

	@Override
	public int getCount() {
		if (mMediaInfoList != null) {
			return mMediaInfoList.size();
		}
		return 0;
	}

	public void setVideos(List<MediaInfo> videos, int maxSize) {
		this.mMediaInfoList.clear();
		this.mMediaInfoList.addAll(videos);
		if (mMediaInfoList.size() < maxSize) {
			mMediaInfoList.add(new MediaInfo("Add"));
		}
		notifyDataSetChanged();
	}

	@Override
	public MediaInfo getItem(int position) {
		if (position < getCount()) {
			return mMediaInfoList.get(position);
		}
		return new MediaInfo("Add");
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflate.inflate(R.layout.zg_adapter_item_grid_add,
					null);
			holder.imageView = (ImageView) convertView
					.findViewById(R.id.imageview);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		ViewGroup.LayoutParams params = holder.imageView.getLayoutParams();
		params.height = getImageWidth(parent);
		holder.imageView.setLayoutParams(params);
		MediaInfo info = getItem(position);
		if (info != null && "Add".equals(info.filePath)) {
			holder.imageView.setBackgroundColor(Color.parseColor("#41D0A4"));
			holder.imageView.setScaleType(ScaleType.CENTER);
			mImageLoader.displayImageAndCached(
					Scheme.DRAWABLE.wrap("" + R.drawable.zg_default_add),
					holder.imageView);
		} else if (info != null) {
			holder.imageView.setScaleType(ScaleType.CENTER_CROP);
			if (info.type == 0) {
				mImageLoader.displayImageAndCached(info.url, holder.imageView);
			} else {
				mImageLoader.displayImageAndCached(
						Scheme.FILE.wrap(info.filePath), holder.imageView);
			}
		}
		return convertView;
	}

	static class ViewHolder {
		ImageView imageView;
	}

	public int getImageWidth(ViewGroup parent) {
		int padding = mRes
				.getDimensionPixelSize(R.dimen.zg_grid_horizontal_spacing_l);
		return (parent.getMeasuredWidth() - 3 * padding) / 4;
	}

}
