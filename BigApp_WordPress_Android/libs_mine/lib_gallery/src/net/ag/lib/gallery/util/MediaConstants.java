package net.ag.lib.gallery.util;

/***
 * 常量
 * 
 * @author AG
 *
 */
public final class MediaConstants {

	public final static int MODE_MULTIPLE_PICK = 1;
	public final static int MODE_SINGLE_PICK = 2;

	public final static int MAX_MULTIPLE_PICK = 9;

	public final static int TYPE_PIC = 1;
	public final static int TYPE_VIDEO = 2;

	public final static int MEDIA_REQUEST_PIC_CODE = 0x1110;
	public final static int MEDIA_REQUEST_VIDEO_CODE = 0x1112;
	public final static int MEDIA_RESULT_PIC_CODE = 0x1113;
	public final static int MEDIA_RESULT_VIDEO_CODE = 0x1114;
	/***
	 * 去拍照界面
	 */
	public final static int MEDIA_REQUEST_CAMERA = 0x1115;
	public final static int MEDIA_REQUEST_GALLERY = 0x1116;
	/***
	 * 去拍照结束返回后的浏览界面
	 */
	public final static int MEDIA_REQUEST_CAMERA_PIC = 0x1117;

	public final static String MEDIA_MAX_MULTIPLE_PICK = "maxPick";
	public final static String MEDIA_PICK_MODE = "pickMode";
	public final static String MEDIA_RESULT_TYPE = "mediaType";
	// public final static String MEDIA_RESULT_DATA = "mediaData_res";
	public final static String MEDIA_RESULT_DATAS = "mediaData_res";
	public final static String MEDIA_REQUEST_DATAS = "mediaData_req";
	/***
	 * 浏览大图模式，一种是0浏览（可以分享）；一种是1预览（可以删除）
	 */
	public final static String MEDIA_PREVIEW_TYPE = "preview_type";
	public final static String MEDIA_POSITION = "position";

	public static int MAX_DURATION_MS = 5 * 60 * 1000;
	public static int MAX_SIZE_BYTE = 30 * 1024 * 1024;
}
