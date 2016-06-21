package com.wjwu.wpmain.cache;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;

import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;

/**
 * Created by wjwu on 2015/8/28.
 */
public class SdCacheTools {

    public static final int MAX_CACHE_SIZE_VOLLEY = 5 * 1024 * 1024;

    public static File getTempCacheDir(Context context) {
        File temp = new File(StorageUtils.getCacheDirectory(context, true),
                "temp");
        if (!temp.exists()) {
            temp.mkdirs();
        }
        return temp;
    }

    public static File getOwnImageCacheDir(Context context) {
        File imageCache = StorageUtils.getCacheDirectory(context,
                "WP/images", true);
        if (!imageCache.exists()) {
            imageCache.mkdirs();
        }
        return imageCache;
    }

    public static File getOwnAudioCacheDir(Context context) {
        File imageCache = StorageUtils.getCacheDirectory(context,
                "WP/audios", true);
        if (!imageCache.exists()) {
            imageCache.mkdirs();
        }
        return imageCache;
    }

    public static File getOwnFileCacheDir(Context context) {
        File imageCache = StorageUtils.getCacheDirectory(context,
                "WP/files", true);
        if (!imageCache.exists()) {
            imageCache.mkdirs();
        }
        return imageCache;
    }

    public static File getOwnVolleyCacheDir(Context context) {
        File imageCache = StorageUtils.getCacheDirectory(context,
                "WP/volley", true);
        if (!imageCache.exists()) {
            imageCache.mkdirs();
        }
        return imageCache;
    }

    public static File getOwnLogCacheDir(Context context) {
        File imageCache = StorageUtils.getOwnCacheDirectory(context,
                "WP/logs");
        if (!imageCache.exists()) {
            imageCache.mkdirs();
        }
        return imageCache;
    }

    public static File getOwnInnerFileCacheDir(Context context) {
        File imageCache = StorageUtils.getCacheDirectory(context,
                "WP/files", false);
        if (!imageCache.exists()) {
            imageCache.mkdirs();
        }
        return imageCache;
    }

    /***
     * 获取可用空间
     *
     * @param external true外部，false内部
     */
    @SuppressWarnings("deprecation")
    public static long getAvailableSize(boolean external) {
        File path = null;
        if (external) {
            if (Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                path = Environment.getExternalStorageDirectory(); // 取得sdcard文件路径
            } else {
                return -1;
            }
        } else {
            path = Environment.getDataDirectory();
        }
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize; // 获取可用大小
    }
}
