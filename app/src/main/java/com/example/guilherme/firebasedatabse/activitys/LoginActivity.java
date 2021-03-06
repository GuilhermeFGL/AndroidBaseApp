package com.example.guilherme.firebasedatabse.activitys;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.example.guilherme.firebasedatabse.FireBaseApplication;
import com.example.guilherme.firebasedatabse.R;
import com.example.guilherme.firebasedatabse.components.ProgressDialog;
import com.example.guilherme.firebasedatabse.config.Constants;
import com.example.guilherme.firebasedatabse.config.Firebase;
import com.example.guilherme.firebasedatabse.helper.LocalPreferences;
import com.example.guilherme.firebasedatabse.model.Avatar;
import com.example.guilherme.firebasedatabse.model.User;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.CredentialRequest;
import com.google.android.gms.auth.api.credentials.CredentialRequestResult;
import com.google.android.gms.auth.api.credentials.IdentityProviders;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
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
    @BindView(R.id.login_facebook)
    LoginButton facebookButton;

    private FirebaseAuth firebaseAuth;
    private CallbackManager facebookCallback;
    private GoogleApiClient mSignInClient;
    private GoogleApiClient mCredentialsClient;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, LoginActivity.class));
        ((Activity) context).finish();
        ((Activity) context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        ((FireBaseApplication) ((Activity) context).getApplication()).updateShortcuts();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        firebaseAuth = Firebase.getFirebaseAuth();
        facebookCallback = CallbackManager.Factory.create();

        setView();
    }

    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (Firebase.getFirebaseAuth().getCurrentUser() != null) {
            goToMainActivity();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                firebaseAuthWithGoogle(result.getSignInAccount());
            } else {
                Toast.makeText(LoginActivity.this,
                        R.string.error_login_failed,
                        Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == Constants.RC_READ) {
            if (resultCode == RESULT_OK) {
                onCredentialRetrieved((Credential) data.getParcelableExtra(Credential.EXTRA_KEY));
            } else {
                Toast.makeText(LoginActivity.this,
                        R.string.error_login_failed,
                        Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode != Constants.RC_SAVE) {
            facebookCallback.onActivityResult(requestCode, resultCode, data);
        }
    }

    @OnClick(R.id.login_action_login)
    public void login(){
        User user = new User();
        user.setEmail(emailTextView.getText().toString());
        user.setPassword(passwordTextView.getText().toString());
        loginWithAccount(user);
    }

    @OnClick(R.id.login_google)
    public void loginGoogle() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mSignInClient);
        startActivityForResult(signInIntent, Constants.RC_SIGN_IN);
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

    private void setView() {
        // google login
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    registerNewUserFromSocialLogin(user);
                }
            }
        };
        mSignInClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(LoginActivity.this,
                                R.string.error_login_failed,
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // smart lock
        mCredentialsClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 1, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(LoginActivity.this,
                                R.string.error_login_failed,
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .addApi(Auth.CREDENTIALS_API)
                .build();
        CredentialRequest mCredentialRequest = new CredentialRequest.Builder()
                .setPasswordLoginSupported(true)
                .setAccountTypes(IdentityProviders.GOOGLE, IdentityProviders.TWITTER)
                .build();

        if (! new LocalPreferences(getApplicationContext()).getUserFirstTime()) {
            Auth.CredentialsApi.request(mCredentialsClient, mCredentialRequest).setResultCallback(
                    new ResultCallback<CredentialRequestResult>() {
                        @Override
                        public void onResult(@NonNull CredentialRequestResult credentialRequestResult) {
                            if (credentialRequestResult.getStatus().isSuccess()) {
                                onCredentialRetrieved(credentialRequestResult.getCredential());
                            } else {
                                resolveResult(credentialRequestResult.getStatus());
                            }
                        }
                    });
        }

        // facebook login
        facebookButton.setReadPermissions(Constants.FACEBOOK_LOGIN);
        facebookButton.registerCallback(facebookCallback, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() { }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(LoginActivity.this,
                        R.string.error_login_failed,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onCredentialRetrieved(Credential credential) {
        String accountType = credential.getAccountType();
        if (accountType == null) {
            User user = new User();
            user.setEmail(credential.getId());
            user.setPassword(credential.getPassword());
            loginWithAccount(user);
        } else if (accountType.equals(IdentityProviders.GOOGLE)) {
            GoogleSignInOptions gso =
                    new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken(getString(R.string.default_web_client_id))
                            .requestEmail()
                            .setAccountName(credential.getId())
                            .build();
            GoogleApiClient signInClient = new GoogleApiClient.Builder(this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .enableAutoManage(this, 2, new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                            Toast.makeText(LoginActivity.this,
                                    R.string.error_login_failed,
                                    Toast.LENGTH_SHORT).show();
                        }
                    })
                    .build();
            OptionalPendingResult<GoogleSignInResult> pendingResult =
                    Auth.GoogleSignInApi.silentSignIn(signInClient);
            if (pendingResult.isDone()) {
                firebaseAuthWithGoogle(pendingResult.get().getSignInAccount());
            } else {
                pendingResult.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                    @Override
                    public void onResult(@NonNull GoogleSignInResult googleSignInResult) {
                        firebaseAuthWithGoogle(googleSignInResult.getSignInAccount());
                    }
                });
            }
        }
    }

    private void resolveResult(Status status) {
        if (status.getStatusCode() == CommonStatusCodes.RESOLUTION_REQUIRED) {
            try {
                status.startResolutionForResult(this, Constants.RC_READ);
            } catch (IntentSender.SendIntentException e) {
                Toast.makeText(LoginActivity.this,
                        R.string.error_login_failed,
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.show(getString(R.string.dialog_wait));
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        dialog.close();
                        saveUserCredentials(firebaseAuth.getCurrentUser());
                        if (task.isSuccessful()) {
                            registerNewUserFromSocialLogin(firebaseAuth.getCurrentUser());
                        } else {
                            LoginManager.getInstance().logOut();
                            Toast.makeText(LoginActivity.this,
                                    R.string.error_login,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.show(getString(R.string.dialog_wait));
        firebaseAuth.signInWithCredential(FacebookAuthProvider.getCredential(token.getToken()))
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        dialog.close();
                        if (task.isSuccessful()) {
                            registerNewUserFromSocialLogin(firebaseAuth.getCurrentUser());
                        } else {
                            dialog.close();
                            LoginManager.getInstance().logOut();
                            Toast.makeText(LoginActivity.this,
                                    R.string.error_login,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void loginWithAccount(User user) {
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
                        final FirebaseUser firebaseUser = task.getResult().getUser();
                        Firebase.getFirebaseDatabase().child(Constants.DATABASE_NODES.USER)
                                .child(firebaseUser.getUid()).addListenerForSingleValueEvent(
                                new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        dialog.close();
                                        User user = dataSnapshot.getValue(User.class);
                                        if (user != null) {
                                            user.setId(firebaseUser.getUid());
                                            user.saveToken();
                                            (new LocalPreferences(getBaseContext()))
                                                    .saveUser(user.getName(), user.getId());
                                            goToMainActivity();
                                        } else {
                                            Toast.makeText(
                                                    LoginActivity.this,
                                                    getString(R.string.error_login),
                                                    Toast.LENGTH_LONG ).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) { }
                                });
                    } else {
                        dialog.close();
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

    private void saveUserCredentials(FirebaseUser firebaseUser) {
        Credential credential = new Credential.Builder(firebaseUser.getEmail())
                .setAccountType(IdentityProviders.GOOGLE)
                .setName(firebaseUser.getDisplayName())
                .setProfilePictureUri(firebaseUser.getPhotoUrl())
                .build();
        Auth.CredentialsApi.save(mCredentialsClient, credential).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                if (!status.isSuccess() && status.hasResolution()) {
                    try {
                        status.startResolutionForResult(LoginActivity.this, Constants.RC_SAVE);
                    } catch (IntentSender.SendIntentException ignored) { }
                }
            }
        });
    }

    private void registerNewUserFromSocialLogin(FirebaseUser firebaseUser) {
        User user = new User();
        user.setId(firebaseUser.getUid());
        user.setName(firebaseUser.getDisplayName());
        user.setEmail(firebaseUser.getEmail());
        user.save();

        if (firebaseUser.getPhotoUrl() != null) {
            Avatar avatar = new Avatar();
            avatar.setUserId(firebaseUser.getUid());
            avatar.setAvatarURL(firebaseUser.getPhotoUrl().toString());
            avatar.save();
        }

        (new LocalPreferences(getBaseContext()))
                .saveUser(user.getName(), user.getId());

        goToMainActivity();
    }
}
