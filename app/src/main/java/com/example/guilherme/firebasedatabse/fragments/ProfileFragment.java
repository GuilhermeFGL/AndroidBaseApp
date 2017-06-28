package com.example.guilherme.firebasedatabse.fragments;

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
import com.example.guilherme.firebasedatabse.helper.LocalPreferences;
import com.example.guilherme.firebasedatabse.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;
import java.util.concurrent.Callable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProfileFragment extends Fragment {

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle instance) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);

        firebaseUser = Firebase.getFirebaseAuth().getCurrentUser();
        dialog = new ProgressDialog(getActivity());
        setView();

        return view;
    }

    @OnClick(R.id.update_action_register)
    public void updateUser(){
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
            if (!passwordTextView.getText().toString().equals("")) {
                new AskPasswordDialog(getActivity()).show(new AskPasswordDialog.ActionCallback() {
                    @Override
                    public void onPositiveClick(String password) {
                        if (!password.equals("")) {
                            updateUserPassword(password);
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
                updateUserName();
            }
        }
    }

    private void setView() {
        HashMap<String, String> localUser = (new LocalPreferences(getActivity())).getUser();
        if (firebaseUser != null) {
            nameTextView.setText(localUser.get(Constants.USER_NAME));
            emailTextView.setText(firebaseUser.getEmail());
        }
    }

    private void updateUserPassword(String password) {
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
                                                    updateUserName();
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

    private void updateUserName() {
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

}
