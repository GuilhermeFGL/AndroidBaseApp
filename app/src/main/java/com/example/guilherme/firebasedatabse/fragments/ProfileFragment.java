package com.example.guilherme.firebasedatabse.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.guilherme.firebasedatabse.R;

public class ProfileFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle instance) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

}