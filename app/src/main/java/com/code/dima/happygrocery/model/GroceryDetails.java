package com.code.dima.happygrocery.model;

public class GroceryDetails {

    private Float amount;
    private String supermarket;
    private String date;
    private boolean closed;

    public GroceryDetails(Float amount, String supermarket, String date, boolean closed) {
        this.amount = amount;
        this.supermarket = supermarket;
        this.date = date;
        this.closed = closed;
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

    public boolean getClosed() {
        return closed;
    }
}
