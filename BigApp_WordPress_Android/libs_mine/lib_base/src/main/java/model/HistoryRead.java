package model;

import java.io.Serializable;

/**
 * Created by wjwu on 2015/10/9.
 */
public class HistoryRead implements Serializable {
    public int _id;
    public int topic_id;
    public String title;
    public String link;
    public long createTime;
    public String createDate;
    public boolean isNewDay = false;
    public int newDaySize = 0;
    public int timePosition = 0;

    public HistoryRead() {
    }

    public HistoryRead(boolean isNewDay, String createDate, int newDaySize) {
        this.isNewDay = isNewDay;
        this.createDate = createDate;
        this.newDaySize = newDaySize;
    }

    public HistoryRead(int topic_id, String title, String link) {
        this.topic_id = topic_id;
        this.title = title;
        this.link = link;
    }

    public HistoryRead(int _id, int topic_id, String title, String link, long createTime, String createDate) {
        this._id = _id;
        this.topic_id = topic_id;
        this.title = title;
        this.link = link;
        this.createTime = createTime;
        this.createDate = createDate;
    }
}
