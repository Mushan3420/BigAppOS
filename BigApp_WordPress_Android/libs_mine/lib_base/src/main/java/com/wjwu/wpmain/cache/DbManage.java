package com.wjwu.wpmain.cache;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by wjwu on 2015/10/9.
 */
public class DbManage {

    private final static int DB_VERSION = 120;

    private static void resetDb(SQLiteDatabase db) {
        db.execSQL(DbTableHistoryRead.CREATE_SQL);
        db.execSQL(DbTableAnonyCollect.CREATE_SQL);
    }

    public static class MySQLiteOpenHelper extends SQLiteOpenHelper {
        /**
         * 数据库构造方法
         */
        public MySQLiteOpenHelper(Context context) {
            super(context, "wp_db", null, DB_VERSION);
        }

        /**
         * 数据库首次创建时所调用的方法 for：创建数据库的表结构&存放一些初始化参数
         */
        @Override
        public void onCreate(SQLiteDatabase db) {
            resetDb(db);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            if (oldVersion < 114) {// 老版本如果在114以下，则要先执行114的数据库变更
            }
            if (oldVersion == 130) {
                oldVersion = 133;
            }
            if (oldVersion < 133 && newVersion >= 133) {
                // 老版本如果在133以下，并且当前版本在133或者133以上，则需要执行133的数据库变更
            }
            if (oldVersion < 134 && newVersion >= 134) {
                // 老版本如果在134以下，并且当前版本在134或者134以上，则需要执行134的数据库变更
            }
            if (oldVersion < 135 && newVersion >= 135) {
                // 老版本如果在135以下，并且当前版本在135或者135以上，则需要执行135的数据库变更
            }
        }
    }
}
