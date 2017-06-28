package com.example.guilherme.firebasedatabse.activitys;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.guilherme.firebasedatabse.R;
import com.example.guilherme.firebasedatabse.components.ProgressDialog;
import com.example.guilherme.firebasedatabse.config.Firebase;
import com.example.guilherme.firebasedatabse.helper.LocalPreferences;
import com.example.guilherme.firebasedatabse.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @OnClick(R.id.register_action_register)
    public void registerUser(){
        boolean isValid = true;
        if (nameTextView.getText().toString().equals("")) {
            nameTextView.setError(getString(R.string.error_empty_required));
            isValid = false;
        }
        if (emailTextView.getText().toString().equals("")) {
            emailTextView.setError(getString(R.string.error_empty_required));
            isValid = false;
        }
        if (passwordTextView.getText().toString().equals("")) {
            passwordTextView.setError(getString(R.string.error_empty_required));
            isValid = false;
        }
        if (!emailTextView.getText().toString().equals(
                emailConfirmTextView.getText().toString())) {
            emailConfirmTextView.setError(getString(R.string.error_register_confirm_email));
            isValid = false;
        }
        if (!passwordTextView.getText().toString().equals(
                passwordConfirmTextView.getText().toString())) {
            passwordConfirmTextView.setError(getString(R.string.error_register_confirm_password));
            isValid = false;
        }

        if (isValid) {

            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.show(getString(R.string.dialog_wait));

            user = new User();
            user.setName(nameTextView.getText().toString().trim());
            user.setEmail(emailTextView.getText().toString().trim());
            user.setPassword(passwordTextView.getText().toString());

            Firebase.getFirebaseAuth().createUserWithEmailAndPassword(
                    user.getEmail(), user.getPassword())
                    .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    dialog.close();

                    if( task.isSuccessful() ){

                        Toast.makeText(RegisterActivity.this,
                                getString(R.string.success_register),
                                Toast.LENGTH_LONG ).show();

                        FirebaseUser firebaseUser = task.getResult().getUser();
                        user.setId(firebaseUser.getUid());
                        user.save();
                        (new LocalPreferences(getBaseContext()))
                                .saveUser(user.getName(), user.getId());
                        finish();

                    } else {

                        int errorMessage;
                        try{
                            throw task.getException();
                        } catch (FirebaseAuthWeakPasswordException e) {
                            errorMessage = R.string.error_register_weak_password;
                        } catch (FirebaseAuthInvalidCredentialsException e) {
                            errorMessage = R.string.error_register_invalid_email;
                        } catch (FirebaseAuthUserCollisionException e) {
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
