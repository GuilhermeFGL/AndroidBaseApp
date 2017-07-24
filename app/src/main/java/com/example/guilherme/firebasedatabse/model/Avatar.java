package com.example.guilherme.firebasedatabse.model;

import com.example.guilherme.firebasedatabse.config.Constants;
import com.example.guilherme.firebasedatabse.config.Firebase;
import com.google.firebase.database.Exclude;

public class Avatar {

    private String userId;
    private String avatarURL;

    public Avatar() { }

    public void save(){
        Firebase.getFirebaseDatabase()
                .child(Constants.DATABASE_NODES.AVATAR).child(getUserId()).setValue(this);
    }

    @Exclude
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAvatarURL() {
        return avatarURL;
    }

    public void setAvatarURL(String avatarURL) {
        this.avatarURL = avatarURL;
    }
}
