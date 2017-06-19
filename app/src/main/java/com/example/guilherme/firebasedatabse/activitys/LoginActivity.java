package com.example.guilherme.firebasedatabse.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.guilherme.firebasedatabse.R;
import com.example.guilherme.firebasedatabse.config.Firebase;
import com.example.guilherme.firebasedatabse.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.login_email)
    TextView emailTextView;
    @BindView(R.id.login_password)
    TextView passwordTextView;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        firebaseAuth = Firebase.getFirebaseAuth();
    }

    @OnClick(R.id.login_action_login)
    public void login(){
        User user = new User();
        user.setEmail(emailTextView.getText().toString());
        user.setPassword(passwordTextView.getText().toString());

        firebaseAuth.signInWithEmailAndPassword(
                user.getEmail(),
                user.getPassword()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if( task.isSuccessful() ){
                    goToMainActivity();
                }else{
                    Toast.makeText(
                            LoginActivity.this,
                            getString(R.string.error_login),
                            Toast.LENGTH_LONG ).show();
                }
            }
        });

    }

    @OnClick(R.id.login_action_register)
    public void goToRegisterActivity(){
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
    }

    @OnClick(R.id.login_forgot_password)
    public void goToForgotPasswordActivity() {
        startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
    }

    private void goToMainActivity(){
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }

}
