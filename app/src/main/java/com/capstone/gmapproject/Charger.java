package com.capstone.gmapproject;

public class Charger {
    private int id;
    private int stationId;
    private String price;
    private String connectionType;
    private String wattage;

   public Charger(){
   }
    public Charger(int id, int stationId, String price, String connectionType, String wattage) {
        this.id = id;
        this.stationId = stationId;
        this.price = price;
        this.connectionType = connectionType;
        this.wattage = wattage;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStationId() {
        return stationId;
    }

    public void setStationId(int stationId) {
        this.stationId = stationId;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getConnectionType() {
        return connectionType;
    }

    public void setConnectionType(String connectionType) {
        this.connectionType = connectionType;
    }

    public String getWattage() {
        return wattage;
    }

    public void setWattage(String wattage) {
        this.wattage = wattage;
    }
    public void print() {
        System.out.println("Connection Type: " + connectionType);
        System.out.println("Wattage: " + wattage);
    }
}

