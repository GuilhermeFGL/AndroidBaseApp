package com.example.guilherme.firebasedatabse.activitys;

import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;

import com.example.guilherme.firebasedatabse.config.Constants;
import com.example.guilherme.firebasedatabse.config.Firebase;
import com.example.guilherme.firebasedatabse.model.NavigationItem;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Firebase.getFirebaseAuth().getCurrentUser() == null) {
            LoginActivity.startActivity(this);
        } else {
            if (getIntent().getExtras() != null && !getIntent().getExtras().isEmpty()
                    && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                PersistableBundle persistableBundle = getIntent()
                        .getParcelableExtra(Constants.BUNDLES.MAIN.CURRENT_FRAGMENT);
                if (persistableBundle != null && !persistableBundle.isEmpty()) {
                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.BUNDLES.MAIN.CURRENT_FRAGMENT,
                            persistableBundle.getString(Constants.BUNDLES.MAIN.CURRENT_FRAGMENT));
                    MainActivity.startActivityWithBundle(this, bundle);
                } else {
                    MainActivity.startActivity(this);
                }
            } else {
                MainActivity.startActivity(this);
            }
        }
    }
}
