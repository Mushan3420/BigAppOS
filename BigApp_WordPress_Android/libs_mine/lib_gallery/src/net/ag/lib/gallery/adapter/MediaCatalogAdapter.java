package net.ag.lib.gallery.adapter;

import java.io.File;
import java.util.List;

import net.ag.lib.gallery.R;
import net.ag.lib.gallery.util.MediaConstants;
import net.ag.lib.gallery.util.MediaInfo;
import net.ag.lib.gallery.util.ViewerImageLoader;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.download.ImageDownloader.Scheme;

/***
 * 媒体目录适配器
 * 
 * @author AG
 *
 */
public class MediaCatalogAdapter extends BaseAdapter {

	private List<MediaInfo> files;
	private LayoutInflater inflate;
	private ViewerImageLoader mImageLoader;
	private int mMediaType;
	private Context context;
	private int select = 0;

	public MediaCatalogAdapter(Context context, int mediaType) {
		this.context = context;
		this.mMediaType = mediaType;
		this.inflate = LayoutInflater.from(context);
		this.mImageLoader = ViewerImageLoader.getInstance();
	}

	public int getSelect() {
		return select;
	}

	public void setSelect(int select) {
		this.select = select;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		if (files != null) {
			return files.size();
		}
		return 0;
	}

	public void setFiles(List<MediaInfo> files) {
		this.files = files;
		notifyDataSetChanged();
	}

	@Override
	public MediaInfo getItem(int position) {
		if (position < getCount()) {
			return files.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflate.inflate(R.layout.zg_adapter_item_catalog,
					null);
			holder.tv_catalog = (TextView) convertView
					.findViewById(R.id.tv_catalog);
			holder.iv_preview = (ImageView) convertView
					.findViewById(R.id.iv_preview);
			holder.iv_select = (ImageView) convertView
					.findViewById(R.id.iv_select);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		MediaInfo file = getItem(position);
		if (file != null && !TextUtils.isEmpty(file.filePath)) {
			if (position == 0) {
				holder.tv_catalog.setText(getMediaText(mMediaType, context));
			} else {
				holder.tv_catalog.setText(new File(file.filePath)
						.getParentFile().getName());
			}

			if (position == select) {
				holder.iv_select.setVisibility(View.VISIBLE);
			} else {
				holder.iv_select.setVisibility(View.GONE);
			}
			mImageLoader.displayImageAndCachedMemory(
					Scheme.FILE.wrap(file.filePath), holder.iv_preview);
		}
		return convertView;
	}

	public static String getMediaText(int MediaType, Context context) {
		if (MediaType == MediaConstants.TYPE_PIC) {
			return context.getString(R.string.zg_content_media_all_pic);
		} else {
			return context.getString(R.string.zg_content_media_all_video);
		}
	}

	static class ViewHolder {
		TextView tv_catalog;
		ImageView iv_preview;
		ImageView iv_select;
	}

}
