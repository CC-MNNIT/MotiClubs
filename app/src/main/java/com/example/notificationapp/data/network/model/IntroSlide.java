package com.example.notificationapp.data.network.model;

public class IntroSlide {

    String title, description;
    int icon;

    public IntroSlide(String title, String description, int icon) {
        this.title = title;
        this.description = description;
        this.icon = icon;
    }

    public IntroSlide() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
}
