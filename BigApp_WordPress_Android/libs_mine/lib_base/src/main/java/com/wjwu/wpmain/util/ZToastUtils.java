package com.wjwu.wpmain.util;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.wjwu.wpmain.lib_base.R;

/**
 * Created by wjwu on 2015/8/28.
 */
public class ZToastUtils {

	private static Toast sToast;

	private static Toast getToast(Context context) {
		return Toast.makeText(context, "", Toast.LENGTH_SHORT);
	}

	/***
	 * 显示toast内容
	 * 
	 * @param context
	 * @param message
	 */
	public static void toastMessage(Context context, String message) {
		if (context == null) {
			return;
		}
		if (TextUtils.isEmpty(message)) {
			sToast = null;
			return;
		}
		if (sToast != null) {
			sToast.cancel();
			sToast = null;
		}
		sToast = getToast(context);
		sToast.setDuration(Toast.LENGTH_SHORT);
		sToast.setText(message);
		sToast.show();
	}

	/***
	 * 显示toast内容
	 * 
	 * @param context
	 * @param message
	 */
	public static void toastMessageLong(Context context, String message) {
		if (context == null) {
			return;
		}
		if (TextUtils.isEmpty(message)) {
			sToast = null;
			return;
		}
		if (sToast != null) {
			sToast.cancel();
			sToast = null;
		}
		sToast = getToast(context);
		sToast.setDuration(Toast.LENGTH_LONG);
		sToast.setText(message);
		sToast.show();
	}

	/***
	 * 根据资源文件显示toast内容
	 * 
	 * @param context
	 * @param resId
	 */
	public static void toastMessage(Context context, int resId) {
		if (context == null) {
			return;
		}
		if (sToast != null) {
			sToast.cancel();
			sToast = null;
		}
		sToast = getToast(context);
		sToast.setDuration(Toast.LENGTH_SHORT);
		sToast.setText(resId);
		sToast.show();
	}

	/***
	 * 加载失败
	 * 
	 * @param context
	 */
	public static void toastLoadingFail(Context context) {
		if (context != null) {
			toastMessage(context, R.string.z_toast_loading_fail);
		}
	}
	
	/***
	 * 网络不可用
	 * 
	 * @param context
	 */
	public static void toastNoNetWork(Context context) {
		if (context != null) {
			toastMessage(context, R.string.z_toast_error_no_net);
		}
	}

	/***
	 * 连接超时
	 * 
	 * @param context
	 */
	public static void toastTimeout(Context context) {
		if (context != null) {
			toastMessage(context, R.string.z_toast_error_timeout);
		}
	}

	/***
	 * 未连接
	 * 
	 * @param context
	 */
	public static void toastUnunited(Context context) {
		if (context != null) {
			toastMessage(context, R.string.z_toast_error_ununited);
		}
	}

	/***
	 * 数据解析错误
	 * 
	 * @param context
	 */
	public static void toastDataError(Context context) {
		if (context != null) {
			toastMessage(context, R.string.z_toast_error_dataError);
		}
	}

	/***
	 * 服务器内部错误
	 * 
	 * @param context
	 */
	public static void toastServerError(Context context) {
		if (context != null) {
			toastMessage(context, R.string.z_toast_error_server_error);
		}
	}

	/***
	 * 请求失败
	 *
	 * @param context
	 */
	public static void toastRequestError(Context context) {
		if (context != null) {
			toastMessage(context, R.string.z_toast_error_request);
		}
	}

	/***
	 * 未知错误
	 *
	 * @param context
	 */
	public static void toastUnknownError(Context context) {
		if (context != null) {
			toastMessage(context, R.string.z_toast_error_unknown);
		}
	}

	/***
	 * 获取本地缓存失败
	 *
	 * @param context
	 */
	public static void toastCacheError(Context context) {
		if (context != null) {
			toastMessage(context, R.string.z_toast_error_cache);
		}
	}

	/***
	 * 再按一次退出
	 * 
	 * @param context
	 */
	public static void toastExitBefore(Context context) {
		if (context != null) {
			toastMessage(context, R.string.z_toast_exit_app);
		}
	}
}
