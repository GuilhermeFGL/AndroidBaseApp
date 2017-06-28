package com.example.guilherme.firebasedatabse.components;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.example.guilherme.firebasedatabse.R;

/**
 * Created by guilherme.lima on 28/06/2017.
 */

public class AskPasswordDialog extends Dialog {

    public AskPasswordDialog(@NonNull Context context) {
        super(context);

        this.setCancelable(false);
        this.setCanceledOnTouchOutside(false);
        this.setTitle(R.string.dialog_aks_password_title);
        this.setContentView(R.layout.dialog_ask_password);
    }

    public void show(final ActionCallback actionCallback) {
        this.findViewById(R.id.dialog_positive_button).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AskPasswordDialog.this.close();
                        actionCallback.onPositiveClick(
                                ((TextView) AskPasswordDialog.this
                                        .findViewById(R.id.dialog_password))
                                        .getText().toString());
                    }
                }
        );
        this.findViewById(R.id.dialog_negative_button).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AskPasswordDialog.this.close();
                        actionCallback.onNegativeClick();
                    }
                }
        );
        this.show();
    }

    public void close() {
        if (this.isShowing()) {
            this.cancel();
        }
    }

    public interface ActionCallback {
        void onPositiveClick(String password);
        void onNegativeClick();
    }
}
