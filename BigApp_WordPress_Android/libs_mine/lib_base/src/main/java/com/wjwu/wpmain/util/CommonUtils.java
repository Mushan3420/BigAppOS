package com.wjwu.wpmain.util;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.Uri;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.download.ImageDownloader;
import com.nostra13.universalimageloader.utils.IoUtils;
import com.wjwu.wpmain.lib_base.BaseApplication;

import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.Topic;

/**
 * Created by wjwu on 2015/8/28.
 */
public class CommonUtils {

    /***
     * 检查网络
     *
     * @param context Context
     * @return true or false
     */
    public static boolean checkNetworkEnable(Context context) {
        return checkNetworkEnable(context, true);
    }

    /***
     * 检查网络
     *
     * @param context Context
     * @param toast   是否需要toast提示
     * @return true or false
     */
    public static boolean checkNetworkEnable(Context context, boolean toast) {
        if (context == null) {
            return false;
        }
        ConnectivityManager manager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info != null && info.isConnected()
                && info.getState() == State.CONNECTED) {
            return true;
        }
        if (toast) {
            ZToastUtils.toastNoNetWork(context);
        }
        return false;
    }

    /**
     * @param context Context
     * @return 1-wifi, 2-3G, 0-无网络连接
     */
    public static int getNetworkType(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        State mobileState = connectivityManager.getNetworkInfo(
                ConnectivityManager.TYPE_MOBILE).getState();
        State wifiState = connectivityManager.getNetworkInfo(
                ConnectivityManager.TYPE_WIFI).getState();
        if (wifiState == State.CONNECTED || wifiState == State.CONNECTING) {
            return 1;
        } else if (mobileState == State.CONNECTED
                || mobileState == State.CONNECTING) {
            return 2;
        } else {
            return 0;
        }
    }

    public static int getConnectTimeOutTime() {
        try {
            int type = getNetworkType(BaseApplication.getInstance().getApplicationContext());
            if (type == 2) {
                return 5000;
            }
        } catch (Exception e) {
            ZLogUtils.logException(e);
        }
        return 2500;
    }


    /***
     * 隐藏输入法键盘
     *
     * @param context Context
     * @param views   views
     */
    public static void hideSoftKeyBoard(Context context, final View... views) {
        try {
            final InputMethodManager imm = (InputMethodManager) context
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (views != null) {
                            for (View view : views) {
                                if (view != null) {
                                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                                }
                            }
                        }
                    } catch (Exception e) {
                    }
                }
            }, 200);
        } catch (Exception e) {
        }
    }

    /***
     * 打开输入法键盘
     *
     * @param context Context
     * @param views   views
     */
    public static void showSoftKeyBoard(Context context, View... views) {
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (views != null) {
            for (View view : views) {
                if (view != null) {
                    imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
                }
            }
        }
    }

    /***
     * 隐藏输入法键盘
     *
     * @param imm   InputMethodManager
     * @param views views
     */
    public static void hideSoftKeyBoard(InputMethodManager imm, View... views) {
        if (views != null && imm != null) {
            for (View view : views) {
                if (view != null) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        }
    }

    /***
     * 打开输入法键盘
     *
     * @param imm   InputMethodManager
     * @param views views
     */
    public static void showSoftKeyBoard(InputMethodManager imm, View... views) {
        if (views != null && imm != null) {
            for (View view : views) {
                if (view != null) {
                    imm.showSoftInput(view, 0);
                }
            }
        }
    }

    /***
     * 验证手机号
     *
     * @param phoneNumber phone
     * @return true or false
     */
    public static boolean isPhoneNumberValid(String phoneNumber) {
        boolean isValid = false;
        String expression = "((^(1|2)[0-9][0-9]{9}$)|(^0[1,2]{1}\\d{1}-?\\d{8}$)|(^0[3-9] {1}\\d{2}-?\\d{7,8}$)|(^0[1,2]{1}\\d{1}-?\\d{8}-(\\d{1,4})$)|(^0[3-9]{1}\\d{2}-? \\d{7,8}-(\\d{1,4})$))";
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(phoneNumber);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    /***
     * 跳到拨号界面
     *
     * @param context     Context
     * @param phoneNumber phone
     */
    public static void gotoCall(Context context, String phoneNumber) {
        try {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel://"
                    + phoneNumber));
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * 直接拨打电话
     *
     * @param context     Context
     * @param phoneNumber phone
     */
    public static void makeCall(Context context, String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel://"
                + phoneNumber));
        context.startActivity(intent);
    }

    public static String getUUID() {
        return UUID.randomUUID().toString();
    }

    public static String getTopActivity(Context context) {
        ActivityManager manager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);

        if (runningTaskInfos != null)
            return runningTaskInfos.get(0).topActivity.getClassName();
        else
            return "";
    }

    public static void pauseMusic(Context context) {
        Intent freshIntent = new Intent();
        freshIntent.setAction("com.android.music.musicservicecommand.pause");
        freshIntent.putExtra("command", "pause");
        context.sendBroadcast(freshIntent);
    }

    public static boolean isFastMobileNetwork(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        // System.out.println("request_http netType networkType = "
        // + telephonyManager.getNetworkType());
        switch (telephonyManager.getNetworkType()) {
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                return false; // ~ 50-100 kbps
            case TelephonyManager.NETWORK_TYPE_CDMA:
                return false; // ~ 14-64 kbps
            case TelephonyManager.NETWORK_TYPE_EDGE:
                return false; // ~ 50-100 kbps
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                return true; // ~ 400-1000 kbps
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                return true; // ~ 600-1400 kbps
            case TelephonyManager.NETWORK_TYPE_GPRS:
                return false; // ~ 100 kbps
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                return true; // ~ 2-14 Mbps
            case TelephonyManager.NETWORK_TYPE_HSPA:
                return true; // ~ 700-1700 kbps
            case TelephonyManager.NETWORK_TYPE_HSUPA:
                return true; // ~ 1-23 Mbps
            case TelephonyManager.NETWORK_TYPE_UMTS:
                return true; // ~ 400-7000 kbps
            case TelephonyManager.NETWORK_TYPE_EHRPD:
                return true; // ~ 1-2 Mbps
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
                return true; // ~ 5 Mbps
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return true; // ~ 10-20 Mbps
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return false; // ~25 kbps
            case TelephonyManager.NETWORK_TYPE_LTE:
                return true; // ~ 10+ Mbps
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                return false;
            default:
                return false;
        }
    }

    /**
     * 获取应用程序名称
     */
    public static String getAppName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            return context.getResources().getString(labelRes);
        } catch (PackageManager.NameNotFoundException e) {
        }
        return null;
    }

    /**
     * [获取应用程序版本名称信息]
     *
     * @param context
     * @return 当前应用的版本名称
     */
    public static String getVersionName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
        }
        return null;
    }

    /**
     * [获取应用程序版本名称信息]
     *
     * @param context
     * @return 当前应用的版本名称
     */
    public static int getVersionCode(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
        }
        return 0;
    }

    public static String gennerateGmtTime() {
        try {
            SimpleDateFormat format_old = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            format_old.setTimeZone(TimeZone.getTimeZone("GMT"));
            return format_old.format(new Date());
        } catch (Exception e) {
            ZLogUtils.logException(e);
        }
        return new SimpleDateFormat("MM-dd").format(new Date());
    }

    public static String getOnlyDateFromGmt(String modifiedTime) {
        return getFormatDate(modifiedTime, "yyyy-MM-dd'T'HH:mm:ss", "GMT", "MM-dd");
    }

    public static String getOnlyDateFromGmt(Topic item) {
        if (item == null) {
            return "";
        }
        return getFormatDate(item.date_gmt, "yyyy-MM-dd'T'HH:mm:ss", "GMT", "MM-dd");
    }

    public static String getYearDateFromGmt(Topic item) {
        if (item == null) {
            return "";
        }
        return getFormatDate(item.date_gmt, "yyyy-MM-dd'T'HH:mm:ss", "GMT", "yyyy-MM-dd");
    }

    public static String getYearDateFromGmt(String modifiedTime) {
        return getFormatDate(modifiedTime, "yyyy-MM-dd'T'HH:mm:ss", "GMT", "yyyy-MM-dd");
    }

    public static String getSecondFromGmt(String modifiedTime) {
        return getFormatDate(modifiedTime, "yyyy-MM-dd'T'HH:mm:ss", "GMT", "yyyy-MM-dd HH:mm:ss");
    }

    public static String getMuniteFromGmt(String modifiedTime) {
        return getFormatDate(modifiedTime, "yyyy-MM-dd'T'HH:mm:ss", "GMT", "yyyy-MM-dd HH:mm");
    }

    public static String getMuniteMonthFromGmt(String modifiedTime) {
        return getFormatDate(modifiedTime, "yyyy-MM-dd'T'HH:mm:ss", "GMT", "MM-dd HH:mm");
    }

    /***
     * yyyy-MM-dd'T'HH:mm:ss.SSSZ
     * return yyyy-MM-dd
     */
    public static String getFormatDateFromModifiedTime(String modifiedTime, String modifiedTZ) {
        return getFormatDate(modifiedTime, "yyyy-MM-dd'T'HH:mm:ss", modifiedTZ, "yyyy-MM-dd HH:mm:ss");
    }

    public static String getFormatDateFromModifiedTimeGmt(String modifiedTime) {
        return getFormatDate(modifiedTime, "yyyy-MM-dd'T'HH:mm:ss", "GMT", "yyyy-MM-dd HH:mm:ss");
    }


    private static String getFormatDate(String oldDateString, String oldFormat, String oldTimeZone, String newFormat) {
        try {
            SimpleDateFormat format_old = new SimpleDateFormat(oldFormat);
            format_old.setTimeZone(TimeZone.getTimeZone(oldTimeZone));
            Date date = format_old.parse(oldDateString);
            //转化成系统默认的时区时间
            SimpleDateFormat format_new = new SimpleDateFormat(newFormat);
            String result = format_new.format(date);
            return result;
        } catch (ParseException e) {
            ZLogUtils.logException(e);
        }
        return oldDateString;
    }

    public static boolean checkEmailEnable(String email) {
        return Pattern.compile("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$").matcher(email).matches();
    }

    public static String toURLEncoded(String paramString) {
        if (paramString == null || paramString.equals("")) {
            return paramString;
        }
        try {
            String str = new String(paramString.getBytes(), "UTF-8");
            str = URLEncoder.encode(str, "UTF-8");
            return str;
        } catch (Exception localException) {
        }

        return paramString;
    }

    public static byte[] getImageByteByPath(String imagePath, ImageSize imageSize) {
        try {
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .cacheInMemory(false).cacheOnDisk(false)
                    .considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565)
                    .imageScaleType(ImageScaleType.EXACTLY).build();
            Bitmap upbitmap = ImageLoader.getInstance().loadImageSync(ImageDownloader.Scheme.FILE.wrap(imagePath), imageSize, options);
            if (upbitmap == null) {
                return null;
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            upbitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            int option = 100;
            while (baos.toByteArray().length / 1024 > 120) {
                baos.reset();
                option -= 10;
                upbitmap.compress(Bitmap.CompressFormat.JPEG, option, baos);
            }
            byte[] result = baos.toByteArray();
            IoUtils.closeSilently(baos);
            upbitmap.recycle();
            return result;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] getImageByteByBitmap(Bitmap upbitmap) {
        try {
            if (upbitmap == null) {
                return null;
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            upbitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            int option = 100;
            while (baos.toByteArray().length / 1024 > 120) {
                baos.reset();
                option -= 10;
                upbitmap.compress(Bitmap.CompressFormat.JPEG, option, baos);
            }
            byte[] result = baos.toByteArray();
            IoUtils.closeSilently(baos);
            upbitmap.recycle();
            return result;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

//
//    当然还有就是可以指定时区的时间(待):
//    复制代码 代码如下:
//
//    df=DateFormat.getDateTimeInstance(DateFormat.FULL,DateFormat.FULL,Locale.CHINA);
//    System.out.println(df.format(new Date()));
}
