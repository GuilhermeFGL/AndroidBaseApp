package com.example.guilherme.firebasedatabse.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.guilherme.firebasedatabse.R;
import com.example.guilherme.firebasedatabse.fragments.TabbedView1Fragment;
import com.example.guilherme.firebasedatabse.fragments.TabbedView2Fragment;
import com.example.guilherme.firebasedatabse.fragments.TabbedView3Fragment;

public class ViewAdapter extends FragmentPagerAdapter {

    private final int PAGE_COUNT = 3;
    private String[] tabTitles;

    public ViewAdapter(FragmentManager fm, Context context) {
        super(fm);
        tabTitles = new String[] {
                context.getString(R.string.tabbed_view_1),
                context.getString(R.string.tabbed_view_2),
                context.getString(R.string.tabbed_view_3)};
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new TabbedView1Fragment();
            case 1:
                return new TabbedView2Fragment();
            case 2:
                return new TabbedView3Fragment();
            default:
                return new TabbedView1Fragment();
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}
