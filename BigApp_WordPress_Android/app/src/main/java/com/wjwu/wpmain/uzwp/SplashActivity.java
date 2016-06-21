package com.wjwu.wpmain.uzwp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;
import com.wjwu.wpmain.cache.BaseResponse;
import com.wjwu.wpmain.cache.FileCache;
import com.wjwu.wpmain.net.CustomConfigure;
import com.wjwu.wpmain.net.RequestTools;
import com.wjwu.wpmain.net.RequestUrl;
import com.wjwu.wpmain.util.ResponseListener;
import com.wjwu.wpmain.util.TaskExecutor;
import com.wjwu.wpmain.util.ZLogUtils;

import java.util.ArrayList;
import java.util.Map;

import model.ConfInfo;
import model.NavCatalog;
import model.VersionUpdate;

/**
 * Created by wjwu on 2015/9/2.
 */
public class SplashActivity extends Activity {

    private VersionUpdate.VersionInfo mVersionInfo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getConf();
    }

    private void getConf() {
        new RequestTools(new ResponseListener(getApplicationContext(), null) {
            @Override
            public void onSuccess(Object obj) {
                if (obj != null) {
                    try {
                        BaseResponse<ConfInfo> base = (BaseResponse<ConfInfo>) obj;
                        if (base.error_code == 0) {
                            FileCache.saveConf(base.data);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                loadDatas();
            }

            @Override
            public void onSuccessError() {
            }

            @Override
            public void useCacheNotAndNoNetwork() {
                loadDatas();
            }

            @Override
            public void onError(VolleyError error) {
                loadDatas();
            }

            @Override
            public void onCacheData(Object obj, boolean hasNetwork) {
            }

            @Override
            public void onCacheDataError(boolean hasNetwork) {
            }
        }).makeCustomJsonRequest(RequestUrl.get_conf, false, Request.Method.GET, null, new TypeToken<BaseResponse<ConfInfo>>() {
        }, "get_conf");
    }

    private void loadDatas() {
        //如果为空的话，才从网络上获取，否则直接返回
        new RequestTools(new ResponseListener<ArrayList<NavCatalog>>(getApplicationContext()) {
            @Override
            public void onSuccess(final Object tempObj) {
                TaskExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ArrayList<NavCatalog> userNavList = null;
                            if (tempObj == null) {//如果数据没有更新，则直接使用缓存数据
                                userNavList = FileCache.getCatalogUser();
                            } else {
                                final BaseResponse<ArrayList<NavCatalog>> obj = (BaseResponse<ArrayList<NavCatalog>>) tempObj;
                                if (obj.data == null || obj.data.size() == 0) {//如果服务器返回的栏目是空，则清空缓存
                                    FileCache.saveCatalogUser(null);
                                    FileCache.saveCatalogOthers(null);
                                } else {
                                    userNavList = FileCache.getCatalogUser();
                                    if (userNavList == null) {//如果用戶设置的为空，重置用户栏目
                                        userNavList = resetUserCatalog(obj.data, userNavList);
                                    } else { //如果用户定义的不为空，则需要遍历了
                                        ArrayList<NavCatalog> tempUserList = new ArrayList<NavCatalog>();
                                        tempUserList.addAll(userNavList);
                                        //过滤掉用户定制的，但是后台后来把那个栏目删除了，则需要同步删除
                                        NavCatalog firstNav = obj.data.get(0);
                                        obj.data.remove(0);
                                        ArrayList<NavCatalog> otherCatalog = new ArrayList<NavCatalog>();
                                        otherCatalog.addAll(obj.data);
                                        //过滤掉用户定制的，但是后台后来把那个栏目删除了，则需要同步删除
                                        userNavList.clear();
                                        for (NavCatalog userCatalog : tempUserList) {
                                            for (NavCatalog catalog : obj.data) {
                                                if (userCatalog.ID.equals(catalog.ID)) {
                                                    userNavList.add(catalog);
                                                    otherCatalog.remove(catalog);//如果用户已经定制过的，则从其他栏目中删除
                                                    break;
                                                }
                                            }
                                        }
                                        if (userNavList.size() == 0) {//如果定制的已经全部从后台删除了，则重置用户栏目
                                            obj.data.add(0, firstNav);
                                            userNavList = resetUserCatalog(obj.data, userNavList);
                                        } else {
                                            try {
                                                //直接添加第一个
                                                userNavList.add(0, firstNav);
                                            } catch (Exception e) {
                                                ZLogUtils.logException(e);
                                            }
                                            FileCache.saveCatalogUser(userNavList);
                                            FileCache.saveCatalogOthers(otherCatalog);
                                        }
                                    }
                                }
                            }
                            Message msg = new Message();
                            msg.obj = userNavList;
                            msg.arg1 = 1;
                            mHandler.sendMessage(msg);
                        } catch (Exception e) {
                            ZLogUtils.logException(e);
                            onError(new VolleyError(e));
                        }
                    }
                });
            }

            @Override
            public void onSuccessError() {
                ArrayList<NavCatalog> tmpList = FileCache.getCatalogUser();
                if (tmpList != null) {
                    gotoMain(tmpList);
                }
            }

            @Override
            public void onError(VolleyError error) {
                ArrayList<NavCatalog> tmpList = FileCache.getCatalogUser();
                if (tmpList != null) {
                    gotoMain(tmpList);
                }
            }

            @Override
            public void onCacheData(Object obj, boolean hasNetwork) {
                // httpcache 有，但是navcatalog的cache没有了,怎么办，所以在清空缓存的时候需要考虑两边要一起清空，至少navcatalog的cache一定要有
                //TODO
                if (!hasNetwork) {
                    gotoMain(FileCache.getCatalogUser());
                }
            }

            @Override
            public void onCacheDataError(boolean hasNetwork) {
                if (!hasNetwork) {
                    gotoMain(FileCache.getCatalogUser());
                }
            }
        }).sendRequest(RequestUrl.nav_list, true, Request.Method.GET, null, new TypeToken<BaseResponse<ArrayList<NavCatalog>>>() {
        }, "nav_list");
    }

    private android.os.Handler mHandler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            gotoMain((ArrayList<NavCatalog>) msg.obj);
        }
    };

    private ArrayList<NavCatalog> resetUserCatalog(ArrayList<NavCatalog> allCatalog, ArrayList<NavCatalog> userNavList) {
        if (allCatalog.size() > CustomConfigure.MAX_DEFAUL_CATALOG_SHOW) {//如果大于默认显示的15个，则截取，并将其他的栏目保存到其他栏目里面
            userNavList = (ArrayList)
                    allCatalog.subList(0, CustomConfigure.MAX_DEFAUL_CATALOG_SHOW);
            FileCache.saveCatalogOthers((ArrayList)
                    allCatalog.subList(CustomConfigure.MAX_DEFAUL_CATALOG_SHOW, allCatalog.size()));
        } else {
            userNavList = allCatalog;
        }
        FileCache.saveCatalogUser(userNavList);
        return userNavList;
    }

    private void gotoMain(ArrayList<NavCatalog> cacheNavList) {
        Log.e("", "wenjun gotoMain cacheNavList = " + (cacheNavList == null ? null : cacheNavList.size())
                + ", mVersionInfo = " + mVersionInfo);
        Intent intent = new Intent(this, ActivityMainSliding.class);
        Bundle extras = new Bundle();
        extras.putSerializable("userNavList", cacheNavList);
        extras.putSerializable("versionInfo", mVersionInfo);
        intent.putExtras(extras);
        startActivity(intent);
        finish();
    }

    public class AnalysisSDK {
        public String status;
        public String msg;
        public Config res;

        public class Config {
            public Map<String, Object> config;
        }
    }
}
