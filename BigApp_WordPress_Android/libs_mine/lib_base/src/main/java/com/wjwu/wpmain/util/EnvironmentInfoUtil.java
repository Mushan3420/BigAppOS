package com.wjwu.wpmain.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.telephony.TelephonyManager;

public class EnvironmentInfoUtil {

	/***
	 * 外部存储是否可以写
	 * 
	 * @return
	 */
	public static boolean canWriteInExternalStorage() {
		boolean mExternalStorageAvailable = false;
		boolean mExternalStorageWriteable = false;
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			// We can read and write the media
			mExternalStorageAvailable = mExternalStorageWriteable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			// We can only read the media
			mExternalStorageAvailable = true;
			mExternalStorageWriteable = false;
		} else {
			// Something else is wrong. It may be one of many other states,
			// but all we need to know is we can neither read nor write
			mExternalStorageAvailable = mExternalStorageWriteable = false;
		}
		return mExternalStorageAvailable && mExternalStorageWriteable;
	}

	public static String getApplicationInfo(Context context) {
		return String.format("%s\n%s\n%s\n%s\n%s\n%s\n", getCountry(context),
				getBrandInfo(), getModelInfo(), getDeviceInfo(),
				getVersionInfo(context), getLocale(context));
	}

	public static String getCountry(Context context) {
		TelephonyManager mTelephonyMgr = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return String.format("Country: %s",
				mTelephonyMgr.getNetworkCountryIso());
	}

	public static String getModelInfo() {
		return String.format("Model: %s", Build.MODEL);
	}

	public static String getBrandInfo() {
		return String.format("Brand: %s", Build.BRAND);
	}

	public static String getDeviceInfo() {
		return String.format("Device: %s", Build.DEVICE);
	}

	public static String getLocale(Context context) {
		return String.format("Locale: %s", context.getResources()
				.getConfiguration().locale.getDisplayName());
	}

	public static String getVersionInfo(Context context) {
		String version = null;
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			version = info.versionName + " (release " + info.versionCode + ")";
		} catch (NameNotFoundException e) {
			version = "not_found";
		}
		return String.format("Version: %s", version);
	}
}
