package com.example.guilherme.firebasedatabse.model;

import com.example.guilherme.firebasedatabse.config.Constants;
import com.example.guilherme.firebasedatabse.config.Firebase;
import com.google.firebase.database.Exclude;

/**
 * Created by guilherme.lima on 29/06/2017.
 */

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
