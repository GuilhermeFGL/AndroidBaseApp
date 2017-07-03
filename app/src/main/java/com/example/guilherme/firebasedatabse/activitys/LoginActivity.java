package com.example.guilherme.firebasedatabse.activitys;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.example.guilherme.firebasedatabse.R;
import com.example.guilherme.firebasedatabse.components.ProgressDialog;
import com.example.guilherme.firebasedatabse.config.Constants;
import com.example.guilherme.firebasedatabse.config.Firebase;
import com.example.guilherme.firebasedatabse.helper.LocalPreferences;
import com.example.guilherme.firebasedatabse.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.login_email)
    TextView emailTextView;
    @BindView(R.id.login_password)
    TextView passwordTextView;

    private FirebaseAuth firebaseAuth;

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, LoginActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        firebaseAuth = Firebase.getFirebaseAuth();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (Firebase.getFirebaseAuth().getCurrentUser() != null) {
            goToMainActivity();
        }
    }

    @OnClick(R.id.login_action_login)
    public void login(){
        User user = new User();
        user.setEmail(emailTextView.getText().toString());
        user.setPassword(passwordTextView.getText().toString());

        if (!user.getEmail().equals("") && !user.getPassword().equals("")) {
            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.show(getString(R.string.dialog_wait));

            firebaseAuth.signInWithEmailAndPassword(
                    user.getEmail(),
                    user.getPassword()
            ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = task.getResult().getUser();
                        Firebase.getFirebaseDatabse().child(Constants.DATABASE_NODES.USER)
                                .child(firebaseUser.getUid()).addListenerForSingleValueEvent(
                                        new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        dialog.close();
                                        User user = dataSnapshot.getValue(User.class);
                                        (new LocalPreferences(getBaseContext()))
                                                .saveUser(user.getName(), user.getId());
                                        goToMainActivity();
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) { }
                                });
                    } else {
                        Toast.makeText(
                                LoginActivity.this,
                                getString(R.string.error_login),
                                Toast.LENGTH_LONG ).show();
                    }
                }
            });
        } else {
            Toast.makeText(
                    LoginActivity.this,
                    getString(R.string.error_login),
                    Toast.LENGTH_LONG ).show();
        }
    }

    @OnClick(R.id.login_action_register)
    public void goToRegisterActivity(){
        RegisterActivity.startActivity(LoginActivity.this);
    }

    @OnClick(R.id.login_forgot_password)
    public void goToForgotPasswordActivity() {
        ForgotPasswordActivity.startActivity(LoginActivity.this);
    }

    private void goToMainActivity(){
        MainActivity.startActivity(LoginActivity.this);
    }
}
