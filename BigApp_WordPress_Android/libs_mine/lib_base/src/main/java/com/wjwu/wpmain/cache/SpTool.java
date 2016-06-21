package com.wjwu.wpmain.cache;

import android.content.Context;
import android.content.SharedPreferences;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class SpTool {

    private static SpTool mSpTool;
    /***
     * 缓存本地用户信息
     */
    public static final String SP_USER = "user";
    /***
     * 用户搜索历史
     */
    public static final String SP_SEARCH_KEY_HISTORY = "search_key";
    /***
     * 版本更新
     */
    public static final String SP_VERSION_UPDATE = "version_update";
    /***
     * 设置
     */
    public static final String SP_SETTING = "setting";
    /***
     * 配置相关缓存
     */
    public static final String SP_CONFIGUAE = "configure";

    private SharedPreferences mSp;

    public SpTool(Context context, String sp_name) {
        mSp = context.getApplicationContext().getSharedPreferences(sp_name, Context.MODE_PRIVATE);
    }

    public void putString(String key, String value) {
        SharedPreferences.Editor editor = mSp.edit();
        editor.putString(key, value);
        apply(editor);
    }

    public String getString(String key, String defaultString) {
        return mSp.getString(key, defaultString);
    }

    public int getInt(String key, int defaultInt) {
        return mSp.getInt(key, defaultInt);
    }

    public void putInt(String key, int defaultInt) {
        SharedPreferences.Editor editor = mSp.edit();
        editor.putInt(key, defaultInt);
        apply(editor);
    }

    public long getLong(String key, long defaultLong) {
        return mSp.getLong(key, defaultLong);
    }

    public void putLong(String key, long defaultLong) {
        SharedPreferences.Editor editor = mSp.edit();
        editor.putLong(key, defaultLong);
        apply(editor);
    }

    public boolean getBoolean(String key, boolean defaultInt) {
        return mSp.getBoolean(key, defaultInt);
    }

    public void putBoolean(String key, boolean defaultInt) {
        SharedPreferences.Editor editor = mSp.edit();
        editor.putBoolean(key, defaultInt);
        apply(editor);
    }

    /**
     * 移除某个key值已经对应的值
     */
    public void remove(String key) {
        SharedPreferences.Editor editor = mSp.edit();
        editor.remove(key);
        apply(editor);
    }

    /**
     * 清除所有数据
     */
    public void clear() {
        SharedPreferences.Editor editor = mSp.edit();
        editor.clear();
        apply(editor);
    }

    private static final Method sApplyMethod = findApplyMethod();

    private static void apply(SharedPreferences.Editor editor) {
        if (sApplyMethod != null) {
            try {
                sApplyMethod.invoke(editor);
                return;
            } catch (InvocationTargetException unused) {
                unused.printStackTrace();
            } catch (IllegalAccessException unused) {
                unused.printStackTrace();
            }
        }
        editor.commit();
    }

    private static Method findApplyMethod() {
        try {
            Class<?> cls = SharedPreferences.Editor.class;
            return cls.getMethod("apply");
        } catch (NoSuchMethodException unused) {
        }
        return null;
    }
}
