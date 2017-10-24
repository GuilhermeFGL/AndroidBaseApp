package com.example.guilherme.firebasedatabse.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.guilherme.firebasedatabse.R;
import com.example.guilherme.firebasedatabse.activitys.TransitionActivity;

import de.hdodenhof.circleimageview.CircleImageView;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

    private Context context;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        context = parent.getContext();
        return new ViewHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_list, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        viewHolder.text1.setText(context.getString(R.string.tabbed_list_title));
        viewHolder.text2.setText(context.getString(R.string.tabbed_list_body));
        viewHolder.item.setOnClickListener(new View.OnClickListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void onClick(View view) {
                context.startActivity(
                        new Intent(context, TransitionActivity.class),
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                                (Activity) context,
                                Pair.create(
                                        (View) viewHolder.image,
                                        viewHolder.image.getTransitionName())
                        ).toBundle());
            }
        });
    }

    @Override
    public int getItemCount() {
        return 15;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout item;
        CircleImageView image;
        TextView text1;
        TextView text2;

        ViewHolder(View itemView) {
            super(itemView);
            item = itemView.findViewById(R.id.list_item);
            image = itemView.findViewById(R.id.item_image);
            text1 = itemView.findViewById(android.R.id.text1);
            text2 = itemView.findViewById(android.R.id.text2);
        }
    }

}
