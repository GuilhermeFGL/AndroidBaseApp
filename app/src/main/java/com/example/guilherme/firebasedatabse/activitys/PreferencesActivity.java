package com.example.guilherme.firebasedatabse.activitys;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.TextUtils;
import android.view.MenuItem;

import com.example.guilherme.firebasedatabse.R;
import com.example.guilherme.firebasedatabse.components.AppCompatPreferenceActivity;

public class PreferencesActivity extends AppCompatPreferenceActivity {

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, PreferencesActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, new MainPreferenceFragment())
                .commit();
    }

    public static class MainPreferenceFragment extends PreferenceFragment {

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.xml_preferences);

            // notification preference change listener
            bindPreferenceSummaryToValue(
                    findPreference(getString(R.string.key_notifications_new_message_ringtone)));

            // feedback preference click listener
            findPreference(getString(R.string.key_send_feedback))
                    .setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                        public boolean onPreferenceClick(Preference preference) {
                            sendFeedback(getActivity());
                            return true;
                        }
                    });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private static void bindPreferenceSummaryToValue(Preference preference) {
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener =
            new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    String stringValue = newValue.toString();

                    if (preference instanceof RingtonePreference) {
                        if (TextUtils.isEmpty(stringValue)) {
                            preference.setSummary(R.string.pref_ringtone_silent);
                        } else {
                            Ringtone ringtone = RingtoneManager.getRingtone(
                                    preference.getContext(), Uri.parse(stringValue));

                            if (ringtone == null) {
                                preference.setSummary(R.string.summary_choose_ringtone);
                            } else {
                                String name = ringtone.getTitle(preference.getContext());
                                preference.setSummary(name);
                            }
                        }

                    } else {
                        preference.setSummary(stringValue);
                    }
                    return true;
                }
            };

    public static void sendFeedback(Context context) {
        String body = null;
        try {
            body = String.format(context.getString(R.string.email_body_contact),
                    Build.VERSION.RELEASE,
                    context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName,
                    Build.BRAND,
                    Build.MODEL,
                    Build.MANUFACTURER
            );
        } catch (PackageManager.NameNotFoundException ignored) { }

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType(context.getString(R.string.email_type));
        intent.putExtra(Intent.EXTRA_EMAIL,
                new String[]{context.getString(R.string.email_sender_contact)});
        intent.putExtra(Intent.EXTRA_SUBJECT,
                context.getString(R.string.email_subject_contact));
        intent.putExtra(Intent.EXTRA_TEXT, body);
        context.startActivity(
                Intent.createChooser(intent, context.getString(R.string.choose_email_client)));
    }
}
