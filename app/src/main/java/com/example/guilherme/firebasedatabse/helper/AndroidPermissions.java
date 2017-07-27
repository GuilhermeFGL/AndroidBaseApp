package com.example.guilherme.firebasedatabse.helper;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class AndroidPermissions {

    public static boolean checkPermissions(int requestCode, Activity activity, String[] requiredPermissions ){
        if(Build.VERSION.SDK_INT >= 23 ){
            List<String> permissionsList = new ArrayList<>();

            for(String permission : requiredPermissions ){
                if (!(ContextCompat.checkSelfPermission(activity, permission)
                        == PackageManager.PERMISSION_GRANTED)) {
                    permissionsList.add(permission);
                }
            }

            if( permissionsList.isEmpty() ) {
                return true;
            }

            String[] requestPermissions = new String[permissionsList.size()];
            permissionsList.toArray(requestPermissions);
            ActivityCompat.requestPermissions(activity, requestPermissions, requestCode);

            return false;
        }
        return  true;
    }

}
