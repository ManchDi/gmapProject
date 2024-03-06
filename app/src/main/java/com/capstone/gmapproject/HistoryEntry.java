package com.capstone.gmapproject;

public class HistoryEntry {
    public String address;
    public String name;
    public String charger;
    private double cost;
    private double time;
    public HistoryEntry(){

    }

    public HistoryEntry(String a, String t, String c){
        setAddress(a);
        setName(t);
        setCharger(c);
    }

    public void setAddress(String newAddress){
        address = newAddress;
    }
    public String getAddress(){
        return address;
    }

    public void setName(String newName){
        name = newName;
    }
    public String getName(){
        return name;
    }

    private void setCharger(String newCharger){
        charger = newCharger;
    }
}
