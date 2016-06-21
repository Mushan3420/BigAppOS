package com.wjwu.wpmain.util;

import android.content.Context;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

/***
 * 屏幕控制
 * 推荐使用下面的 FLAG_KEEP_SCREEN_ON
 * @author ag
 * 
 */
public class ZScreenControl {

	private WakeLock mWakeLock;

	@SuppressWarnings("deprecation")
	public ZScreenControl(Context context) {
		try {
			PowerManager pManager = ((PowerManager) context
					.getSystemService(Context.POWER_SERVICE));
			mWakeLock = pManager.newWakeLock(
					PowerManager.SCREEN_BRIGHT_WAKE_LOCK
							| PowerManager.ON_AFTER_RELEASE, getClass()
							.getCanonicalName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/***
	 * 禁止屏灭
	 */
	public void notAllowDim() {
		if (mWakeLock != null) {
			mWakeLock.acquire();
		}
	}

	/***
	 * 允许屏灭
	 */
	public void allowDim() {
		if (mWakeLock != null && mWakeLock.isHeld()) {
			mWakeLock.release();
		}
	}

	// private void resetScreenOn() {
	// mHandler.removeMessages(CLEAR_SCREEN_DELAY);
	// getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	// }
	//
	// private void keepScreenOnAwhile() {
	// Log.d(TAG, TAG + "\t call keepScreenOnAwhile() !");
	// mHandler.removeMessages(CLEAR_SCREEN_DELAY);
	// getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	// mHandler.sendEmptyMessageDelayed(CLEAR_SCREEN_DELAY, SCREEN_DELAY);
	//
	// }
	//
	// private void keepScreenOn() {
	// Log.d(TAG, TAG + "\t call keepScreenOn() !");
	// mHandler.removeMessages(CLEAR_SCREEN_DELAY);
	// getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	// }
}
