package com.example.guilherme.firebasedatabse.config;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class FirebaseTest {

    @Test
    public void getFirebaseDatabase_verifyConnection() throws Exception {
        assertNotNull(Firebase.getFirebaseDatabase());
    }

    @Test
    public void getFirebaseAuth_verifyConnection() throws Exception {
        assertNotNull(Firebase.getFirebaseAuth());
    }

    @Test
    public void getStorageReference_verifyConnection() throws Exception {
        assertNotNull(Firebase.getStorageReference("6VeQdKsEvFOwNTRXWvHlxBn7BFB2.jpg"));
    }

    @Test
    public void isUserLoggedIn_verifyConnection() throws Exception {
        assertNotNull(Firebase.isUserLoggedIn());
    }
}
