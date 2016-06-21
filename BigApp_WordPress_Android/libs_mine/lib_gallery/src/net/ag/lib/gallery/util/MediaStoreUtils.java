package net.ag.lib.gallery.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import com.nostra13.universalimageloader.utils.IoUtils;
import com.nostra13.universalimageloader.utils.StorageUtils;

/***
 * 媒体扫描工具
 * 
 * @author AG
 *
 */
public class MediaStoreUtils {

	private final static String[] videoColumns = new String[] {
			MediaStore.Video.VideoColumns.DATA,
			MediaStore.Video.VideoColumns._ID,
			MediaStore.Video.VideoColumns.TITLE,
			MediaStore.Video.VideoColumns.MIME_TYPE,
			MediaStore.Video.VideoColumns.DURATION,
			MediaStore.Video.VideoColumns.SIZE };

	private final static String[] imageColumns = {
			MediaStore.Images.ImageColumns.DATA,
			MediaStore.Images.ImageColumns._ID,
			MediaStore.Images.ImageColumns.TITLE,
			MediaStore.Images.ImageColumns.MIME_TYPE,
			MediaStore.Images.ImageColumns.SIZE };

	@SuppressLint("InlinedApi")
	private final static String[] fileColumns = {
			MediaStore.Files.FileColumns.DATA,
			MediaStore.Files.FileColumns.PARENT,
			MediaStore.Files.FileColumns.TITLE,
			MediaStore.Files.FileColumns.MIME_TYPE,
			MediaStore.Files.FileColumns.SIZE };

	/***
	 * 获取视频信息
	 * 
	 * @param cr
	 * @return
	 */
	public static ArrayList<MediaInfo> getVideoInfo(ContentResolver cr) {
		ArrayList<MediaInfo> mediaList = new ArrayList<MediaInfo>();
		String orderBy = MediaStore.Video.VideoColumns._ID + " DESC";
		Cursor cursor = cr.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
				videoColumns, null, null, orderBy);
		if (cursor == null) {
			return mediaList;
		}
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			MediaInfo info = new MediaInfo();
			try {
				int pIndex = cursor.getColumnIndexOrThrow(videoColumns[0]);
				info.filePath = cursor.getString(pIndex);
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				int iIndex = cursor.getColumnIndexOrThrow(videoColumns[1]);
				info.id = cursor.getInt(iIndex);
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				int tIndex = cursor.getColumnIndexOrThrow(videoColumns[2]);
				info.title = cursor.getString(tIndex);
			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				int mIndex = cursor.getColumnIndexOrThrow(videoColumns[3]);
				info.mimeType = cursor.getString(mIndex);
			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				int dIndex = cursor.getColumnIndexOrThrow(videoColumns[4]);
				info.duration = cursor.getInt(dIndex);
			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				int sizeIndex = cursor.getColumnIndexOrThrow(videoColumns[5]);
				info.size = cursor.getInt(sizeIndex);
			} catch (Exception e) {
				e.printStackTrace();
			}

			mediaList.add(info);
		}
		cursor.close();
		return mediaList;
	}

	/***
	 * 获取图片信息
	 * 
	 * @param cr
	 * @return
	 */
	public static ArrayList<MediaInfo> getImageInfo(ContentResolver cr) {
		ArrayList<MediaInfo> mediaList = new ArrayList<MediaInfo>();
		String orderBy = MediaStore.Images.ImageColumns._ID + " DESC";
		Cursor cursor = MediaStore.Images.Media.query(cr,
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageColumns,
				null, orderBy);
		if (cursor == null) {
			return mediaList;
		}
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			MediaInfo info = new MediaInfo();
			try {
				int pIndex = cursor.getColumnIndexOrThrow(imageColumns[0]);
				info.filePath = cursor.getString(pIndex);
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				int iIndex = cursor.getColumnIndexOrThrow(imageColumns[1]);
				info.id = cursor.getInt(iIndex);
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				int tIndex = cursor.getColumnIndexOrThrow(imageColumns[2]);
				info.title = cursor.getString(tIndex);
			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				int mIndex = cursor.getColumnIndexOrThrow(imageColumns[3]);
				info.mimeType = cursor.getString(mIndex);
			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				int sizeIndex = cursor.getColumnIndexOrThrow(imageColumns[4]);
				info.size = cursor.getInt(sizeIndex);
			} catch (Exception e) {
				e.printStackTrace();
			}
			mediaList.add(info);
		}
		cursor.close();
		return mediaList;
	}

	/***
	 * 获取媒体目录信息
	 * 
	 * @param cr
	 * @param type
	 * @return
	 */
	@SuppressLint("NewApi")
	public static ArrayList<MediaInfo> getMediaCatalogInfo(ContentResolver cr,
			int type) {
		ArrayList<MediaInfo> mediaList = new ArrayList<MediaInfo>();
		String mSelection;
		if (type == MediaConstants.TYPE_PIC) {
			mSelection = MediaStore.Files.FileColumns.MEDIA_TYPE + " = "
					+ MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE + " ) "
					+ " group by ( " + MediaStore.Files.FileColumns.PARENT;
		} else if (type == MediaConstants.TYPE_VIDEO) {
			mSelection = MediaStore.Files.FileColumns.MEDIA_TYPE + " = "
					+ MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO + " ) "
					+ " group by ( " + MediaStore.Files.FileColumns.PARENT;
		} else {
			return mediaList;
		}
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			return mediaList;
		}
		// getContentUri Call requires API level 11 (current min is 8)
		Cursor cursor = cr.query(MediaStore.Files.getContentUri("external"),
				null, mSelection, null, null);
		if (cursor == null) {
			return mediaList;
		}
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			MediaInfo info = new MediaInfo();
			try {
				int pIndex = cursor.getColumnIndexOrThrow(fileColumns[0]);
				info.filePath = cursor.getString(pIndex);
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				int iIndex = cursor.getColumnIndexOrThrow(fileColumns[1]);
				info.id = cursor.getInt(iIndex);
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				int tIndex = cursor.getColumnIndexOrThrow(fileColumns[2]);
				info.title = cursor.getString(tIndex);
			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				int mIndex = cursor.getColumnIndexOrThrow(fileColumns[3]);
				info.mimeType = cursor.getString(mIndex);
			} catch (Exception e) {
				e.printStackTrace();
			}
			mediaList.add(info);
		}
		cursor.close();
		return mediaList;
	}

	/**
	 * 获取视频的缩略图 先通过ThumbnailUtils来创建一个视频的缩略图，然后再利用ThumbnailUtils来生成指定大小的缩略图。
	 * 如果想要的缩略图的宽和高都小于MICRO_KIND，则类型要使用MICRO_KIND作为kind的值，这样会节省内存。
	 * 
	 * @param videoPath
	 *            视频的路径
	 * @param width
	 *            指定输出视频缩略图的宽度
	 * @param height
	 *            指定输出视频缩略图的高度度
	 * @param kind
	 *            参照MediaStore.Images.Thumbnails类中的常量MINI_KIND和MICRO_KIND。
	 *            其中，MINI_KIND: 512 x 384，MICRO_KIND: 96 x 96
	 * @return 指定大小的视频缩略图
	 */
	public static Bitmap getVideoThumbnail(String videoPath, int width,
			int height, int kind) {
		Bitmap bitmap = null;
		bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
		if (width > 0 && height > 0) {
			bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
					ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		}
		return bitmap;
	}

	public static String getPathByUri(ContentResolver cr, Uri uri) {
		String path = "";
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			try {
				path = getPathByDocument(cr, uri);
			} catch (Exception e) {
				path = getPathByURI(cr, uri);
			}
		} else {
			path = getPathByURI(cr, uri);
		}
		return path;
	};

	@SuppressLint("NewApi")
	private static String getPathByDocument(ContentResolver cr, Uri uri)
			throws Exception {
		String path = "";
		String wholeID = DocumentsContract.getDocumentId(uri);
		String id = wholeID.split(":")[1];
		String sel = MediaStore.Images.Media._ID + "=?";
		Cursor cursor = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
				imageColumns, sel, new String[] { id }, null);
		cursor.moveToFirst();
		int columnIndex = cursor.getColumnIndex(imageColumns[0]);
		path = cursor.getString(columnIndex);
		cursor.close();
		return path;
	}

	private static String getPathByURI(ContentResolver cr, Uri uri) {
		String path = "";
		Cursor cursor = cr.query(uri, imageColumns, null, null, null);
		cursor.moveToFirst();
		int columnIndex = cursor.getColumnIndex(imageColumns[0]);
		path = cursor.getString(columnIndex);
		cursor.close();
		return path;
	}

	private static MediaScannerConnection mConnection;

	public static void addImage2Gallery(Context context,
			BaseMediaScannerListener l) {
		l.setMimeType("image/jpg");
		mConnection = new MediaScannerConnection(context, l);
		mConnection.connect();
	}

	public static void addVideo2Gallery(Context context,
			BaseMediaScannerListener l) {
		l.setMimeType("video/mp4");
		mConnection = new MediaScannerConnection(context, l);
		mConnection.connect();
	}

	public static class BaseMediaScannerListener extends MediaScannerListener {
		public BaseMediaScannerListener(String path) {
			super(path);
		}

		public BaseMediaScannerListener(String path, String mimeType) {
			super(path);
			setMimeType(mimeType);
		}
	}

	private static class MediaScannerListener implements
			MediaScannerConnectionClient {
		String path;
		String mimeType;

		public MediaScannerListener(String path) {
			this.path = path;
		}

		public String getMimeType() {
			return mimeType;
		}

		public void setMimeType(String mimeType) {
			this.mimeType = mimeType;
		}

		@Override
		public void onMediaScannerConnected() {
			if (mConnection != null) {
				mConnection.scanFile(path, mimeType);
			}
		}

		@Override
		public void onScanCompleted(String path, Uri uri) {
			mConnection.disconnect();
		}
	}

	public static File getTempCacheDir(Context context) {
		File temp = new File(StorageUtils.getCacheDirectory(context, true),
				"temp");
		if (!temp.exists()) {
			temp.mkdirs();
		}
		return temp;
	}

	public static File getOwnImageCacheDir(Context context) {
		File imageCache = StorageUtils.getOwnCacheDirectory(context,
				"AG/images");
		if (!imageCache.exists()) {
			imageCache.mkdirs();
		}
		return imageCache;
	}

	public static File getOwnAudioCacheDir(Context context) {
		File imageCache = StorageUtils.getOwnCacheDirectory(context,
				"AG/audios");
		if (!imageCache.exists()) {
			imageCache.mkdirs();
		}
		return imageCache;
	}

	/**
	 * 复制单个文件
	 * 
	 * @param oldPath
	 *            String
	 * @param newPath
	 *            String
	 * @return boolean
	 */
	public static boolean copyFile(String oldPath, String newPath) {
		if (!new File(oldPath).exists())
			return false;
		boolean r = false;
		int bytesum = 0;
		int byteread = 0;
		FileInputStream fi = null;
		FileOutputStream fo = null;
		try {
			fi = new FileInputStream(oldPath);
			fo = new FileOutputStream(newPath);
			byte[] buffer = new byte[1024];
			while ((byteread = fi.read(buffer)) != -1) {
				bytesum += byteread; // 字节数 文件大小
				fo.write(buffer, 0, byteread);
			}
			fo.flush();
			r = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		IoUtils.closeSilently(fi);
		IoUtils.closeSilently(fo);
		return r;
	}

}
