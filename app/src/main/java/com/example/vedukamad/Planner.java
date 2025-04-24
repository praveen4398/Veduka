package com.example.vedukamad;

public class Planner {
    private String name;
    private String location;
    private float rating;
    private int imageRes;

    public Planner(String name, String location, float rating, int imageRes) {
        this.name = name;
        this.location = location;
        this.rating = rating;
        this.imageRes = imageRes;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public float getRating() {
        return rating;
    }

    public int getImageRes() {
        return imageRes;
    }
}
