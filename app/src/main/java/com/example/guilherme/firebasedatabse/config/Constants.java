package com.example.guilherme.firebasedatabse.config;

import android.Manifest;

public class Constants {

    private Constants() {}

    public static final String PREFERENCES_NAME = "firebaseTeste.preferences";
    public static final String USER_NAME = "nome";
    public static final String USER_TOKEN = "token";
    public static final String USER_FIRST_TIME = "first";
    public static final String USER_AVATAR = "avatar";
    public static final String EMAIL_TYPE = "message/rfc822";
    public static final String EMAIL_SENDER = "guilherme_fgl@hotmail.com";
    public static final String NOTIFICATION_CHANNEL_MESSAGE_ID = "notification_message";
    public static final String NOTIFICATION_CHANNEL_DATA_ID = "notification_data";
    public static final String PIN_PREFERENCES = "pin_preferences";

    public static final int PICK_IMAGE_FOR_PROFILE = 1001;
    public static final int PICK_IMAGE_FOR_REGISTER = 1002;
    public static final int NOTIFICATION_MESSAGE_ID = 1003;
    public static final int RC_SIGN_IN = 1004;
    public static final int RC_READ = 1005;
    public static final int RC_SAVE = 1006;

    public static final String DEFAULT_IMAGE_EXTENSION = ".jpg";

    public static final String[] FACEBOOK_LOGIN = new String[] {"email", "public_profile"};

    public static final class PERMISSIONS {
        public static final int REQUEST_CODE_CAMERA = 1007;
        public static final String[] CAMERA = {
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};
    }

    public static final class BUNDLES {
        public static final class MAIN {
            public static final String CURRENT_FRAGMENT = "current_fragment";
        }
    }

    public static final class DATABASE_NODES {
        public static final String USER = "users";
        public static final String TOKEN = "token";
        public static final String AVATAR = "avatars";
    }
}
