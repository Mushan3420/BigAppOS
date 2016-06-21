package com.wjwu.wpmain.lib_base;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.wjwu.wpmain.cache.SdCacheTools;
import com.wjwu.wpmain.cache.SpTool;
import com.wjwu.wpmain.util.CrashHandler;

import java.io.File;

public class BaseApplication extends Application {

    public static int sDefaultImageDrawable = R.drawable.z_iv_default;

    private static String sProviderAuthority = null;

    public static final String TAG = BaseApplication.class
            .getSimpleName();

    private RequestQueue mRequestQueue;

    protected static BaseApplication mInstance;

    protected static String mUserId = null;

    protected static String mCookies = null;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        initUil();
        getAuthority();
        CrashHandler.getInstance().init(getApplicationContext());
    }

    public static String getAuthority() {
        return getAuthority(null);
    }

    public static String getAuthority(Context context) {
        if (sProviderAuthority == null) {
            if (context == null && mInstance != null) {
                context = mInstance.getApplicationContext();
            }
            if (context != null) {
                try {
                    ApplicationInfo appInfo = context.getPackageManager()
                            .getApplicationInfo(context.getPackageName(),
                                    PackageManager.GET_META_DATA);
                    if (appInfo != null && appInfo.metaData != null) {
                        sProviderAuthority = appInfo.metaData.getString("provider_authorities");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    sProviderAuthority = context.getPackageName() + ".provider_native";
                }
            }
        }
        return sProviderAuthority;
    }

    private void initUil() {
        File cacheDir = SdCacheTools.getOwnImageCacheDir(this);
        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(this)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCache(new UnlimitedDiskCache(cacheDir))
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileCount(100)
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                .memoryCacheSizePercentage(13)
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .build();
        ImageLoader.getInstance().init(configuration);
    }

    public static String getCookies() {
        if (mCookies == null) {
            mCookies = new SpTool(mInstance.getApplicationContext(), SpTool.SP_USER).getString("cookies", null);
        }
        return mCookies;
    }

    public static void setCookies(String cookies) {
        mCookies = cookies;
        new SpTool(mInstance.getApplicationContext(), SpTool.SP_USER).putString("cookies", cookies);
    }

    public static String getUserId() {
        if (mUserId == null) {
            mUserId = new SpTool(mInstance.getApplicationContext(), SpTool.SP_USER).getString("userId", null);
        }
        return mUserId;
    }

    public static synchronized BaseApplication getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
//            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
            mRequestQueue = getRequestQueueCustom(getApplicationContext());
        }

        return mRequestQueue;
    }

    private RequestQueue getRequestQueueCustom(Context context) {
        File cacheDir = SdCacheTools.getOwnVolleyCacheDir(context);
        HurlStack stack = null;
        if (Build.VERSION.SDK_INT >= 9) {
            stack = new HurlStack();
        } else {
            stack = new HurlStack();
//            stack = new HttpClientStack(AndroidHttpClient.newInstance(userAgent));
        }
        BasicNetwork network1 = new BasicNetwork(stack);
        long externalUsage = SdCacheTools.getAvailableSize(true);
        if (externalUsage == -1) {
            externalUsage = SdCacheTools.getAvailableSize(false);
        }
        RequestQueue queue1 = new RequestQueue(new DiskBasedCache(cacheDir, externalUsage > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) externalUsage), network1, 5);
        queue1.start();
        return queue1;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

}