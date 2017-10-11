package com.example.guilherme.firebasedatabse.model;

import com.example.guilherme.firebasedatabse.R;

public enum NavigationItem {
    HOME(R.drawable.ic_menu_home, R.string.navigation_home, 0),
    TABBED(R.drawable.ic_menu_tabs, R.string.navigation_tabbed, 1),
    PROFILE(R.drawable.ic_menu_profile, R.string.navigation_profile, 2),
    PREFERENCES(R.drawable.ic_preferences, R.string.title_activity_preferences, 3),
    DIVIDER(0, 0, 4),
    LOGOUT(R.drawable.ic_menu_logout, R.string.navigation_logout, 5);

    public Integer icon;
    public Integer name;
    public Integer position;

    NavigationItem(int icon, int name, int position) {
        this.icon = icon;
        this.name = name;
        this.position = position;
    }
}
