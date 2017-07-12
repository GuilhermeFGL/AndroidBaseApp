package com.example.guilherme.firebasedatabse.model;

import com.example.guilherme.firebasedatabse.R;

/**
 * Created by guilherme.lima on 22/06/2017.
 */

public enum NavigationItem {
    HOME(R.drawable.ic_menu_home, R.string.navigation_home, 0),
    PROFILE(R.drawable.ic_menu_profile, R.string.navigation_profile, 1),
    PREFERENCES(R.drawable.ic_preferences, R.string.title_activity_preferences, 2),
    DIVIDER(0, 0, 3),
    LOGOUT(R.drawable.ic_menu_logout, R.string.navigation_logout, 4);

    public Integer icon;
    public Integer name;
    public Integer position;

    NavigationItem(int icon, int name, int position) {
        this.icon = icon;
        this.name = name;
        this.position = position;
    }
}
