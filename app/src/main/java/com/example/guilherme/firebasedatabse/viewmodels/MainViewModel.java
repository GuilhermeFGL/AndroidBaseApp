package com.example.guilherme.firebasedatabse.viewmodels;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;

import com.example.guilherme.firebasedatabse.config.Firebase;
import com.google.firebase.auth.FirebaseUser;

public class MainViewModel extends ViewModel {

    private MutableLiveData<FirebaseUser> currentUser = new MutableLiveData<>();
    private MutableLiveData<Fragment> currentFragment = new MutableLiveData<>();
    private MutableLiveData<Bitmap> avatarBM = new MutableLiveData<>();

    public MainViewModel() {
        currentUser.setValue(Firebase.getFirebaseAuth().getCurrentUser());
        currentFragment.setValue(null);
    }

    public MutableLiveData<FirebaseUser> getUser() {
        return currentUser;
    }

    public void updateUser() {
        this.currentUser.setValue(Firebase.getFirebaseAuth().getCurrentUser());
    }

    public MutableLiveData<Fragment> getCurrentFragment() {
        return currentFragment;
    }

    public void setCurrentFragment(Fragment currentFragment) {
        this.currentFragment.setValue(currentFragment);
    }

    public void setAvatar(Bitmap avatarBM) {
        this.avatarBM.setValue(avatarBM);
    }

    public MutableLiveData<Bitmap> getAvatar() {
        return avatarBM;
    }
}
