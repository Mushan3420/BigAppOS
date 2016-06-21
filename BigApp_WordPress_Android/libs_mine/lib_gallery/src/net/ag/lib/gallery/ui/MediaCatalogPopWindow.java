package net.ag.lib.gallery.ui;

import java.util.List;

import net.ag.lib.gallery.R;
import net.ag.lib.gallery.adapter.MediaCatalogAdapter;
import net.ag.lib.gallery.util.MediaInfo;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.PopupWindow;

/***
 * 媒体目录展示的window
 * 
 * @author AG
 *
 */
public class MediaCatalogPopWindow implements OnItemClickListener {
	private Context mContext;
	private List<MediaInfo> mCatalogs;
	private PopupWindow mPopupWindow;
	private int mMediaType;
	private MediaCatalogAdapter mAdapter;
	private AdapterView.OnItemClickListener mListener;

	public MediaCatalogPopWindow(Context context, List<MediaInfo> catalogs,
			int mediaType) {
		mContext = context;
		mCatalogs = catalogs;
		mMediaType = mediaType;
		init();
	}

	public OnItemClickListener getOnItemClickListener() {
		return mListener;
	}

	public void setOnItemClickListener(OnItemClickListener mListener) {
		this.mListener = mListener;
	}

	private void init() {
		View view = LayoutInflater.from(mContext).inflate(
				R.layout.zg_widget_pop_media_catalog, null);
		ListView listView = (ListView) view.findViewById(R.id.listview);
		mPopupWindow = new PopupWindow(view, LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		mAdapter = new MediaCatalogAdapter(mContext, mMediaType);
		mAdapter.setFiles(mCatalogs);
		listView.setAdapter(mAdapter);
		listView.setOnItemClickListener(this);
		mPopupWindow.setFocusable(true);
		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.setBackgroundDrawable(new ColorDrawable(0));
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		dismiss();
		if (mListener != null && mAdapter.getSelect() != position) {
			mAdapter.setSelect(position);
			mListener.onItemClick(parent, view, position, id);
		}
	}

	public void show(View view) {
		if (mPopupWindow != null) {
			mPopupWindow.setAnimationStyle(R.style.AnimUpDown);
			int y = mContext.getResources().getDimensionPixelSize(
					R.dimen.zg_bottom_bar_height);
			mPopupWindow.showAtLocation(view, Gravity.BOTTOM, 0, y);
		}
	}

	public boolean isShowing() {
		if (mPopupWindow != null) {
			return mPopupWindow.isShowing();
		}
		return false;
	}

	public void dismiss() {
		if (mPopupWindow != null) {
			mPopupWindow.dismiss();
		}
	}

}
