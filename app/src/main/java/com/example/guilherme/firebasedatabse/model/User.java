package com.example.guilherme.firebasedatabse.model;

import android.support.annotation.NonNull;

import com.example.guilherme.firebasedatabse.config.Constants;
import com.example.guilherme.firebasedatabse.config.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.Exclude;

import java.util.concurrent.Callable;

public class User {

    private String id;
    private String name;
    private String email;
    private String password;

    public User() { }

    public void save(){
        Firebase.getFirebaseDatabase()
                .child(Constants.DATABASE_NODES.USER).child(getId()).setValue(this);
        saveToken();
    }

    public void save(final Callable<Void> callback){
        Firebase.getFirebaseDatabase()
                .child(Constants.DATABASE_NODES.USER).child(getId()).setValue(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        saveToken();
                        try {
                            callback.call();
                        } catch (Exception ignored) { }
                    }
                });
    }

    public void saveToken() {
        Firebase.getFirebaseDatabase()
                .child(Constants.DATABASE_NODES.USER)
                .child(getId())
                .child(Constants.DATABASE_NODES.TOKEN)
                .setValue(Firebase.getFirebaseId().getToken());
    }

    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Exclude
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
