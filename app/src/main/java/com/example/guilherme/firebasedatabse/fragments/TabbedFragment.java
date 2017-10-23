package com.example.guilherme.firebasedatabse.fragments;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.guilherme.firebasedatabse.R;
import com.example.guilherme.firebasedatabse.activitys.MainActivity;
import com.example.guilherme.firebasedatabse.adapters.ViewAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TabbedFragment extends Fragment {

    @BindView(R.id.tabbed_actionbar)
    CollapsingToolbarLayout actionBar;
    @BindView(R.id.tabbed_toolbar)
    Toolbar toolbar;
    @BindView(R.id.tabbed_tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.tabbed_view_page)
    ViewPager viewPager;

    private ActionBar activityToolbar;

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tabbed, container, false);
        ButterKnife.bind(this, view);

        activityToolbar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (activityToolbar != null) {
            activityToolbar.hide();
        }

        actionBar.setTitle(getString(R.string.navigation_tabbed));
        viewPager.setAdapter(new ViewAdapter(getChildFragmentManager(), getContext()));
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (activityToolbar != null) {
            activityToolbar.show();
        }
    }

    @OnClick(R.id.tabbed_navigation)
    public void openNavigationDrawer() {
        ((MainActivity)getActivity()).openNavigationDrawer();
    }
}
