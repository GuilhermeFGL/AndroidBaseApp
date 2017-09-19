package com.example.guilherme.firebasedatabse.activitys;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.guilherme.firebasedatabse.FireBaseApplication;
import com.example.guilherme.firebasedatabse.R;
import com.example.guilherme.firebasedatabse.adapters.NavigationAdapter;
import com.example.guilherme.firebasedatabse.config.Constants;
import com.example.guilherme.firebasedatabse.config.Firebase;
import com.example.guilherme.firebasedatabse.fragments.HomeFragment;
import com.example.guilherme.firebasedatabse.fragments.ProfileFragment;
import com.example.guilherme.firebasedatabse.helper.ImagePicker;
import com.example.guilherme.firebasedatabse.helper.LocalPreferences;
import com.example.guilherme.firebasedatabse.model.Avatar;
import com.example.guilherme.firebasedatabse.model.NavigationItem;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.navigation_list)
    ListView mMenuListView;
    @BindView(R.id.toolbar_main)
    Toolbar mToolbar;
    @BindView(R.id.navigation_menu)
    LinearLayout navigationMenu;
    @BindView(R.id.navigation_user_pic)
    CircleImageView avatarImageView;
    @BindView(R.id.navigation_user_name)
    TextView userName;
    @BindView(R.id.navigation_user_email)
    TextView userEmail;

    private NavigationAdapter navigationAdapter;
    private FirebaseUser currentUser;
    private Fragment currentFragment;

    public static void startActivity(Context context) {
        startActivity(context, new Intent(context, MainActivity.class));
    }

    public static void startActivityWithBundle(Context context, Bundle bundle) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtras(bundle);
        startActivity(context, intent);
    }

    private static void startActivity(Context context, Intent intent) {
        context.startActivity(intent);
        ((Activity) context).finish();
        ((Activity) context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        ((FireBaseApplication) ((Activity) context).getApplication()).updateShortcuts();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        NavigationItem currentFragment = NavigationItem.HOME;
        if (getIntent().getExtras() != null && !getIntent().getExtras().isEmpty()) {
            currentFragment = NavigationItem.valueOf(
                    getIntent().getExtras().getString(Constants.BUNDLES.MAIN.CURRENT_FRAGMENT));
        }

        setNavigationDrawer();
        setAvatar();
        if (currentFragment == NavigationItem.PREFERENCES) {
            openFragment(R.string.navigation_home, new HomeFragment());
        }
        openMenu(currentFragment);
    }

    @Override
    public void onResume() {
        super.onResume();

        verifyIsUserLogged();
        setUser();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (i != NavigationItem.DIVIDER.position) {
            navigationAdapter.setPositionSelected(i);
            navigationAdapter.notifyDataSetChanged();
            openMenu((NavigationItem) mMenuListView.getItemAtPosition(i));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constants.PICK_IMAGE_FOR_PROFILE:
                    ((ProfileFragment) currentFragment).setAvatar(
                            ImagePicker.getImageFromResult(this, resultCode, data));
                    break;
                default:
                    super.onActivityResult(requestCode, resultCode, data);
                    break;
            }
        }
    }

    @OnClick(R.id.navigation_header_container)
    public void goToProfile() {
        openMenu(NavigationItem.PROFILE);
    }

    private void setNavigationDrawer() {
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.app_name, R.string.app_name);
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerToggle.syncState();
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        navigationAdapter = new NavigationAdapter(this, NavigationItem.values());
        mMenuListView.setAdapter(navigationAdapter);
        mMenuListView.setOnItemClickListener(this);
        mMenuListView.getAdapter()
                .getView(NavigationItem.DIVIDER.position, null, mMenuListView)
                .setEnabled(false);
    }

    public void openMenu(NavigationItem item) {
        if (NavigationItem.HOME.equals(item)) {
            openFragment(R.string.navigation_home, new HomeFragment());
        } else if (NavigationItem.PROFILE.equals(item)) {
            openFragment(R.string.navigation_profile, new ProfileFragment());
        } else if (NavigationItem.PREFERENCES.equals(item)) {
            PreferencesActivity.startActivity(this);
        } else if (NavigationItem.LOGOUT.equals(item)) {
            currentFragment = null;
            dialogLogout();
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
    }

    private void openFragment(int title, Fragment fragment) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
            currentFragment = fragment;
            openFragment(currentFragment);
        }
    }

    public void setUser() {
        if (currentUser != null) {
            userEmail.setText(currentUser.getEmail());
            userName.setText((new LocalPreferences(getBaseContext())
                    .getUser().get(Constants.USER_NAME)));

            Firebase.getFirebaseDatabase().child(Constants.DATABASE_NODES.AVATAR)
                    .child(currentUser.getUid()).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot != null && dataSnapshot.getValue(Avatar.class) != null) {
                                (new LocalPreferences(getBaseContext())).saveAvatar(
                                        dataSnapshot.getValue(Avatar.class).getAvatarURL());
                                setAvatar();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) { }
                    });
        }
    }

    private void setAvatar() {
        final String avatar = (new LocalPreferences(getBaseContext()).getUser().get(Constants.USER_AVATAR));
        if (avatar != null && !avatar.equals("")) {
            new RequestCachedAvatar().execute(avatar);
            Picasso.with(this)
                    .load(avatar)
                    .fit()
                    .centerCrop()
                    .noPlaceholder()
                    .into(avatarImageView);
        }
    }

    public void openFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content_view, fragment);
        fragmentTransaction.commit();
    }

    private void dialogLogout() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setMessage(R.string.dialog_logout);
        alertBuilder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                logout();
            }
        });
        alertBuilder.setNeutralButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mDrawerLayout.closeDrawer(GravityCompat.START);
            }
        });
        alertBuilder.show();
    }

    private void logout () {
        Firebase.getFirebaseAuth().signOut();
        (new LocalPreferences(getBaseContext())).logoutUser();
        LoginManager.getInstance().logOut();
        goToLogin();
    }

    private void verifyIsUserLogged() {
        if (Firebase.getFirebaseAuth().getCurrentUser() == null) {
            goToLogin();
        } else {
            currentUser = Firebase.getFirebaseAuth().getCurrentUser();
        }
    }

    private void goToLogin() {
        LoginActivity.startActivity(MainActivity.this);
    }

    private class RequestCachedAvatar extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... strings) {
            try {
                final Bitmap avatarCached = Picasso.with(MainActivity.this)
                                .load(strings[0])
                                .networkPolicy(NetworkPolicy.OFFLINE)
                                .get();
                if (avatarCached != null) {
                    MainActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            avatarImageView.setImageBitmap(avatarCached);
                        }
                    });
                }
            } catch (IOException ignored) { }
            return null;
        }
    }
}
