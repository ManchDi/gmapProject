package com.capstone.gmapproject;

import android.util.Log;

public class Station {
    private int id;
    private String name;
    private String address;
    private int chargerAmount;
    private String chargerType;
    private double latitude;
    private double longitude;

    // Constructors
    public Station() {
    }

    public Station(int id, String name, String address, int chargerAmount, String chargerType, double latitude, double longitude) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.chargerAmount = chargerAmount;
        this.chargerType = chargerType;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getChargerAmount() {
        return chargerAmount;
    }

    public void setChargerAmount(int chargerAmount) {
        this.chargerAmount = chargerAmount;
    }

    public String getChargerType() {
        return chargerType;
    }

    public void setChargerType(String chargerType) {
        this.chargerType = chargerType;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    public void printStation(){
        Log.d("stationsInfo", "Name "+name+", lng: "+longitude+", ltd: "+latitude);
    }
    // toString method
    @Override
    public String toString() {
        return "Station{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", chargerAmount=" + chargerAmount +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}

