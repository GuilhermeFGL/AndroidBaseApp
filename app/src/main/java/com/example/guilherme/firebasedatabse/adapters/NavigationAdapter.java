package com.example.guilherme.firebasedatabse.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.guilherme.firebasedatabse.R;
import com.example.guilherme.firebasedatabse.model.NavigationItem;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by guilherme.lima on 22/06/2017.
 */

public class NavigationAdapter extends ArrayAdapter<NavigationItem> {

    private long positionSelected = 0;

    public NavigationAdapter(Context context, NavigationItem[] items) {
        super(context, R.layout.menu_item, items);
    }

    public void setPositionSelected(long position){
        this.positionSelected = position;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        NavigationItem navigationItem = getItem(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = View.inflate(getContext(), R.layout.menu_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (navigationItem != null) {
            if (navigationItem.position.equals(NavigationItem.DIVIDER.position)) {
                viewHolder.selectableLayout.setVisibility(View.GONE);
                viewHolder.separatorView.setVisibility(View.VISIBLE);
                viewHolder.menuItem.setBackgroundResource(R.color.text_color_primary_light);
            } else {
                viewHolder.iconImageView.setImageResource(navigationItem.icon);
                viewHolder.nameTextView.setText(navigationItem.name);
            }
        }

        if (positionSelected == position) {
            viewHolder.iconImageView.setColorFilter(
                    ContextCompat.getColor(getContext(), R.color.colorPrimary));
            viewHolder.nameTextView.setTextColor(
                    ContextCompat.getColor(getContext(), R.color.colorPrimary));
        } else {
            viewHolder.iconImageView.setColorFilter(
                    ContextCompat.getColor(getContext(), R.color.text_color_primary)
            );
            viewHolder.nameTextView.setTextColor(
                    ContextCompat.getColor(getContext(), R.color.text_color_primary)
            );
        }
        return convertView;
    }

    class ViewHolder{
        @BindView(R.id.menu_item)
        LinearLayout menuItem;
        @BindView(R.id.menu_item_icon_image_view)
        ImageView iconImageView;
        @BindView(R.id.menu_item_name_text_view)
        TextView nameTextView;
        @BindView(R.id.menu_item_selectable)
        LinearLayout selectableLayout;
        @BindView(R.id.menu_item_separator)
        View separatorView;

        ViewHolder(View view){
            ButterKnife.bind(this, view);
        }
    }

}
