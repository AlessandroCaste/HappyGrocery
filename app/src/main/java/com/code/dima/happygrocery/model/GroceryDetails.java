package com.code.dima.happygrocery.model;

public class GroceryDetails {

    private Float amount;
    private String supermarket;
    private String date;

    public GroceryDetails(Float amount, String supermarket, String date) {
        this.amount = amount;
        this.supermarket = supermarket;
        this.date = date;
    }

    public Float getAmount() {
        return amount;
    }

    public String getSupermarket() {
        return supermarket;
    }

    public String getDate() {
        return date;
    }
}
