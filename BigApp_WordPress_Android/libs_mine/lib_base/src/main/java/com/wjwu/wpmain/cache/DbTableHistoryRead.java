package com.wjwu.wpmain.cache;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TimeZone;

import model.HistoryRead;

/**
 * Created by wjwu on 2015/10/9.
 */
public class DbTableHistoryRead extends BaseDbTable {

    public static final String NAME = "history_read";

    public DbTableHistoryRead(ContentResolver contentResolver) {
        setContentURLAndResolver(NAME, contentResolver);
    }

    public static class Columns implements BaseColumns {
        public static final String TOPIC_ID = "topic_id";
        public static final String LINK = "link";
        public static final String TITLE = "title";
        public static final String CREATETIME = "createTime";
        public static final String CREATEDATE = "createDate";
    }

    public static final String CREATE_SQL = "create table " + NAME + " ("
            + Columns._ID + " integer primary key autoincrement, "
            + Columns.TOPIC_ID + " integer, "
            + Columns.LINK + " text, "
            + Columns.TITLE + " text, "
            + Columns.CREATEDATE + " text, "
            + Columns.CREATETIME + " long,"
            + "UNIQUE (" + Columns.TOPIC_ID + ", " + Columns.CREATEDATE + ") ON CONFLICT REPLACE);\n";

    public boolean saveOrReplace(HistoryRead obj) {
        if (obj == null) {
            return false;
        }
        boolean result = true;
        long createTime = System.currentTimeMillis();
        ContentValues values = new ContentValues();
        values.put(Columns.TOPIC_ID, obj.topic_id);
        values.put(Columns.LINK, obj.link);
        values.put(Columns.TITLE, obj.title);
        values.put(Columns.CREATETIME, createTime);
        values.put(Columns.CREATEDATE, new SimpleDateFormat("yyyy-MM-dd").format(createTime));
        Uri uri = mContentResolver.insert(CONTENT_URL, values);
        if (uri == null || uri.toString().length() <= 0) {
            result = false;
        }
        return result;
    }

    public boolean delete(int id) {
        return mContentResolver.delete(CONTENT_URL, Columns._ID + "=" + id, null) > 0;
    }

    public boolean clear() {
        return mContentResolver.delete(CONTENT_URL, null, null) > 0;
    }

    private long getZeroCurrentTime(long currentTime) {
        return currentTime / (1000 * 3600 * 24) * (1000 * 3600 * 24) - TimeZone.getDefault().getRawOffset();
    }

    public ArrayList<HistoryRead> getLatelyHistorys() {
        long currentTime = System.currentTimeMillis();
        String section = Columns.CREATETIME + ">" + (getZeroCurrentTime(currentTime) - 6 * 24 * 3600 * 1000 - 1);
        Cursor cursor = mContentResolver.query(CONTENT_URL, new String[]{
                Columns._ID, Columns.TOPIC_ID, Columns.LINK,
                Columns.TITLE, Columns.CREATEDATE, Columns.CREATETIME}, section, null, Columns.CREATETIME + " DESC");
        ArrayList<HistoryRead> list = new ArrayList<>();
        String lastCreateDate = null;
        if (cursor != null && cursor.getCount() > 0) {
            HistoryRead obj = null;
            int newDaySize = 0;
            int timePosition = 0;
            while (cursor.moveToNext()) {
                obj = new HistoryRead();
                obj._id = cursor.getInt(cursor.getColumnIndex(Columns._ID));
                obj.topic_id = cursor.getInt(cursor.getColumnIndex(Columns.TOPIC_ID));
                obj.link = cursor.getString(cursor.getColumnIndex(Columns.LINK));
                obj.title = cursor.getString(cursor.getColumnIndex(Columns.TITLE));
                obj.createDate = cursor.getString(cursor.getColumnIndex(Columns.CREATEDATE));
                obj.createTime = cursor.getLong(cursor.getColumnIndex(Columns.CREATETIME));
                if (obj.createDate != null) {
                    if (lastCreateDate == null) {
                        lastCreateDate = obj.createDate;
                    }
                    if (!obj.createDate.equals(lastCreateDate)) {
                        list.add(timePosition, new HistoryRead(true, lastCreateDate, newDaySize));
                        lastCreateDate = obj.createDate;
                        timePosition = newDaySize + 1;
                        newDaySize = 0;
                    }
                    newDaySize++;
                }
                obj.timePosition = timePosition;
                list.add(obj);
            }
            if (list.size() > 0 && newDaySize > 0) {
                list.add(timePosition, new HistoryRead(true, lastCreateDate, newDaySize));
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        return list;
    }
}
