package com.example.guilherme.firebasedatabse.model;

import android.support.annotation.NonNull;

import com.example.guilherme.firebasedatabse.config.Constants;
import com.example.guilherme.firebasedatabse.config.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import java.util.concurrent.Callable;

public class User {

    private String id;
    private String name;
    private String email;
    private String password;

    public User() { }

    public void save(){
        Firebase.getFirebaseDatabse()
                .child(Constants.DATABASE_NODES.USER).child(getId()).setValue(this);
    }

    public void save(final Callable<Void> callback){
        Firebase.getFirebaseDatabse()
                .child(Constants.DATABASE_NODES.USER).child(getId()).setValue(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        try {
                            callback.call();
                        } catch (Exception ignored) { }
                    }
                });
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
