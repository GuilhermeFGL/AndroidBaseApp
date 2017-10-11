package com.example.guilherme.firebasedatabse.fragments;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.guilherme.firebasedatabse.R;
import com.example.guilherme.firebasedatabse.activitys.MainActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TabbedFragment extends Fragment {

    @BindView(R.id.tabbed_actionbar)
    CollapsingToolbarLayout actionBar;
    @BindView(R.id.tabbed_toolbar)
    Toolbar toolbar;
    @BindView(R.id.tabbed_list_view)
    RecyclerView list;

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
        list.setLayoutManager(new LinearLayoutManager(getActivity()));
        list.setAdapter(new RecyclerView.Adapter<ViewHolder>() {
            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int position) {
                return new ViewHolder(getLayoutInflater().inflate(R.layout.list_item, parent, false));
            }

            @Override
            public void onBindViewHolder(ViewHolder viewHolder, int position) {
                viewHolder.text1.setText(getString(R.string.tabbed_list_title));
                viewHolder.text2.setText(getString(R.string.tabbed_list_body));
            }

            @Override
            public int getItemCount() {
                return 15;
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

    private static class ViewHolder extends RecyclerView.ViewHolder {
        TextView text1;
        TextView text2;

        ViewHolder(View itemView) {
            super(itemView);
            text1 = itemView.findViewById(android.R.id.text1);
            text2 = itemView.findViewById(android.R.id.text2);
        }
    }
}
