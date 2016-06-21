package net.ag.lib.gallery.adapter;

import java.util.ArrayList;
import java.util.List;

import net.ag.lib.gallery.R;
import net.ag.lib.gallery.util.MediaConstants;
import net.ag.lib.gallery.util.MediaInfo;
import net.ag.lib.gallery.util.ViewerImageLoader;
import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.download.ImageDownloader.Scheme;

/***
 * 媒体文件显示适配器
 * 
 * @author AG
 *
 */
public class AdapterMediaShow extends BaseAdapter {

	private List<MediaInfo> mMedias;
	private LayoutInflater inflate;
	private Context context;
	private ViewerImageLoader mImageLoader;
	private int MediaType;
	private LayoutParams mParams;
	/***
	 * 已经选择的媒体文件
	 */
	private ArrayList<MediaInfo> mSelectedMedias = new ArrayList<MediaInfo>();
	private ArrayList<MediaInfo> mDeletedMedias = new ArrayList<MediaInfo>();

	public AdapterMediaShow(Context context, int mediaType,
			ArrayList<MediaInfo> selectedMedias) {
		if (selectedMedias != null) {
			mSelectedMedias.addAll(selectedMedias);
		}
		this.context = context;
		this.MediaType = mediaType;
		this.inflate = LayoutInflater.from(context);
		this.mImageLoader = ViewerImageLoader.getInstance();
		mParams = new LayoutParams(getThumbWidth(), getThumbWidth());
	}

	/***
	 * 如果在列表界面选择了图片的时候，删除已选择的
	 * 
	 * @param filePath
	 */
	public void removeSelectedMediasByFilePath(MediaInfo mediaInfo) {
		if (mSelectedMedias == null || mSelectedMedias.size() == 0) {
			return;
		}
		if (mediaInfo != null) {
			mSelectedMedias.remove(mediaInfo);
		}
	}

	/***
	 * 如果在列表界面，取消选择的时候，删除已删除的
	 * 
	 * @param filePath
	 */
	public void removeDeletedMediasByFilePath(MediaInfo mediaInfo) {
		if (mDeletedMedias == null || mDeletedMedias.size() == 0) {
			return;
		}
		if (mediaInfo != null) {
			mDeletedMedias.remove(mediaInfo);
		}
	}

	/***
	 * 添加在预览界面删除的medias
	 * 
	 * @param deletedMedias
	 *            not null
	 */
	public void addDeletedMedias(ArrayList<MediaInfo> deletedMedias) {
		mDeletedMedias.addAll(deletedMedias);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		if (mMedias != null) {
			return mMedias.size() + 1;
		}
		return 1;
	}

	public List<MediaInfo> getMedias() {
		return mMedias;
	}

	public void setMedias(List<MediaInfo> medias) {
		this.mMedias = medias;
		notifyDataSetChanged();
	}

	@Override
	public MediaInfo getItem(int position) {
		if (position < getCount() - 1) {
			return mMedias.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		if (position == 0) {
			return position;
		} else {
			return position - 1;
		}
	}

	public int getId(int position) {
		return Long.valueOf(getItemId(position)).intValue();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflate.inflate(R.layout.zg_adapter_item_grid_show,
					null);
			holder.tv_camera = (TextView) convertView
					.findViewById(R.id.tv_camera);
			holder.iv_select = (ImageView) convertView
					.findViewById(R.id.iv_select);
			holder.iv_content = (ImageView) convertView
					.findViewById(R.id.iv_content);
			ViewGroup.LayoutParams params = holder.iv_content.getLayoutParams();
			params.width = mParams.width;
			params.height = mParams.height;
			holder.iv_content.setLayoutParams(params);
			convertView.setLayoutParams(mParams);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (position == 0) {
			holder.tv_camera.setText(getMediaText());
			holder.tv_camera.setVisibility(View.VISIBLE);
			holder.iv_content.setVisibility(View.GONE);
		} else if (MediaType == MediaConstants.TYPE_VIDEO) {
			holder.tv_camera.setVisibility(View.GONE);
			holder.iv_content.setVisibility(View.VISIBLE);
			mImageLoader.displayImageAndCachedMemory(
					Scheme.FILE.wrap(getItem(getId(position)).filePath),
					holder.iv_content);
		} else if (MediaType == MediaConstants.TYPE_PIC) {
			holder.tv_camera.setVisibility(View.GONE);
			holder.iv_content.setVisibility(View.VISIBLE);
			MediaInfo _mediaInfo = getItem(getId(position));
			if (_mediaInfo.select) {
				holder.iv_select.setVisibility(View.VISIBLE);
				if (mDeletedMedias != null && mDeletedMedias.size() > 0
						&& mDeletedMedias.remove(_mediaInfo)) {
					_mediaInfo.select = false;
					holder.iv_select.setVisibility(View.GONE);
				}
			} else {
				holder.iv_select.setVisibility(View.GONE);
				if (mSelectedMedias != null && mSelectedMedias.size() > 0
						&& mSelectedMedias.remove(_mediaInfo)) {
					_mediaInfo.select = true;
					holder.iv_select.setVisibility(View.VISIBLE);
				}
			}
			mImageLoader.displayImageAndCachedMemory(
					Scheme.FILE.wrap(getItem(getId(position)).filePath),
					holder.iv_content);
		}
		return convertView;
	}

	private String getMediaText() {
		if (MediaType == MediaConstants.TYPE_PIC) {
			return context.getString(R.string.zg_content_media_take_pic);
		} else {
			return context.getString(R.string.zg_content_media_take_video);
		}
	}

	public static class ViewHolder {
		TextView tv_camera;
		ImageView iv_content;
		public ImageView iv_select;
	}

	private int getThumbWidth() {
		DisplayMetrics metric = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay()
				.getMetrics(metric);
		int padding = context.getResources().getDimensionPixelSize(
				R.dimen.zg_grid_horizontal_spacing_s);
		return (metric.widthPixels - 3 * padding) / 4;
	}
}
