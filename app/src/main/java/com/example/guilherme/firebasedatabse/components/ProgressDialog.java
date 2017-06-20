package com.example.guilherme.firebasedatabse.components;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Window;
import android.widget.TextView;

import com.example.guilherme.firebasedatabse.R;

/**
 * Created by guilherme.lima on 20/06/2017.
 */

public class ProgressDialog extends Dialog {

    public ProgressDialog(@NonNull Context context) {
        super(context);

        this.setCancelable(false);
        this.setCanceledOnTouchOutside(false);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.dialog_progress);
    }

    public void show(String message) {
        ((TextView) this.findViewById(R.id.progress_dialog_message)).setText(message);
        this.show();
    }

    public void close() {
        if (this.isShowing()) {
            this.cancel();
        }
    }
}
