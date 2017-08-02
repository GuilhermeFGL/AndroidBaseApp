package com.example.guilherme.firebasedatabse.components;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import org.json.JSONObject;
import org.junit.Test;

public class NotificationHandlerTest {

    private Context appContext = InstrumentationRegistry.getTargetContext();

    @Test
    public void testNotificationHandlerTest_showNotificationMessage_verifyExceptionsForSimpleMessageNotification() throws Exception {
        NotificationHandler notificationHandler = new NotificationHandler(appContext);
        notificationHandler.showNotificationMessage("Notification message");
    }

    @Test
    public void testNotificationHandlerTest_showNotificationData_verifyExceptionsForDataNotification() throws Exception {
        NotificationHandler notificationHandler = new NotificationHandler(appContext);
        notificationHandler.showNotificationData(new JSONObject("{\"data\": \"Notification data\"}"));
    }
}
