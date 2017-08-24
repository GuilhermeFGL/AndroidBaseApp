package com.example.guilherme.firebasedatabse;

import android.app.Application;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.PersistableBundle;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.ndk.CrashlyticsNdk;
import com.example.guilherme.firebasedatabse.activitys.MainActivity;
import com.example.guilherme.firebasedatabse.config.Constants;
import com.example.guilherme.firebasedatabse.config.Firebase;
import com.example.guilherme.firebasedatabse.model.NavigationItem;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;

import io.fabric.sdk.android.Fabric;

public class FireBaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if (!BuildConfig.DEBUG) {
            Fabric.with(this, new Crashlytics(), new CrashlyticsNdk());
        }

        setPicasso();
        updateShortcuts();
    }

    public void setPicasso() {
        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttpDownloader(this,Integer.MAX_VALUE));
        Picasso built = builder.build();
        built.setIndicatorsEnabled(true);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);
    }

    public void updateShortcuts() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ShortcutManager shortcutManager = getSystemService(ShortcutManager.class);
            if (Firebase.getFirebaseAuth().getCurrentUser() != null ) {
                Intent preferencesPin = new Intent(getApplicationContext(), MainActivity.class);
                PersistableBundle bundle = new PersistableBundle();

                bundle.putString(Constants.BUNDLES.MAIN.CURRENT_FRAGMENT, NavigationItem.PREFERENCES.name());
                preferencesPin.putExtra(Constants.BUNDLES.MAIN.CURRENT_FRAGMENT, bundle);
                preferencesPin.setAction(Intent.ACTION_MAIN);
                shortcutManager.setDynamicShortcuts(Collections.singletonList(
                        new ShortcutInfo.Builder(this, Constants.PIN_PREFERENCES)
                                .setShortLabel(getString(R.string.pin_preferences_shot_label))
                                .setLongLabel(getString(R.string.pin_preferences_long_label))
                                .setIcon(Icon.createWithResource(
                                        getApplicationContext(), R.drawable.ic_preferences))
                                .setIntent(preferencesPin)
                                .build()));
            } else {
                shortcutManager.removeDynamicShortcuts(new ArrayList<String>() {{
                    add(Constants.PIN_PREFERENCES);
                }});
            }
        }
    }
}
