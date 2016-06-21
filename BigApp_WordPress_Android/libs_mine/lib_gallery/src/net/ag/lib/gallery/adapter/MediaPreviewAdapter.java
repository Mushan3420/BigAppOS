package net.ag.lib.gallery.adapter;

import java.util.List;

import net.ag.lib.gallery.ui.MediaDetailFragment;
import net.ag.lib.gallery.util.MediaInfo;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;

/***
 * 图片浏览适配器
 * 
 * @author macy
 *
 */
public class MediaPreviewAdapter extends FragmentStatePagerAdapter {
	private List<MediaInfo> selectList;

	public MediaPreviewAdapter(FragmentManager fm, List<MediaInfo> selectList) {
		super(fm);
		this.selectList = selectList;
	}

	@Override
	public Fragment getItem(int position) {
		return MediaDetailFragment.newInstance(selectList.get(position));
	}

	@Override
	public int getItemPosition(Object object) {
		return PagerAdapter.POSITION_NONE;
	}

	@Override
	public int getCount() {
		if (selectList != null) {
			return selectList.size();
		}
		return 0;
	}

}
