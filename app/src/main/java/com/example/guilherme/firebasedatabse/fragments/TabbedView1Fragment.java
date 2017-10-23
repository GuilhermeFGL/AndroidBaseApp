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
        list.setAdapter(new RecyclerView.Adapter<TabbedView1Fragment.ViewHolder>() {
            @Override
            public TabbedView1Fragment.ViewHolder onCreateViewHolder(ViewGroup parent, int position) {
                return new TabbedView1Fragment.ViewHolder(getLayoutInflater().inflate(R.layout.list_item, parent, false));
            }

            @Override
            public void onBindViewHolder(TabbedView1Fragment.ViewHolder viewHolder, int position) {
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
