package com.example.guilherme.firebasedatabse.viewmodels;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.graphics.Bitmap;

import com.example.guilherme.firebasedatabse.config.Firebase;
import com.google.firebase.auth.FirebaseUser;

public class ProfileViewModel extends ViewModel {

    private MutableLiveData<FirebaseUser> firebaseUser = new MutableLiveData<>();
    private MutableLiveData<Bitmap> avatarBitmap = new MutableLiveData<>();

    ProfileViewModel() {
        firebaseUser.setValue(Firebase.getFirebaseAuth().getCurrentUser());
        avatarBitmap.setValue(null);
    }

    public MutableLiveData<FirebaseUser> getUser() {
        return firebaseUser;
    }

    public MutableLiveData<Bitmap> getAvatarBitmap() {
        return avatarBitmap;
    }

    public void setAvatarBitmap(Bitmap avatarBitmap) {
        this.avatarBitmap.setValue(avatarBitmap);
    }


}
