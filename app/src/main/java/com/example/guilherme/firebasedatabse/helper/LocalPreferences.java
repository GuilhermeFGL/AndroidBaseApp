package com.example.guilherme.firebasedatabse.helper;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.guilherme.firebasedatabse.config.Constants;

import java.util.HashMap;

public class LocalPreferences {

    private Context context;
    private final int MODE = 0;
    private SharedPreferences preferences;

    public LocalPreferences(Context context){
        this.context = context;
        preferences = this.context.getSharedPreferences(Constants.PREFERENCES_NAME, MODE);
    }

    public void saveUser(String nome, String token){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Constants.USER_NAME, nome);
        editor.putString(Constants.USER_TOKEN, token);
        editor.apply();
    }

    public void saveAvatar(String avatarUrl){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Constants.USER_AVATAR, avatarUrl);
        editor.apply();
    }

    public void logoutUser(){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Constants.USER_NAME, null);
        editor.putString(Constants.USER_TOKEN, null);
        editor.putString(Constants.USER_AVATAR, null);
        editor.apply();
    }

    public HashMap<String, String> getUser(){
        HashMap<String, String> dadosUsuario = new HashMap<>();
        dadosUsuario.put(Constants.USER_NAME,
                preferences.getString(Constants.USER_NAME, null) );
        dadosUsuario.put(Constants.USER_TOKEN,
                preferences.getString(Constants.USER_TOKEN, null));
        dadosUsuario.put(Constants.USER_AVATAR,
                preferences.getString(Constants.USER_AVATAR, null));
        return dadosUsuario;
    }

}
