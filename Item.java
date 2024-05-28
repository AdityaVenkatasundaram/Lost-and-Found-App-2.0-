package com.example.lostandfoundapp;

public class Item {
    private String itemName;
    private String phone;
    private String description;
    private String date;
    private String location;
    private boolean isFound;

    public Item(String itemName, String phone, String description, String date, String location, boolean isFound) {
        this.itemName = itemName;
        this.phone = phone;
        this.description = description;
        this.date = date;
        this.location = location;
        this.isFound = isFound;
    }

    public String getItemName() {
        return itemName;
    }

    public String getPhone() {
        return phone;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }

    public String getLocation() {
        return location;
    }

    public boolean isFound() {
        return isFound;
    }
}
