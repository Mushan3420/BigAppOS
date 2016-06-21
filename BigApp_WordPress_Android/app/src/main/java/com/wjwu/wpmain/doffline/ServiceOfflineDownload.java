package com.wjwu.wpmain.doffline;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.wjwu.wpmain.cache.BaseResponse;
import com.wjwu.wpmain.cache.FileCache;
import com.wjwu.wpmain.cache.SettingCache;
import com.wjwu.wpmain.lib_base.BaseApplication;
import com.wjwu.wpmain.net.RequestTools;
import com.wjwu.wpmain.util.CommonUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

import model.NavCatalog;
import model.Topic;
import model.TopicImage;

/**
 * Created by wjwu on 2015/9/23.
 */
public class ServiceOfflineDownload extends IntentService {

    private OfflineDownloadNotification mOfflineDownloadNotification;

    public ServiceOfflineDownload() {
        super("ServiceOfflineDownload");
    }

    private boolean hasError = false;

    @Override
    protected void onHandleIntent(Intent intent) {
        if (!CommonUtils.checkNetworkEnable(getApplicationContext(), false)) {
            return;
        }
        int mode = SettingCache.getImgMode(getBaseContext());
        mOfflineDownloadNotification = new OfflineDownloadNotification(getBaseContext());
        ArrayList<NavCatalog> catalogs = FileCache.getCatalogUser();
        if (catalogs == null || catalogs.size() == 0) {
            //TODO 特殊处理
            return;
        }
        int mTaskSize = catalogs.size();
        mOfflineDownloadNotification.notifyStartDownload();
        Log.e("", "wenjun download thread start");
        for (int i = 0; i < mTaskSize; i++) {
            if (hasError) {
                break;
            }
            if (i == 0) {
                mOfflineDownloadNotification.updateProgress(100, 100 / (mTaskSize + 1) * (i + 1) > 5 ? 5 : 0, catalogs.get(i).name + "");
            }
            cacheList(catalogs.get(i), mode);
            mOfflineDownloadNotification.updateProgress(100, 100 / (mTaskSize + 1) * (i + 1), catalogs.get(i).name + "");
        }
        if (hasError) {
            mOfflineDownloadNotification.notifyFail();
            return;
        }
        mOfflineDownloadNotification.notifyComplete();
        Log.e("", "wenjun download thread end");
    }

    private void cacheList(final NavCatalog catalog, final int mode) {
        Log.e("", "wenjun download cacheList type = " + catalog.type + ", anme = " + catalog.name);
        if ("custom".equals(catalog.type)) {
            //外链不保存吧？
            return;
        }
        if ("post_type".equals(catalog.type)) {
            //缓存link对应的第一个topic的content内容
            cacheLinkContent(catalog.link, mode);
            return;
        }
        if ("taxonomy".equals(catalog.type)) {
            //缓存列表中每一个内容
            Object obj = new RequestTools(null).makeJsonRequestForCache2(catalog.name, catalog.link + "&img_mod=" + (mode == 1 ? 3 : mode) + "&filter[posts_per_page]=" + 10 + "&page=" + 1, Request.Method.GET, null, new TypeToken<BaseResponse<ArrayList<Topic>>>() {
            }, catalog.name + "");
            if (obj == null) {
                return;
            }
            if (obj != null && obj instanceof Exception) {
                hasError = true;
                return;
            }
            try {
                BaseResponse<ArrayList<Topic>> res = (BaseResponse<ArrayList<Topic>>) obj;
                if (res.error_code == 0) {
                    ArrayList<Topic> list = res.data;
                    int size = list.size();
                    for (int i = 0; i < size; i++) {
                        if (hasError) {
                            break;
                        }
                        cacheTopicImage(list.get(i).featured_image);
                        cacheLinkContent(list.get(i).link, mode);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void cacheTopicImage(ArrayList<TopicImage> imgList) {
        if (imgList == null || imgList.size() == 0) {
            return;
        }
        for (TopicImage image : imgList) {
            if (image == null || TextUtils.isEmpty(image.source)) {
                continue;
            }
            ImageLoader.getInstance().loadImageSync(image.source, new DisplayImageOptions.Builder()
                    .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                    .bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true)
                    .cacheInMemory(false).cacheOnDisk(true).resetViewBeforeLoading(true)
                    .considerExifParams(true).build());
        }
    }


    private void cacheLinkContent(String link, int mode) {
        link = link + "&img_mod=" + (mode == 1 ? 3 : mode);
        Object obj = new RequestTools(null).makeJsonRequestForCache2("detail", link, Request.Method.GET, null, new TypeToken<BaseResponse<Topic>>() {
        }, "link_detail_cache");
        if (obj == null) {
            return;
        }
        if (obj != null && obj instanceof Exception) {
            //TODO
//            hasError = true;
            return;
        }
        try {
            cacheContentImg(link, obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cacheContentImg(String link, Object obj) {
        //TODO 下载webContent图片
        Topic topic = ((BaseResponse<Topic>) obj).data;
        if (topic == null) {
            return;
        }
        try {
            Document doc = Jsoup.parse(topic.content);
            Elements es = doc.getElementsByTag("img");
            //解析html将其img标签的src值改成图片路径
            for (Element e : es) {
                String imgUrl = e.attr("src");
                if (TextUtils.isEmpty(imgUrl) || imgUrl.endsWith("gif")) {
                    continue;
                }
                ImageLoader.getInstance().loadImageSync(imgUrl, new DisplayImageOptions.Builder()
                        .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                        .bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true)
                        .cacheInMemory(false).cacheOnDisk(true).resetViewBeforeLoading(true)
                        .considerExifParams(true).build());
                try {
                    e.attr("src", "file://" + ImageLoader.getInstance().getDiskCache().get(imgUrl).getPath());
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
            Cache cache = BaseApplication.getInstance().getRequestQueue().getCache();
            Cache.Entry entry = cache.get(link);
            if (entry != null) {
                topic.content = doc.html();
                ((BaseResponse<Topic>) obj).data = topic;
                entry.data = new Gson().toJson(obj).getBytes("UTF-8");
                cache.put(link, entry);
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }
}
