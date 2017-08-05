package io.makerforce.ambrose.launcher;

import android.graphics.drawable.Drawable;

/**
 * Created by ambrosechua on 5/8/17.
 */

class AppItem {

    private final String packageName;
    private final String title;
    private final String description;
    private final Drawable icon;
    private final boolean hasColor;
    private final int color;
    private final int type;
    public static final int NORMAL_ICON = 0;
    public static final int SIMPLE_ICON = 1;
    public static final int BANNER_IMAGE = 2;

    public AppItem(String packageName, String title, String description, Drawable icon, int type) {
        this.packageName = packageName;
        this.title = title;
        this.description = description;
        this.icon = icon;
        this.hasColor = false;
        this.color = 0;
        this.type = type;
    }
    public AppItem(String packageName, String title, String description, Drawable icon, int type, int color) {
        this.packageName = packageName;
        this.title = title;
        this.description = description;
        this.icon = icon;
        this.hasColor = color != -1 ? true : false;
        this.color = color;
        this.type = type;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Drawable getIcon() {
        return icon;
    }

    public boolean hasColor() {
        return hasColor;
    }
    public int getColor() {
        return color;
    }

    public int getType() {
        return type;
    }

}
