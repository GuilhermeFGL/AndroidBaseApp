package com.example.guilherme.firebasedatabse.services;

import com.example.guilherme.firebasedatabse.config.Firebase;
import com.example.guilherme.firebasedatabse.model.User;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class FirebaseRegisterService  extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        final FirebaseUser firebaseUser = Firebase.getFirebaseAuth().getCurrentUser();
        if (firebaseUser != null){
            new User() {{
                setId(firebaseUser.getUid());
            }}.saveToken();
        }
    }
}