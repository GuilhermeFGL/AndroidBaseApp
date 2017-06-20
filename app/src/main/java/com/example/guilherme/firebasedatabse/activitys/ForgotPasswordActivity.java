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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ForgotPasswordActivity extends AppCompatActivity {

    @BindView(R.id.forgot_email)
    TextView emailTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
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

    @OnClick(R.id.forgot_action)
    public void sendEmail() {
        String email = emailTextView.getText().toString();
        if (!email.equals("")) {
            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.show(getString(R.string.dialog_wait));

            Firebase.getFirebaseAuth().sendPasswordResetEmail(emailTextView.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            dialog.close();

                            if (task.isSuccessful()) {
                                Toast.makeText(ForgotPasswordActivity.this,
                                        getString(R.string.success_forgot),
                                        Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(ForgotPasswordActivity.this,
                                        getString(R.string.error_forgot),
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        } else {
            emailTextView.setError(getString(R.string.error_empty_required));
        }
    }

}
