package com.example.guilherme.firebasedatabse.config;

public class Constants {
    public static final String PREFERENCES_NAME = "firebaseTeste.preferences";
    public static final String USER_NAME = "nome";
    public static final String USER_TOKEN = "token";
    public static final String USER_AVATAR = "avatar";
    public static final String EMAIL_TYPE = "message/rfc822";
    public static final String EMAIL_SENDER = "guilherme_fgl@hotmail.com";

    public static final int PICK_IMAGE_FOR_PROFILE = 1001;
    public static final int PICK_IMAGE_FOR_REGISTER = 1002;
    public static final int NOTIFICATION_MESSAGE_ID = 1003;
    public static final int RC_SIGN_IN = 1004;

    public static final String DEFAULT_IMAGE_EXTENSION = ".jpg";

    public static final String[] FACEBOOK_LOGIN = new String[] {"email", "public_profile"};

    public static final class DATABASE_NODES {
        public static final String USER = "users";
        public static final String TOKEN = "token";
        public static final String AVATAR = "avatars";
    }
}
