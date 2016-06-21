package com.wjwu.wpmain.util;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;

public class IntentUtils {
    public static final int REQUESTCODE_TAKEPIC = 0x1F01;
    public static final int REQUESTCODE_PICKPIC = 0x1F02;
    public static final int REQUESTCODE_TAKEANDPICKPIC = 0x1F03;

    /***
     * 拍照
     *
     * @param fragment
     * @param requestCode
     */
    public static void takePicForResult(Fragment fragment, int requestCode) {
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Intent chooserIntent = Intent.createChooser(takePhotoIntent, "拍照");
        fragment.startActivityForResult(chooserIntent, requestCode);
    }

    /***
     * 选择图片
     *
     * @param fragment
     * @param requestCode
     */
    public static void pickPicForResult(Fragment fragment, int requestCode) {
        Intent pickIntent = new Intent(Intent.ACTION_GET_CONTENT);
        pickIntent.setType("image/*");
        Intent chooserIntent = Intent.createChooser(pickIntent, "选择图片");
        fragment.startActivityForResult(chooserIntent, requestCode);
    }

    /***
     * 拍照或者选择图片
     *
     * @param fragment
     * @param requestCode
     */
    public static void takeAndPickPicForResult(Fragment fragment,
                                               int requestCode) {
        Intent pickIntent = new Intent(Intent.ACTION_GET_CONTENT);
        pickIntent.setType("image/*");
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Intent chooserIntent = Intent.createChooser(pickIntent, "拍照或者选择图片");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
                new Intent[]{takePhotoIntent});
        fragment.startActivityForResult(chooserIntent, requestCode);
    }

    /***
     * 发送邮件
     *
     */
//	public static void launchEmailToIntent(Context context) {
//		Intent msg = new Intent(Intent.ACTION_SEND);
//		StringBuilder body = new StringBuilder("\n\n----------\n");
//		body.append(EnvironmentInfoUtil.getApplicationInfo(context));
//		msg.putExtra(Intent.EXTRA_EMAIL,
//				context.getString(R.string.z_content_feedback_email)
//						.split(", "));
//		msg.putExtra(Intent.EXTRA_SUBJECT,
//				context.getString(R.string.z_content_feedback_subject));
//		msg.putExtra(Intent.EXTRA_TEXT, body.toString());
//
//		msg.setType("message/rfc822");
//		context.startActivity(Intent.createChooser(msg,
//				context.getString(R.string.z_content_feedback_title)));
//	}

    /**
     * 打开网络设置界面
     */
    public static void openSetting(Activity activity) {
        Intent intent = new Intent("/");
        ComponentName cm = new ComponentName("com.android.settings",
                "com.android.settings.WirelessSettings");
        intent.setComponent(cm);
        intent.setAction("android.intent.action.VIEW");
        activity.startActivityForResult(intent, 0);
    }
}
