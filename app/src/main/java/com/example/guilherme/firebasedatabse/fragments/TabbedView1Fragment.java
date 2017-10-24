package com.example.guilherme.firebasedatabse.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.guilherme.firebasedatabse.R;
import com.example.guilherme.firebasedatabse.adapters.ListAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TabbedView1Fragment extends Fragment {

    @BindView(R.id.tabbed_list_view)
    RecyclerView list;

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tabbed_view_1, container, false);
        ButterKnife.bind(this, view);

        list.setLayoutManager(new LinearLayoutManager(getActivity()));
        list.setAdapter(new ListAdapter());

        return view;
    }
}
