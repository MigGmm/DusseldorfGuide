package com.example.miguel.guiadusseldorf.model;

/**
 * Model for work with places.
 */
public class Place {

    private int id;
    private String name;
    private String town;
    private String address;
    private String manager;
    private double latitude;
    private double longitude;

    public String getName() {
        return name;
    }

    public String getTown() {
        return town;
    }

    public String getAddress() {
        return address;
    }

    public String getManager() {
        return manager;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setManager(String manager) {
        this.manager = manager;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
