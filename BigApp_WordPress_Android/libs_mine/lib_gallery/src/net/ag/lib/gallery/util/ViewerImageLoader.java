package net.ag.lib.gallery.util;

import net.ag.lib.gallery.R;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.wjwu.wpmain.lib_base.BaseApplication;

/***
 * 图片加载工具
 * 
 * @author AG
 *
 */
public class ViewerImageLoader {

	/***
	 * 缓存内存和硬盘
	 */
	private DisplayImageOptions options;
	/***
	 * 仅缓存内存
	 */
	private DisplayImageOptions options_memory;
	/***
	 * 仅缓存磁盘
	 */
	private DisplayImageOptions options_disk;
	/***
	 * 都不缓存
	 */
	private DisplayImageOptions options_none;
	private ImageLoader imageLoader;

	private static ViewerImageLoader sViewerImageLoader;

	private ViewerImageLoader() {
	}

	public static ViewerImageLoader getInstance() {
		if (sViewerImageLoader == null) {
			sViewerImageLoader = new ViewerImageLoader();
			sViewerImageLoader.imageLoader = ImageLoader.getInstance();
		}
		return sViewerImageLoader;
	}

	public void displayImage(String imagePath, ImageView imageView,
			ImageLoadingListener listener) {
		if (options_none == null) {
			this.options_none = getOptions().cacheInMemory(false)
					.cacheOnDisk(false).build();
		}
		imageLoader.displayImage(imagePath, imageView, options_none, listener);
	}

	public void displayImageWp(String imagePath, ImageView imageView,
			ImageLoadingListener listener) {
		if (options_none == null) {
			this.options_none = getOptionsWp().cacheInMemory(false)
					.cacheOnDisk(false).build();
		}
		imageLoader.displayImage(imagePath, imageView, options_none, listener);
	}

	public void displayImage(String imagePath, ImageView imageView) {
		if (options_none == null) {
			this.options_none = getOptions().cacheInMemory(false)
					.cacheOnDisk(false).build();
		}
		imageLoader.displayImage(imagePath, imageView, options_none);
	}

	public void displayImageAndCached(String imagePath, ImageView imageView) {
		if (options == null) {
			this.options = getOptions().cacheInMemory(true).cacheOnDisk(true)
					.build();
		}
		imageLoader.displayImage(imagePath, imageView, options);
	}

	public void displayImageAndCachedMemory(String imagePath,
			ImageView imageView) {
		if (options_memory == null) {
			this.options_memory = getOptions().cacheInMemory(true)
					.cacheOnDisk(false).build();
		}
		imageLoader.displayImage(imagePath, imageView, options_memory);
	}

	public void displayImageAndCachedDisk(String imagePath,
			ImageView imageView) {
		if (options_disk == null) {
			this.options_disk = getOptions().cacheInMemory(false)
					.cacheOnDisk(true).build();
		}
		imageLoader.displayImage(imagePath, imageView, options_disk);
	}

	private DisplayImageOptions.Builder getOptions() {
		return new DisplayImageOptions.Builder()
				.showImageForEmptyUri(R.drawable.zg_default_pic_beizi_64)
				.showImageOnFail(R.drawable.zg_default_pic_beizi_64)
				.showImageOnLoading(R.drawable.zg_default_pic_beizi_64)
				.imageScaleType(ImageScaleType.IN_SAMPLE_INT)
				.bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true)
				.displayer(new FadeInBitmapDisplayer(300));
	}

	private DisplayImageOptions.Builder getOptionsWp() {
		return new DisplayImageOptions.Builder()
				.showImageForEmptyUri(BaseApplication.sDefaultImageDrawable)
				.showImageOnFail(BaseApplication.sDefaultImageDrawable)
				.showImageOnLoading(BaseApplication.sDefaultImageDrawable)
				.imageScaleType(ImageScaleType.IN_SAMPLE_INT)
				.bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true)
				.displayer(new FadeInBitmapDisplayer(300));
	}
}
