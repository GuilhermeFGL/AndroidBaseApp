package com.example.guilherme.firebasedatabse.fragments;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
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

    @BindView(R.id.tabbed_app_bar)
    AppBarLayout appBar;
    @BindView(R.id.tabbed_actionbar)
    CollapsingToolbarLayout actionBar;
    @BindView(R.id.tabbed_toolbar)
    Toolbar toolbar;
    @BindView(R.id.tabbed_tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.tabbed_view_page)
    ViewPager viewPager;
    @BindView(R.id.tabbed_fab_p1)
    FloatingActionButton fab1;
    @BindView(R.id.tabbed_fab_p2)
    FloatingActionButton fab2;
    @BindView(R.id.tabbed_fab_p3)
    FloatingActionButton fab3;

    private ActionBar activityToolbar;
    private boolean isCollapsed;

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tabbed, container, false);
        ButterKnife.bind(this, view);

        isCollapsed = false;

        activityToolbar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (activityToolbar != null) {
            activityToolbar.hide();
        }

        actionBar.setTitle(getString(R.string.navigation_tabbed));
        viewPager.setAdapter(new ViewAdapter(getChildFragmentManager(), getContext()));
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                animateFab(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });
        appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (Math.abs(verticalOffset) - appBarLayout.getTotalScrollRange() == 0) {
                    animateFab(-1);
                    isCollapsed = true;
                } else {
                    isCollapsed = false;
                    animateFab(tabLayout.getSelectedTabPosition());
                }
            }
        });

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

    private void animateFab(int position) {
        if (!isCollapsed) {
            switch (position) {
                case 0:
                    fab1.show();
                    fab2.hide();
                    fab3.hide();
                    break;
                case 1:
                    fab1.hide();
                    fab2.show();
                    fab3.hide();
                    break;
                case 2:
                    fab1.hide();
                    fab2.hide();
                    fab3.show();
                    break;
                default:
                    fab1.hide();
                    fab2.hide();
                    fab3.hide();
                    break;
            }
        }
    }
}
