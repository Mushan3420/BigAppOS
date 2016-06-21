package com.wjwu.wpmain.cache;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import model.Topic;
import model.TopicImage;

/**
 * Created by wjwu on 2015/10/9.
 */
public class DbTableAnonyCollect extends BaseDbTable {

    public static final String NAME = "anony_collect";

    public DbTableAnonyCollect(ContentResolver contentResolver) {
        setContentURLAndResolver(NAME, contentResolver);
    }

    public static class Columns implements BaseColumns {
        public static final String TOPIC_ID = "topic_id";
        public static final String LINK = "link";
        public static final String DATE_GMT = "date_gmt";
        public static final String TITLE = "title";
        public static final String VIEWS = "views";
        public static final String COMMENT_NUM = "comment_num";
        public static final String FEATURED_IMAGE = "featured_image";
        public static final String CREATE_TIME = "create_time";
    }

    public static final String CREATE_SQL = "create table " + NAME + " ("
            + Columns._ID + " integer primary key autoincrement, "
            + Columns.TOPIC_ID + " integer, "
            + Columns.LINK + " text, "
            + Columns.DATE_GMT + " text,"
            + Columns.TITLE + " text, "
            + Columns.VIEWS + " text, "
            + Columns.COMMENT_NUM + " integer,"
            + Columns.FEATURED_IMAGE + " text,"
            + Columns.CREATE_TIME + " long,"
            + "UNIQUE (" + Columns.TOPIC_ID + ") ON CONFLICT REPLACE);\n";

    public boolean saveOrReplace(Topic obj) {
        if (obj == null) {
            return false;
        }
        boolean result = true;
        ContentValues values = new ContentValues();
        values.put(Columns.CREATE_TIME, System.currentTimeMillis());
        values.put(Columns.TOPIC_ID, obj.ID);
        values.put(Columns.LINK, obj.link);
        values.put(Columns.DATE_GMT, obj.date_gmt);
        values.put(Columns.TITLE, obj.title);
        values.put(Columns.VIEWS, obj.views);
        values.put(Columns.COMMENT_NUM, obj.comment_num);
        values.put(Columns.FEATURED_IMAGE, obj.featured_image == null ? null : new Gson().toJson(obj.featured_image));
        Uri uri = mContentResolver.insert(CONTENT_URL, values);
        if (uri == null || uri.toString().length() <= 0) {
            result = false;
        }
        return result;
    }

    public boolean delete(int topic_id) {
        return mContentResolver.delete(CONTENT_URL, Columns.TOPIC_ID + "=" + topic_id, null) > 0;
    }

    public boolean clear() {
        return mContentResolver.delete(CONTENT_URL, null, null) > 0;
    }

    public boolean checkIfExits(int topic_id) {
        Cursor cursor = mContentResolver.query(CONTENT_URL, new String[]{Columns._ID, Columns.TITLE}, Columns.TOPIC_ID + "=" + topic_id, null, null);
        if (cursor == null) {
            return false;
        }
        boolean result = false;
        if (cursor.getCount() > 0) {
            result = true;
        }
        cursor.close();
        return result;
    }

    public ArrayList<Topic> getAllAnonyCollects() {
        Cursor cursor = mContentResolver.query(CONTENT_URL, new String[]{
                Columns._ID, Columns.TOPIC_ID, Columns.LINK, Columns.DATE_GMT,
                Columns.TITLE, Columns.VIEWS, Columns.COMMENT_NUM, Columns.FEATURED_IMAGE, Columns.CREATE_TIME}, null, null, Columns.CREATE_TIME + " DESC");
        ArrayList<Topic> list = new ArrayList<>();
        if (cursor != null && cursor.getCount() > 0) {
            Topic obj;
            while (cursor.moveToNext()) {
                obj = new Topic();
                obj.ID = cursor.getInt(cursor.getColumnIndex(Columns.TOPIC_ID));
                obj.link = cursor.getString(cursor.getColumnIndex(Columns.LINK));
                obj.date_gmt = cursor.getString(cursor.getColumnIndex(Columns.DATE_GMT));
                obj.title = cursor.getString(cursor.getColumnIndex(Columns.TITLE));
                obj.views = cursor.getString(cursor.getColumnIndex(Columns.VIEWS));
                obj.comment_num = cursor.getInt(cursor.getColumnIndex(Columns.COMMENT_NUM));
                obj.featured_image = new Gson().fromJson(cursor.getString(cursor.getColumnIndex(Columns.FEATURED_IMAGE)), new TypeToken<ArrayList<TopicImage>>() {
                }.getType());
                list.add(obj);
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        return list;
    }
}
