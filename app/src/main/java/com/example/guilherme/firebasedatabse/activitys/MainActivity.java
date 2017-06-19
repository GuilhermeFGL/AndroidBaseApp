package com.example.guilherme.firebasedatabse.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.guilherme.firebasedatabse.R;
import com.example.guilherme.firebasedatabse.config.Constants;
import com.example.guilherme.firebasedatabse.config.Firebase;
import com.example.guilherme.firebasedatabse.helper.Preferences;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.main_user_name)
    TextView userTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        userTextView.setText(new Preferences().getUser().get(Constants.USER_NAME));

        verifyIsUserLogged();
    }

    private void verifyIsUserLogged() {
        if (Firebase.getFirebaseAuth().getCurrentUser() != null) {
            startActivity(new Intent(this, LoginActivity.class));
        }
    }
}
