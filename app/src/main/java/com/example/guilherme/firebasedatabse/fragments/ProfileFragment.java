package com.example.guilherme.firebasedatabse.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.guilherme.firebasedatabse.R;
import com.example.guilherme.firebasedatabse.activitys.MainActivity;
import com.example.guilherme.firebasedatabse.components.AskPasswordDialog;
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
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.concurrent.Callable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    @BindView(R.id.update_pic)
    CircleImageView avatarImageView;
    @BindView(R.id.update_name)
    TextView nameTextView;
    @BindView(R.id.update_email)
    TextView emailTextView;
    @BindView(R.id.update_password)
    TextView passwordTextView;
    @BindView(R.id.update_password_confirm)
    TextView passwordConfirmTextView;

    private FirebaseUser firebaseUser;
    private ProgressDialog dialog;
    private Bitmap avatarBitmap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle instance) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);

        firebaseUser = Firebase.getFirebaseAuth().getCurrentUser();
        dialog = new ProgressDialog(getActivity());
        avatarBitmap = null;
        setView();

        return view;
    }

    @OnClick(R.id.update_pic)
    public void pickImage() {
        Intent chooseImageIntent = ImagePicker.getPickImageIntent(getActivity());
        getActivity().startActivityForResult(chooseImageIntent, Constants.PICK_IMAGE_FOR_PROFILE);
    }

    @OnClick(R.id.update_action_register)
    public void updateProfile(){
        boolean isValid = true;
        if (nameTextView.getText().toString().equals("")) {
            nameTextView.setError(getString(R.string.error_empty_required));
            isValid = false;
        }
        if (!passwordTextView.getText().toString().equals("")
                && !passwordTextView.getText().toString().equals(
                passwordConfirmTextView.getText().toString())) {
            passwordTextView.setError(getString(R.string.error_empty_required));
            isValid = false;
        }

        if (isValid) {
            dialog.show(getString(R.string.dialog_wait));
            updateUserAvatar();
        }
    }

    private void setView() {
        HashMap<String, String> localUser = (new LocalPreferences(getActivity())).getUser();
        if (firebaseUser != null) {
            nameTextView.setText(localUser.get(Constants.USER_NAME));
            emailTextView.setText(firebaseUser.getEmail());

            Firebase.getFirebaseDatabse().child(Constants.DATABASE_NODES.AVATAR)
                    .child(firebaseUser.getUid()).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Avatar avatar = dataSnapshot.getValue(Avatar.class);
                            if (avatar != null
                                    && avatar.getAvatarURL() != null
                                    && !avatar.getAvatarURL().equals("")) {
                                Picasso.with(getActivity())
                                        .load(avatar.getAvatarURL())
                                        .fit()
                                        .centerCrop()
                                        .into(avatarImageView);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) { }
                    });
        }
    }

    private void updateUserAvatar() {
        if (avatarBitmap != null) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            avatarBitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);

            Firebase.getStorageReference(firebaseUser.getUid().concat(".jpg"))
                    .putBytes(outputStream.toByteArray())
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            requestFeedBack(R.string.error_update_generic);
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    if (taskSnapshot.getDownloadUrl() != null) {
                        Avatar avatar = new Avatar();
                        avatar.setUserId(firebaseUser.getUid());
                        avatar.setAvatarURL(taskSnapshot.getDownloadUrl().toString());
                        avatar.save();
                    }
                    updateUserPassword();
                }
            });
        } else {
            updateUserPassword();
        }
    }

    private void updateUserPassword() {
        if (!passwordTextView.getText().toString().equals("")) {
            new AskPasswordDialog(getActivity()).show(new AskPasswordDialog.ActionCallback() {
                @Override
                public void onPositiveClick(String password) {
                    if (!password.equals("")) {
                        requestUserPassword(password);
                    } else {
                        requestFeedBack(R.string.error_update_generic);
                    }
                }

                @Override
                public void onNegativeClick() {
                    dialog.close();
                }
            });
        } else {
            updateUser();
        }
    }

    private void requestUserPassword(String password) {
        AuthCredential authCredential = Firebase.getAuthCredential(password);
        if (authCredential != null) {
            firebaseUser.reauthenticate(authCredential).addOnCompleteListener(
                    new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                firebaseUser.updatePassword(passwordTextView.getText().toString())
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    updateUser();
                                                } else {
                                                    int errorMessage;
                                                    try {
                                                        throw task.getException();
                                                    } catch (FirebaseAuthWeakPasswordException e) {
                                                        errorMessage = R.string.error_register_weak_password;
                                                    } catch (Exception e) {
                                                        errorMessage = R.string.error_update_generic;
                                                        e.printStackTrace();
                                                    }
                                                    requestFeedBack(errorMessage);
                                                }
                                            }
                                        });
                            } else {
                                requestFeedBack(R.string.error_update_generic);
                            }
                        }
                    }
            );
        }
    }

    private void updateUser() {
        final String userName = nameTextView.getText().toString().trim();
        final String userEmail = emailTextView.getText().toString().trim();

        User updateUser = new User();
        updateUser.setId(firebaseUser.getUid());
        updateUser.setName(userName);
        updateUser.setEmail(userEmail);
        updateUser.save(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                (new LocalPreferences(getActivity())).saveUser(
                        userName, userEmail);

                requestFeedBack(R.string.success_profile_update);
                return null;
            }
        });
    }

    private void requestFeedBack(int message) {
        if (isAdded()) {
            ((MainActivity) getActivity()).setUser();
            Toast.makeText(getActivity(),
                    getString(message),
                    Toast.LENGTH_LONG ).show();
            if (dialog.isShowing()) {
                dialog.close();
            }
        }
    }

    public void setAvatar(Bitmap avatar) {
        avatarBitmap = avatar;
        avatarImageView.setImageBitmap(avatar);
    }

}
