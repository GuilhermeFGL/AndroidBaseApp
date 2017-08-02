package com.example.guilherme.firebasedatabse.helper;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.example.guilherme.firebasedatabse.config.Constants;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class LocalPreferencesTest {

    private Context appContext = InstrumentationRegistry.getTargetContext();
    private LocalPreferences preferences = new LocalPreferences(appContext);

    @Test
    public void getNotificationPreferences_assertDefaultValues() throws Exception {
        assertEquals(true, preferences.getNotificationPreferences());
    }

    @Test
    public void getVibratePreferences_assertDefaultValues() throws Exception {
        assertEquals(true, preferences.getVibratePreferences());
    }

    @Test
    public void getRingtonePreferences_assertDefaultValues() throws Exception {
        assertEquals("DEFAULT_SOUND", preferences.getRingtonePreferences());
    }

    @Test
    public void saveUser_saveAvatar_verifySavedValues() throws Exception {
        preferences.saveUser("Nome", "email");
        preferences.saveAvatar("URL Avatar");

        HashMap<String, String> user = preferences.getUser();
        assertEquals("Nome", user.get(Constants.USER_NAME));
        assertEquals("email", user.get(Constants.USER_TOKEN));
        assertEquals("URL Avatar", user.get(Constants.USER_AVATAR));
    }

    @Test
    public void logoutUser_verifyRemovedValues() throws Exception {
        preferences.logoutUser();

        HashMap<String, String> user = preferences.getUser();
        assertEquals(null, user.get(Constants.USER_NAME));
        assertEquals(null, user.get(Constants.USER_TOKEN));
        assertEquals(null, user.get(Constants.USER_AVATAR));
    }
}
