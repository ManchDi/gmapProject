package com.capstone.gmapproject;

public class HistoryEntry {
    public String address = "";
    public String type = "";
    public String charger = "";
    private double cost;
    private double time;

    public HistoryEntry(String a, String t, String c){
        setAddress(a);
        setType(t);
        setCharger(c);
    }

    private void setAddress(String newAddress){
        address = newAddress;
    }

    private void setType(String newType){
        type = newType;
    }

    private void setCharger(String newCharger){
        charger = newCharger;
    }
}
