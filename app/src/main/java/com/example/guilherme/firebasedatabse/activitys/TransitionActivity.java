package com.example.guilherme.firebasedatabse.activitys;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.transition.Explode;
import android.transition.Transition;
import android.view.animation.AnimationUtils;

import com.example.guilherme.firebasedatabse.R;

public class TransitionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transition);

        Transition transition = new Explode()
                .excludeTarget(android.R.id.statusBarBackground, true)
                .excludeTarget(android.R.id.navigationBarBackground, true)
                .setInterpolator(AnimationUtils.loadInterpolator(
                        this,
                        android.R.interpolator.linear_out_slow_in));

        getWindow().setEnterTransition(transition);
    }
}
