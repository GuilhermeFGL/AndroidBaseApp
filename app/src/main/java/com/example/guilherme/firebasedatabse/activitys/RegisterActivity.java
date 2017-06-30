package com.example.guilherme.firebasedatabse.activitys;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.guilherme.firebasedatabse.R;
import com.example.guilherme.firebasedatabse.components.ProgressDialog;
import com.example.guilherme.firebasedatabse.config.Constants;
import com.example.guilherme.firebasedatabse.config.Firebase;
import com.example.guilherme.firebasedatabse.helper.ImagePicker;
import com.example.guilherme.firebasedatabse.helper.LocalPreferences;
import com.example.guilherme.firebasedatabse.model.Avatar;
import com.example.guilherme.firebasedatabse.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.register_pic)
    CircleImageView avatarImageView;
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

    private User user;
    private Bitmap avatarBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        avatarBitmap = null;
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constants.PICK_IMAGE_FOR_REGISTER:
                    setAvatar(ImagePicker.getImageFromResult(this, resultCode, data));
                    break;
                default:
                    super.onActivityResult(requestCode, resultCode, data);
                    break;
            }
        }
    }

    @OnClick(R.id.register_pic)
    public void pickImage() {
        Intent chooseImageIntent = ImagePicker.getPickImageIntent(this);
        startActivityForResult(chooseImageIntent, Constants.PICK_IMAGE_FOR_REGISTER);
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

                    if( task.isSuccessful() ){
                        Toast.makeText(RegisterActivity.this,
                                getString(R.string.success_register),
                                Toast.LENGTH_LONG ).show();

                        final FirebaseUser firebaseUser = task.getResult().getUser();
                        user.setId(firebaseUser.getUid());
                        user.save();
                        (new LocalPreferences(getBaseContext()))
                                .saveUser(user.getName(), user.getId());

                        if (avatarBitmap != null) {

                            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                            avatarBitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);

                            Firebase.getStorageReference(
                                    firebaseUser.getUid().concat(Constants.DEFAULT_IMAGE_EXTENSION))
                                    .putBytes(outputStream.toByteArray())
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception exception) {
                                            dialog.close();
                                            finish();
                                        }
                                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    dialog.close();
                                    finish();
                                    if (taskSnapshot.getDownloadUrl() != null) {
                                        Avatar avatar = new Avatar();
                                        avatar.setUserId(firebaseUser.getUid());
                                        avatar.setAvatarURL(taskSnapshot.getDownloadUrl().toString());
                                        avatar.save();
                                    }
                                }
                            });
                        } else {
                            dialog.close();
                            finish();
                        }

                    } else {

                        dialog.close();
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

    public void setAvatar(Bitmap avatar) {
        avatarBitmap = avatar;
        avatarImageView.setImageBitmap(avatar);
    }

}
