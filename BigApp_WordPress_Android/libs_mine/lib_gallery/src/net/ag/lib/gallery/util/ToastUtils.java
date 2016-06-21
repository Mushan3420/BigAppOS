package net.ag.lib.gallery.util;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

/***
 * 错误指示类
 * 
 * @author AG
 * 
 */
public class ToastUtils {

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
}
