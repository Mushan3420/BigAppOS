package com.wjwu.wpmain.doffline;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.wjwu.wpmain.uzwp.R;

public class OfflineDownloadNotification {
    private static final int nId = 8002;

    private Context mContext;

    private NotificationManager mNM;
    private PendingIntent mDownloadingIntent;
    private PendingIntent mContentIntent;
    private String mProgressText;
    private int mProgress;
    private int mMax;
    private int mFlags;
    private boolean mDone;
    private long mWhen;

    public OfflineDownloadNotification(Context context) {
        mContext = context;
        mNM = (NotificationManager) mContext
                .getSystemService(Context.NOTIFICATION_SERVICE);
        mMax = 0;
        mProgress = 0;

        Intent resultIntent = new Intent();
        mDownloadingIntent = PendingIntent.getActivity(mContext, 0,
                resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        mDone = false;
        mWhen = System.currentTimeMillis();
    }

    public void updateProgress(int max, int progress, String name) {
        setProgress(max, progress).notify(name);
    }

    public void notifyStartDownload() {
        mDone = false;
        setContentIntent(mDownloadingIntent).setProgress(100, 0)
                .setOngoing(true).notify("");
    }

    public void notifyComplete() {
        mDone = true;
        setOngoing(false).notify(null);
    }

    public void notifyFail() {
        mDone = true;
        setOngoing(false).setAutoCancel(true).notify(mContext.getString(R.string.z_download_offline_fail));
    }

    private OfflineDownloadNotification setProgress(int max, int progress) {
        mMax = max;
        mProgress = progress;
        if (max != 0) {
            mProgressText = progress * 100 / max + "%";
        }
        return this;
    }

    private OfflineDownloadNotification setOngoing(boolean ongoing) {
        setFlag(Notification.FLAG_ONGOING_EVENT, ongoing);
        return this;
    }

    private OfflineDownloadNotification setContentIntent(PendingIntent pendingIntent) {
        mContentIntent = pendingIntent;
        return this;
    }

    private OfflineDownloadNotification setAutoCancel(boolean autoCancel) {
        setFlag(Notification.FLAG_AUTO_CANCEL, autoCancel);
        return this;
    }

    private OfflineDownloadNotification setProgressText(String text) {
        mProgressText = text;
        return this;
    }

    private void notify(String name) {
        Notification notification = new Notification();
        notification.icon = R.mipmap.ic_launcher;
        notification.tickerText = mContext.getString(R.string.z_download_offline);

        notification.contentIntent = mContentIntent;
        notification.flags = mFlags;
        notification.when = mWhen;

        RemoteViews contentView;
        if (mDone) {
            if (name != null) {
                contentView = new RemoteViews(mContext.getPackageName(),
                        R.layout.z_notification_download_offline_fail);
                contentView.setTextViewText(R.id.content, name);
            } else {
                contentView = new RemoteViews(mContext.getPackageName(),
                        R.layout.z_notification_download_offline_succ);
                contentView.setTextViewText(R.id.title, mContext.getString(R.string.z_version_download_finish));
                contentView.setTextViewText(R.id.content, mContext.getString(R.string.z_download_offline_read_tip));
            }
            contentView.setImageViewResource(R.id.icon, R.mipmap.ic_launcher);
        } else {
            contentView = new RemoteViews(mContext.getPackageName(),
                    R.layout.z_notification_download_offline);
            contentView.setTextViewText(R.id.title, notification.tickerText);
            contentView.setTextViewText(R.id.content, name);
            contentView.setTextViewText(R.id.percent, mProgressText);
            contentView.setImageViewResource(R.id.icon, R.mipmap.ic_launcher);
            contentView.setProgressBar(R.id.progress, mMax, mProgress,
                    mProgress == 0);
        }
        notification.contentView = contentView;
        mNM.notify(nId, notification);
    }

    public void cancel() {
        mNM.cancel(nId);
    }

    private void setFlag(int mask, boolean value) {
        if (value) {
            mFlags |= mask;
        } else {
            mFlags &= ~mask;
        }
    }
}
