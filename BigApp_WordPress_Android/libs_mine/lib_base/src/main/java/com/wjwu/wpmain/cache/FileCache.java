package com.wjwu.wpmain.cache;

import com.wjwu.wpmain.lib_base.BaseApplication;
import com.wjwu.wpmain.util.FileTools;

import java.io.File;
import java.util.ArrayList;

import model.ConfInfo;
import model.NavCatalog;
import model.VersionUpdate;

/**
 * Created by wjwu on 2015/9/2.
 * 保存到内部文件的重要缓存信息（目前有 首页栏目缓存）
 */
public class FileCache {

    private static File getCacheFileDir() {
        return SdCacheTools.getOwnInnerFileCacheDir(BaseApplication.getInstance().getApplicationContext());
    }

    /***
     * 保存配置信息
     *
     * @param info info
     */
    public static void saveConf(ConfInfo info) {
        FileTools.saveObject(new File(getCacheFileDir(), "conf_info"), info);
    }

    public static ConfInfo getConf() {
        Object obj = FileTools.readObject(new File(getCacheFileDir(), "conf_info"));
        if (obj == null) {
            return null;
        }
        return (ConfInfo) obj;
    }

    /***
     * 保存配置信息
     *
     * @param info info
     */
    public static void saveVersionInfo(VersionUpdate.VersionInfo info) {
        FileTools.saveObject(new File(getCacheFileDir(), "version_info"), info);
    }

    public static VersionUpdate.VersionInfo getVersionInfo() {
        Object obj = FileTools.readObject(new File(getCacheFileDir(), "version_info"));
        if (obj == null) {
            return null;
        }
        return (VersionUpdate.VersionInfo) obj;
    }

    /***
     * 保存用户选择的栏目
     *
     * @param object object
     */
    public static void saveCatalogUser(ArrayList<NavCatalog> object) {
        //此处 BaseApplication.getUserId() 涉及到退出登录和成功登录后 栏目 需要同步变化的问题，故先不做此处理，栏目不随用户变更
//        FileTools.saveObject(new File(getCacheFileDir(), "catalog_user_" + BaseApplication.getUserId()), object);
        FileTools.saveObject(new File(getCacheFileDir(), "catalog_user_"), object);
    }

    /***
     * 获取用户选择的栏目
     *
     * @return object
     */
    public static ArrayList<NavCatalog> getCatalogUser() {
//        Object obj = FileTools.readObject(new File(getCacheFileDir(), "catalog_user_" + BaseApplication.getUserId()));
        Object obj = FileTools.readObject(new File(getCacheFileDir(), "catalog_user_"));
        if (obj == null) {
            return null;
        }
        return (ArrayList<NavCatalog>) obj;
    }

    /***
     * 保存其它栏目
     *
     * @param object object
     */
    public static void saveCatalogOthers(ArrayList<NavCatalog> object) {
//        FileTools.saveObject(new File(getCacheFileDir(), "catalog_other_" + BaseApplication.getUserId()), object);
        FileTools.saveObject(new File(getCacheFileDir(), "catalog_other_"), object);
    }

    /***
     * 获取其它栏目
     *
     * @return object
     */
    public static ArrayList<NavCatalog> getCatalogOthers() {
//        Object obj = FileTools.readObject(new File(getCacheFileDir(), "catalog_other_" + BaseApplication.getUserId()));
        Object obj = FileTools.readObject(new File(getCacheFileDir(), "catalog_other_"));
        if (obj == null) {
            return null;
        }
        return (ArrayList<NavCatalog>) obj;
    }
}
