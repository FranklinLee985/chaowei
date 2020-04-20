package com.eduhdsdk.tools;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;

import com.eduhdsdk.R;
import com.eduhdsdk.ui.OneToManyActivity;
import com.eduhdsdk.ui.OneToOneActivity;

public class MonitorService extends Service {
    public static final String KEY = "ClassName";
    private NotificationManager manager;

    public MonitorService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @SuppressLint("StringFormatInvalid")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String CHANNEL_ONE_ID = "CHANNEL_ONE_ID";
        String CHANNEL_ONE_NAME = "CHANNEL_ONE_ID";
        NotificationChannel notificationChannel = null;
        Notification.Builder notification = new Notification.Builder(this);
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (intent != null) {
            Intent resultIntent = new Intent(this,
                    intent.getStringExtra(KEY).equals(OneToOneActivity.class.getName()) ? OneToOneActivity.class : OneToManyActivity.class);
            resultIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pIntent = PendingIntent.getActivity(
                    getApplicationContext(), 0, resultIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            //设置通知内容
            notification.setTicker(getString(ResourceSetManage.getInstance().getAppName()))
                    .setSmallIcon(R.drawable.tk_logo)                                                                           //设置小logo
                    .setContentIntent(pIntent)                                                                                  //设置点击事件
                    .setAutoCancel(true)
                    .setContentTitle(getString(ResourceSetManage.getInstance().getAppName()))                                   //设置通知的标题
                    .setContentText(getString(R.string.back_hint, getString(ResourceSetManage.getInstance().getAppName())));
        } else {
            notification.setTicker(getString(ResourceSetManage.getInstance().getAppName()))
                    .setSmallIcon(R.drawable.tk_logo)                                                                           //设置小logo
                    .setAutoCancel(true)
                    .setContentTitle(getString(ResourceSetManage.getInstance().getAppName()))                                   //设置通知的标题
                    .setContentText(getString(R.string.back_hint, getString(ResourceSetManage.getInstance().getAppName())));
        }
        //进行8.0的判断
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = new NotificationChannel(CHANNEL_ONE_ID,
                    CHANNEL_ONE_NAME, NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            manager.createNotificationChannel(notificationChannel);
            notification.setChannelId(CHANNEL_ONE_ID);
            startForeground(10086, notification.build());
        } else {
            manager.notify(10086, notification.build());
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        if (manager != null)
            manager.cancel(10086);
        stopForeground(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
    }
}
