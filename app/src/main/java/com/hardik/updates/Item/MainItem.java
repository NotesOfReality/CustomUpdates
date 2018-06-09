package com.hardik.updates.Item;

import android.graphics.Bitmap;

public class MainItem {

    private boolean isheading;
    private int iconId;
    private String header, title, text;
    private int id;

    public MainItem(boolean isheading, int iconId, String header, String title, String text, int id) {
        this.isheading = isheading;
        this.iconId = iconId;
        this.header = header;
        this.title = title;
        this.text = text;
        this.id = id;
    }

    public boolean isIsheading() {
        return isheading;
    }

    public int getIconId() {
        return iconId;
    }

    public String getHeader() {
        return header;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public int getId() {
        return id;
    }
}
