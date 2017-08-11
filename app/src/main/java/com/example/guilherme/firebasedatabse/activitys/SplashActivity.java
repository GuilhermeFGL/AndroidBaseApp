package com.example.guilherme.firebasedatabse.activitys;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.guilherme.firebasedatabse.config.Firebase;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Firebase.getFirebaseAuth().getCurrentUser() == null) {
            LoginActivity.startActivity(this);
        } else {
            MainActivity.startActivity(this);
        }
    }
}
