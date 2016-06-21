package com.wjwu.wpmain.share;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;

import com.joanzapata.android.BaseAdapterHelper;
import com.joanzapata.android.QuickAdapter;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.wjwu.wpmain.cache.SdCacheTools;
import com.wjwu.wpmain.util.ZToastUtils;
import com.wjwu.wpmain.uzwp.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;

import static com.mob.tools.utils.BitmapHelper.captureView;

public class DialogShare {
    private Dialog dialog;
    private Context mContext;

    private String title, url, desc, imageUrl;

    private PlatformActionListener platformActionListener;

    public DialogShare(Context context, String title, String url, String desc, String imageUrl, PlatformActionListener listener) {
        mContext = context;
        this.title = title;
        this.url = url;
        this.desc = desc;
        this.imageUrl = imageUrl;
        platformActionListener = listener;
        init(context);
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(
                R.layout.z_dialog_share, null);
        dialog = new Dialog(context, R.style.Dialog_General);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(view);

        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity = Gravity.BOTTOM;
        lp.width = LayoutParams.MATCH_PARENT;
        window.setWindowAnimations(R.style.AnimUpDown);

        GridView gridView = (GridView) view.findViewById(R.id.gridview);

        Resources resources = context.getResources();
        ArrayList<ShareItem> shareList = new ArrayList<>();
        if (resources.getString(R.string.use_wechat).equals("1")) {
            shareList.add(new ShareItem(R.drawable.z_wechat_share, R.string.z_share_wechat, "Wechat_p"));
            shareList.add(new ShareItem(R.drawable.z_wechat_pp, R.string.z_share_wechat_p, "Wechat_pp"));
        }
        if (resources.getString(R.string.use_sina).equals("1")) {
            shareList.add(new ShareItem(R.drawable.z_sina, R.string.z_share_sina, "Sina"));
        }
        if (resources.getString(R.string.use_qq).equals("1")) {
            shareList.add(new ShareItem(R.drawable.z_qq_share, R.string.z_share_qq, "qq_p"));
            shareList.add(new ShareItem(R.drawable.z_qq_k, R.string.z_share_qq_k, "qq_k"));
        }
        shareList.add(new ShareItem(R.drawable.z_link, R.string.z_share_link, "copy_k"));

        QuickAdapter adapter = new QuickAdapter<ShareItem>(context,
                R.layout.z_dialog_share_item, shareList) {
            @Override
            protected void convert(final BaseAdapterHelper helper, ShareItem item) {
                helper.setImageResource(R.id.iv_share, item.drawableId);
                helper.setText(R.id.tv_share, item.stringId);
            }
        };
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                cancel();
                ShareItem shareItem = (ShareItem) parent.getAdapter().getItem(position);
                switch (shareItem.drawableId) {
                    case R.drawable.z_wechat_share:
                        wechat();
                        break;
                    case R.drawable.z_wechat_pp:
                        wechatP();
                        break;
                    case R.drawable.z_sina:
                        sina();
                        break;
                    case R.drawable.z_qq_share:
                        qq();
                        break;
                    case R.drawable.z_qq_k:
                        qzone();
                        break;
                    case R.drawable.z_link:
                        if (mContext == null) {
                            return;
                        }
                        ClipboardManager clip = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                        clip.setText(url); // 复制
                        ZToastUtils.toastMessage(mContext.getApplicationContext(), mContext.getString(R.string.v_toast_copy_success) + " " + clip.getText());
                        break;
                }
            }
        });

        view.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });
    }

    public void cancel() {
        if (dialog != null) {
            dialog.cancel();
        }
    }

    /***
     * 显示对话框
     */
    public void show() {
        if (dialog != null) {
            dialog.show();
        }
    }

    /***
     * 截图 有可能出现outofMemory
     *
     * @param contentView
     * @return
     */
    private String getImagePath(View contentView) {
        try {
            Bitmap viewToShare = captureView(contentView, contentView.getWidth(), contentView.getHeight());
            String path = com.mob.tools.utils.R.getCachePath(mContext, "screenshot");
            File ss = new File(path, String.valueOf(System.currentTimeMillis()) + ".jpg");
            FileOutputStream fos = new FileOutputStream(ss);
            viewToShare.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            return ss.getAbsolutePath();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return null;
    }

    private void doShare(String platformName, Platform.ShareParams sp, boolean ssoSetting) {
        sp.setShareType(Platform.SHARE_TEXT);
        sp.setShareType(Platform.SHARE_WEBPAGE);
        ShareSDK.initSDK(mContext);
        Platform plat = ShareSDK.getPlatform(mContext, platformName);
        plat.SSOSetting(ssoSetting);
        if (platformActionListener != null) {
            plat.setPlatformActionListener(platformActionListener);
        }
        plat.share(sp);
//        ShareCore shareCore = new ShareCore();
//        shareCore.setShareContentCustomizeCallback(customizeCallback);
//        shareCore.share(plat, data);
    }

    private void wechat() {
        doShare("Wechat", getWechatDefault(false), true);
    }

    private void wechatP() {
        doShare("WechatMoments", getWechatDefault(true), true);
    }

    private void setWeiboImage(Platform.ShareParams sp) {
        try {
            ImageLoader.getInstance().loadImageSync(imageUrl, new DisplayImageOptions.Builder()
                    .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                    .bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true)
                    .cacheInMemory(false).cacheOnDisk(true).resetViewBeforeLoading(true)
                    .considerExifParams(true).build());
            String path = ImageLoader.getInstance().getDiskCache().get(imageUrl).getPath();
            if (TextUtils.isEmpty(path)) {
                return;
            }
            sp.setImagePath(path);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void setImageData(Platform.ShareParams sp, boolean isWechat) {
        if (TextUtils.isEmpty(imageUrl)) {
            if (isWechat) {
                try {
                    Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_launcher);
                    sp.setImageData(bitmap);
                    return;
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
            sp.setImagePath(getSdcardIconPath());
        } else {
            sp.setImageUrl(imageUrl);
        }
    }

    private String getSdcardIconPath() {
        if (mContext == null) {
            return null;
        }
        File distFile = new File(SdCacheTools.getOwnFileCacheDir(mContext), "ic_launcher.png");
        if (distFile.exists() && distFile.isFile()) {
            return distFile.getPath();
        }
        FileOutputStream fos = null;
        try {
            Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_launcher);
            fos = new FileOutputStream(distFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.flush();
                } catch (IOException e) {
                }
                try {
                    fos.close();
                } catch (IOException e) {
                }
            }
        }
        if (distFile.exists() && distFile.isFile()) {
            return distFile.getPath();
        }
        return null;
    }

    private void sina() {
        new Thread() {
            @Override
            public void run() {
                Platform.ShareParams sp = new Platform.ShareParams();
                sp.setTitle(title.substring(0, (title.length() > 120 ? 120 : title.length())));
                sp.setText(desc.substring(0, (desc.length() > 120 ? 120 : desc.length())) + "\n" + url);
                setWeiboImage(sp);
                doShare("SinaWeibo", sp, true);
            }
        }.start();
    }

    private void qzone() {
        doShare("QZone", getQQDefault(), false);
    }

    private void qq() {
        doShare("QQ", getQQDefault(), false);
    }

    private Platform.ShareParams getWechatDefault(boolean pyc) {
        Platform.ShareParams sp = new Platform.ShareParams();
        sp.setTitle(title);
        sp.setText(desc);
        if (pyc) {//如果是朋友圈，则显示标题
            sp.setTitle(desc);
        }
        setImageData(sp, true);
        sp.setUrl(url);
        return sp;
    }

    private Platform.ShareParams getQQDefault() {
        Platform.ShareParams sp = new Platform.ShareParams();
        sp.setTitle(title);
        sp.setTitleUrl(url);
        sp.setText(desc);
        setImageData(sp, false);
        sp.setSite(mContext.getString(R.string.app_name));
        sp.setSiteUrl(url);
        return sp;
    }
}
