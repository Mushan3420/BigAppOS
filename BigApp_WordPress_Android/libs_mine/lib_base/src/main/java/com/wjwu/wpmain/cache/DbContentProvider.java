package com.wjwu.wpmain.cache;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.wjwu.wpmain.lib_base.BaseApplication;

/**
 * Created by wjwu on 2015/10/9.
 */
public class DbContentProvider extends ContentProvider {
    public String AUTHORITY;
    private static DbManage.MySQLiteOpenHelper mDbOpenHelper;
    private static UriMatcher mMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private static final int HISTORY_READ = 1; // 匹配history_read的匹配码
    private static final int ANONY_COLLECT = 2; // 匹配people的匹配码

    private void init() {
        // static代码块，可供直接调用不用创建实例
        // 为UriMatcher注册Uri,方便进行匹配
        AUTHORITY = BaseApplication.getAuthority(getContext());//此处是优先于application
        mMatcher.addURI(AUTHORITY, DbTableHistoryRead.NAME, HISTORY_READ);
        mMatcher.addURI(AUTHORITY, DbTableAnonyCollect.NAME, ANONY_COLLECT);
    }

    @Override
    public boolean onCreate() {
        // 第一次调用此Provider时，创建数据库
        mDbOpenHelper = new DbManage.MySQLiteOpenHelper(this.getContext());
        init();
        return true;
    }

    // 删除数据
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        try {
            SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();
            int num = 0;// 记载删除记录数
            String tableName = null;
            switch (mMatcher.match(uri)) {
                case HISTORY_READ:
                    tableName = DbTableHistoryRead.NAME;
                    break;
                case ANONY_COLLECT:
                    tableName = DbTableAnonyCollect.NAME;
                    break;
                default:
                    break;
            }
            num = db.delete(tableName, selection, selectionArgs);
            getContext().getContentResolver().notifyChange(uri, null); // 通知数据已经改变
            return num;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    // 插入数据
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        try {
            SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();// 获取数据库实例
            String tableName = null;
            switch (mMatcher.match(uri)) { // 根据接收的uri判断对方的insert请求是针对哪个表的
                case HISTORY_READ:
                    tableName = DbTableHistoryRead.NAME;
                    break;
                case ANONY_COLLECT:
                    tableName = DbTableAnonyCollect.NAME;
                    break;
                default:
                    break;
            }
            long rowId = db.insert(tableName, null, values); // 插入数据
            if (rowId > 0) {
                Uri stoneUri = ContentUris.withAppendedId(uri, rowId); // 在Uri结尾加上ID数据
                getContext().getContentResolver().notifyChange(stoneUri, null); // 通知数据改变
                return stoneUri;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 查询数据
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        try {
            SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();// 获取数据库实例
            String tableName = null;
            switch (mMatcher.match(uri)) {
                case HISTORY_READ:
                    tableName = DbTableHistoryRead.NAME;
                    break;
                case ANONY_COLLECT:
                    tableName = DbTableAnonyCollect.NAME;
                    break;
                default:
                    break;
            }
            return db.query(tableName, projection, selection, selectionArgs,
                    null, null, sortOrder);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 修改数据
    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        try {
            SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();// 获取数据库实例
            int num = 0;
            String tableName = null;
            switch (mMatcher.match(uri)) {
                case HISTORY_READ:
                    tableName = DbTableHistoryRead.NAME;
                    break;
                case ANONY_COLLECT:
                    tableName = DbTableAnonyCollect.NAME;
                    break;
                default:
                    break;
            }
            num = db.update(tableName, values, selection, selectionArgs);
            getContext().getContentResolver().notifyChange(uri, null);
            return num;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    // 返回指定uri参数对应的数据的MIME类型
    @Override
    public String getType(Uri uri) {
        switch (mMatcher.match(uri)) {
            case HISTORY_READ:
            case ANONY_COLLECT:
                // return "vnd.android.cursor.dir/com.ag.demo.provider";
                // //操作的数据是多项记录
            default:
                break;
        }
        return "vnd.android.cursor.item/" + AUTHORITY;// 操作的数据是单项记录
    }

}
