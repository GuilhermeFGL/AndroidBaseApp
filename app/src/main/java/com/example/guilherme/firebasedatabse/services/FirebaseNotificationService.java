package com.example.guilherme.firebasedatabse.services;

import com.example.guilherme.firebasedatabse.components.NotificationHandler;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

public class FirebaseNotificationService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage != null) {
            if (remoteMessage.getNotification() != null) {
                new NotificationHandler(getBaseContext())
                        .showNotificationMessage(remoteMessage.getNotification().getBody());
            }

            if (remoteMessage.getData().size() > 0) {
                try {
                    new NotificationHandler(getBaseContext())
                            .showNotificationData(
                                    new JSONObject(remoteMessage.getData().toString())
                                            .getJSONObject("data"));
                } catch (Exception ignored) { }
            }
        }
    }
}