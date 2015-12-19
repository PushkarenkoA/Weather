package com.pushkarenko.weather;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import java.util.concurrent.TimeUnit;

/**
 * Created by Андрей on 17.12.2015.
 */
public class ServiceNotification extends Service {
    private NotificationManager nm;
    private final int NOTIFICATION_ID = 73;
    Notification notification;
    public static boolean state = true;


    final String TAG = "myLogs";

    public void onCreate() {
        super.onCreate();
        state = false;
        nm = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        Log.d(TAG, "NotifyService - onCreate");
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public int onStartCommand(Intent intent, int flags, int startId) {

        Notification.Builder builder = new Notification.Builder(getApplicationContext());
        Intent appIntent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent appPendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, appIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder
                .setContentIntent(appPendingIntent)
                .setSmallIcon(R.drawable.ic_thunder_icon)
                .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.mipmap.ic_launcher))
                .setTicker("Дступны новые обновления погоды")
                .setWhen(System.currentTimeMillis()) // время уведомления - текущее
                .setAutoCancel(true) // для автоматического закрытия
                .setContentTitle("Sun&Rain")
                .setContentText("Нажмите чтобы обновить информацию о погоде. Будет показано только 10 уведомлений.");

        notification = builder.build();
        //notification.defaults = Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE;

        new Thread(new Runnable() {
            public void run() {
                for (int i = 1; i <= 10; i++) {
                    try {
                        nm.notify(NOTIFICATION_ID, notification);
                        Log.d(TAG, "Notification = " + i);
                        TimeUnit.SECONDS.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                stopSelf();
            }
        }).start();

        return Service.START_STICKY;
    }



    public void onDestroy() {
        Log.d(TAG, "NotifyService - onDestroy");
        nm.cancel(NOTIFICATION_ID);
        super.onDestroy();
        state = true;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "NotifyService - onBind");
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
