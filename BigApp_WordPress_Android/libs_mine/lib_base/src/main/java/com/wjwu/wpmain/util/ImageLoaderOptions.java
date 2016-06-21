package com.wjwu.wpmain.util;

import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedCircleBitmapDisplayer;
import com.wjwu.wpmain.lib_base.BaseApplication;
import com.wjwu.wpmain.lib_base.R;

public class ImageLoaderOptions {

    private static DisplayImageOptions.Builder getBaseOptions() {
        return new DisplayImageOptions.Builder()
                .showImageForEmptyUri(BaseApplication.sDefaultImageDrawable)
                .showImageOnFail(BaseApplication.sDefaultImageDrawable)
                .showImageOnLoading(BaseApplication.sDefaultImageDrawable)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true)
                .displayer(new FadeInBitmapDisplayer(300));
    }

    private static DisplayImageOptions.Builder getBaseOptionsUser() {
        return new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.z_iv_default_user)
                .showImageOnFail(R.drawable.z_iv_default_user)
                .showImageOnLoading(R.drawable.z_iv_default_user)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .displayer(new RoundedCircleBitmapDisplayer())
                .bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true);
    }

    public static DisplayImageOptions getOptionsCachedBoth() {
        return getBaseOptions()
                .cacheInMemory(true).cacheOnDisk(true)
                .resetViewBeforeLoading(true)
                .considerExifParams(true).build();
//                .setDiskCache(new UnlimitedDiskCache(
//                        StorageUtils.getCacheDirectory(context));
    }

    public static DisplayImageOptions getOptionsCachedDisk() {
        return getOptionsCachedDisk(false);
    }

    public static DisplayImageOptions getOptionsCachedDisk(boolean isUser) {
        DisplayImageOptions.Builder builder;
        if (isUser) {
            builder = getBaseOptionsUser()
                    .cacheInMemory(false).cacheOnDisk(true)
                    .resetViewBeforeLoading(true)
                    .considerExifParams(true);
        } else {
            builder = getBaseOptions()
                    .cacheInMemory(false).cacheOnDisk(true)
                    .resetViewBeforeLoading(true)
                    .considerExifParams(true);
        }
        return builder.build();
//                .setDiskCache(new UnlimitedDiskCache(
//                        StorageUtils.getCacheDirectory(context));
    }

//    private boolean loadFile(String avatar, ImageView imageView,
//                             ImageLoadingListener listener, DisplayImageOptions options) {
//        File file = null;
//        switch (Scheme.ofUri(avatar)) {
//            case FILE:
//                file = new File(Scheme.FILE.crop(avatar));
//                break;
//            default:
//                file = new File(StorageUtils.getCacheDirectory(context),
//                        new HashCodeFileNameGenerator().generate(avatar));
//                break;
//        }
//        if (file != null && file.exists()) {
//            String uri = Scheme.FILE.wrap(file.getAbsolutePath());
//            imageLoader.displayImage(uri, imageView, options, listener);
//            return true;
//        }
//        return false;
//    }
}
