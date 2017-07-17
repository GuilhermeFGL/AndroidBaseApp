package com.example.guilherme.firebasedatabse.config;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public final class Firebase {

    private static DatabaseReference databaseReference;
    private static FirebaseAuth firebaseAuth;

    public static DatabaseReference getFirebaseDatabase(){
        if( databaseReference == null ){
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            databaseReference =  FirebaseDatabase.getInstance().getReference();
        }
        return databaseReference;
    }

    public static FirebaseAuth getFirebaseAuth(){
        if( firebaseAuth == null ){
            firebaseAuth = FirebaseAuth.getInstance();
        }
        return firebaseAuth;
    }

    public static boolean isUserLoggedIn() {
        return getFirebaseAuth().getCurrentUser() != null;
    }

    public static AuthCredential getAuthCredential(String password) {
        FirebaseUser firebaseUser = getFirebaseAuth().getCurrentUser();
        if (firebaseUser != null && firebaseUser.getEmail() != null) {
            return EmailAuthProvider
                    .getCredential(firebaseUser.getEmail(), password);
        } else {
            return null;
        }
    }

    public static StorageReference getStorageReference(String name) {
        return FirebaseStorage.getInstance().getReference(name);
    }

    public static FirebaseInstanceId getFirebaseId() {
        return FirebaseInstanceId.getInstance();
    }

}
