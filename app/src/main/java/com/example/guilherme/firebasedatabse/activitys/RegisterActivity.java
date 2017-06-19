package com.example.guilherme.firebasedatabse.activitys;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.example.guilherme.firebasedatabse.R;
import com.example.guilherme.firebasedatabse.config.Firebase;
import com.example.guilherme.firebasedatabse.helper.Preferences;
import com.example.guilherme.firebasedatabse.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.register_name)
    TextView nameTextView;
    @BindView(R.id.register_email)
    TextView emailTextView;
    @BindView(R.id.register_email_confirm)
    TextView emailConfirmTextView;
    @BindView(R.id.register_password)
    TextView passwordTextView;
    @BindView(R.id.register_password_confirm)
    TextView passwordConfirmTextView;

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    @OnClick(R.id.register_action_register)
    public void cadastrarUsuario(){
        if (!emailTextView.getText().toString().equals(
                emailConfirmTextView.getText().toString())) {
            Toast.makeText(RegisterActivity.this,
                    getString(R.string.error_register_confirm_email),
                    Toast.LENGTH_LONG ).show();
        } else if (!passwordTextView.getText().toString().equals(
                passwordConfirmTextView.getText().toString())) {
            Toast.makeText(RegisterActivity.this,
                    getString(R.string.error_register_confirm_password),
                    Toast.LENGTH_LONG ).show();
        } else {
            user = new User();
            user.setName(nameTextView.getText().toString() );
            user.setEmail(emailTextView.getText().toString());
            user.setPassword(passwordTextView.getText().toString());

            Firebase.getFirebaseAuth().createUserWithEmailAndPassword(
                    user.getEmail(), user.getPassword())
                    .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if( task.isSuccessful() ){

                        Toast.makeText(RegisterActivity.this,
                                getString(R.string.success_register),
                                Toast.LENGTH_LONG ).show();

                        FirebaseUser firebaseUser = task.getResult().getUser();
                        user.setId( firebaseUser.getUid() );
                        user.salvar();
                        new Preferences().saveUser(user.getName(), user.getId());
                        finish();

                    } else {

                        int errorMessage;
                        try{
                            throw task.getException();
                        } catch (FirebaseAuthWeakPasswordException e) {
                            errorMessage = R.string.error_register_weak_password;
                        } catch (FirebaseAuthInvalidCredentialsException
                                | FirebaseAuthUserCollisionException e) {
                            errorMessage = R.string.error_register_duplicate_email;
                        } catch (Exception e) {
                            errorMessage = R.string.error_register_generic;
                            e.printStackTrace();
                        }
                        Toast.makeText(RegisterActivity.this,
                                getString(errorMessage),
                                Toast.LENGTH_LONG ).show();
                    }
                }
            });
        }
    }

}
