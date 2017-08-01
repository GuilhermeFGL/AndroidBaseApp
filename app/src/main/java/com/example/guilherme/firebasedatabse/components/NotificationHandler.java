package com.example.guilherme.firebasedatabse.components;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.example.guilherme.firebasedatabse.R;
import com.example.guilherme.firebasedatabse.config.Constants;
import com.example.guilherme.firebasedatabse.config.Firebase;
import com.example.guilherme.firebasedatabse.helper.LocalPreferences;

import org.json.JSONObject;

import java.util.List;

public class NotificationHandler {

    private Context context;
    private LocalPreferences preferences;

    public NotificationHandler(Context mContext) {
        this.context = mContext;
        preferences = new LocalPreferences(context);
    }

    public void showNotificationMessage(final String message) {
        if (preferences.getNotificationPreferences()) {
            NotificationManager notificationManager =
                    ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE));
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel notificationChannel =
                        new NotificationChannel(
                                Constants.NOTIFICATION_CHANNEL_MESSAGE_ID,
                                context.getString(R.string.notification_channel_message_title),
                                NotificationManager.IMPORTANCE_DEFAULT);
                notificationChannel.setDescription(message);
                notificationChannel.setShowBadge(true);
                notificationChannel.enableLights(true);
                notificationChannel.enableVibration(preferences.getVibratePreferences());
                notificationChannel.setLightColor(ContextCompat.getColor(context, R.color.colorPrimary));
                notificationManager.createNotificationChannel(notificationChannel);
            }
            notificationManager.notify(Constants.NOTIFICATION_MESSAGE_ID, buildBaseNotification()
                            .setContentText(message)
                            .build());
        }
    }

    public void showNotificationData(JSONObject data) {
        if (Firebase.isUserLoggedIn() && preferences.getNotificationPreferences()) {
            NotificationManager notificationManager =
                    ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE));
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel notificationChannel =
                        new NotificationChannel(
                                Constants.NOTIFICATION_CHANNEL_DATA_ID,
                                context.getString(R.string.notification_channel_data_title),
                                NotificationManager.IMPORTANCE_HIGH);
                notificationChannel.setDescription(data.toString());
                notificationChannel.setShowBadge(true);
                notificationChannel.enableLights(true);
                notificationChannel.enableVibration(preferences.getVibratePreferences());
                notificationChannel.setLightColor(ContextCompat.getColor(context, R.color.colorPrimary));
                notificationManager.createNotificationChannel(notificationChannel);
            }
            notificationManager.notify(0, buildBaseNotification()
                    .setContentText(data.toString())
                    .build());
        }
    }

    private NotificationCompat.Builder buildBaseNotification() {
        NotificationCompat.Builder notification = new NotificationCompat.Builder(context)
                .setContentTitle(context.getString(R.string.app_name))
                .setSound(Uri.parse(preferences.getRingtonePreferences()))
                .setStyle(new NotificationCompat.InboxStyle())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                .setLights(ContextCompat.getColor(context, R.color.colorPrimary), 1, 1);
        if (preferences.getVibratePreferences()) {
            notification.setDefaults(Notification.DEFAULT_VIBRATE);
        }

        return notification;
    }

    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }
}
