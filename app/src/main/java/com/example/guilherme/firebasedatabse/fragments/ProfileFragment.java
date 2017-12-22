package com.example.guilherme.firebasedatabse.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.example.guilherme.firebasedatabse.helper.AndroidPermissions;
import com.example.guilherme.firebasedatabse.helper.ImagePicker;
import com.example.guilherme.firebasedatabse.helper.LocalPreferences;
import com.example.guilherme.firebasedatabse.model.Avatar;
import com.example.guilherme.firebasedatabse.model.User;
import com.example.guilherme.firebasedatabse.viewmodels.ProfileViewModel;
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
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;
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

    private ProgressDialog dialog;
    private ProfileViewModel mViewModel;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);
        mViewModel.getUser().observe(this, new Observer<FirebaseUser>() {
            @Override
            public void onChanged(@Nullable FirebaseUser firebaseUser) {
                setUserView(firebaseUser);
            }
        });
        mViewModel.getAvatarBitmap().observe(this, new Observer<Bitmap>() {
            @Override
            public void onChanged(@Nullable Bitmap bitmap) {
                setAvatarView(bitmap);
            }
        });
        loadAvatarFromStorage();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle instance) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);
        if (getActivity() != null) {
            dialog = new ProgressDialog(getActivity());
            setHasOptionsMenu(true);
        }
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.profile_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_profile_save:
                updateProfile();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @OnClick(R.id.update_pic)
    public void pickImage() {
        if (getActivity() != null
                && !AndroidPermissions.checkPermissions(getActivity(), Constants.PERMISSIONS.CAMERA)) {
            getActivity().startActivityForResult(
                    ImagePicker.getPickImageIntent(getActivity()), Constants.PICK_IMAGE_FOR_PROFILE);
        } else {
            AndroidPermissions.requestPermission(
                    Constants.PERMISSIONS.REQUEST_CODE_CAMERA,
                    getActivity(),
                    Constants.PERMISSIONS.CAMERA);
        }
    }

    private void setUserView(FirebaseUser firebaseUser) {
        if (firebaseUser != null) {
            nameTextView.setText(firebaseUser.getDisplayName());
            emailTextView.setText(firebaseUser.getEmail());
        }
    }

    public void setAvatarView(Bitmap avatar) {
        if (avatar != null) {
            avatarImageView.setImageBitmap(avatar);
        }
    }

    private void loadAvatarFromStorage() {
        final String avatarUrl =
                (new LocalPreferences(getContext())).getUser().get(Constants.USER_AVATAR);
        if (avatarUrl != null && !avatarUrl.equals("")) {
            getAvatarBitmap(avatarUrl);
        } else {
            FirebaseUser firebaseUser = mViewModel.getUser().getValue();
            if (firebaseUser != null) {
                Firebase.getFirebaseDatabase().child(Constants.DATABASE_NODES.AVATAR)
                        .child(firebaseUser.getUid()).addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Avatar avatar = dataSnapshot.getValue(Avatar.class);
                                if (avatar != null
                                        && avatar.getAvatarURL() != null
                                        && !avatar.getAvatarURL().equals("")) {
                                    getAvatarBitmap(avatar.getAvatarURL());
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
            }
        }
    }

    private void getAvatarBitmap(String url) {
        Picasso.with(getActivity())
                .load(url)
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        setAvatar(bitmap);
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) { }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) { }
                });
    }

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

    private void updateUserAvatar() {
        final FirebaseUser firebaseUser = mViewModel.getUser().getValue();
        Bitmap avatarBitmap = mViewModel.getAvatarBitmap().getValue();
        if (firebaseUser != null && avatarBitmap != null) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            avatarBitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);

            Firebase.getStorageReference(
                    firebaseUser.getUid().concat(Constants.DEFAULT_IMAGE_EXTENSION))
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
        if (getActivity() != null && !passwordTextView.getText().toString().equals("") && isAdded()) {
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
        final FirebaseUser firebaseUser = mViewModel.getUser().getValue();
        AuthCredential authCredential = Firebase.getAuthCredential(password);
        if (firebaseUser != null && authCredential != null) {
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
                                                        throw task.getException() != null ?
                                                                task.getException() :
                                                                new Exception();
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
        FirebaseUser firebaseUser = mViewModel.getUser().getValue();
        if (firebaseUser != null) {
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
    }

    private void requestFeedBack(int message) {
        if (isAdded() && getActivity() != null) {
            ((MainActivity) getActivity()).requestUpdateUser();
            Toast.makeText(getActivity(),
                    getString(message),
                    Toast.LENGTH_LONG).show();
            if (dialog.isShowing()) {
                dialog.close();
            }
        }
    }

    public void setAvatar(Bitmap avatar) {
        mViewModel.setAvatarBitmap(avatar);
    }
}
