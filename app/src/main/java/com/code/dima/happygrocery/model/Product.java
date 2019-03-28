package com.code.dima.happygrocery.model;

import com.code.dima.happygrocery.R;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Product {

    private Category category;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("price")
    @Expose
    private float price;

    @SerializedName("barcode")
    @Expose
    private String barcode;

    @SerializedName("weight")
    @Expose
    private float weight;

    @SerializedName("quantity")
    @Expose
    private int quantity;

    private int imageID;


    public Product(){
        category = Category.FOOD;
        barcode = "1234567890100";
        name = "Fragole di Montagna";
        price = 2.50f;
        imageID = R.drawable.fragole;
        weight = 0.4f;
        quantity = 1;
    }


    public Product(Category category, String name, float price, String barcode, float weight, int quantity, int imageID){
        this.category = category;
        this.name = name;
        this.price = price;
        this.barcode = barcode;
        this.weight = weight;
        this.quantity = quantity;
        this.imageID = imageID;
    }


    public Category getCategory() {
        return category;
    }


    public String getName() {
        return name;
    }


    public float getPrice() {
        return price;
    }


    public String getBarcode() {return barcode;}


    public float getWeight() {
        return weight;
    }


    public int getQuantity() {return quantity; }

    public void setQuantity(int newQuantity) {
        this.quantity = newQuantity;
    }


    public int getImageID() {
        return imageID;
    }

    public void setImageID(int imageID) {
        this.imageID = imageID;
    }


    @Override
    public boolean equals(Object other){
        boolean answer = false;
        if (other == this) answer = true;
        if (other instanceof Product) {
            Product otherP = (Product) other;
            answer = (
                    this.name.equals(otherP.name) &&
                    this.category == otherP.category &&
                    this.barcode.equals(otherP.barcode) &&
                    this.imageID == otherP.imageID &&
                    this.price == otherP.price &&
                    this.weight == otherP.weight
                    );
        }
        return answer;
    }

    public boolean decreaseQuantity(){
        quantity = quantity - 1;
        if(quantity == 0)
            return false;
        return true;
    }

    public void updateQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return this.name;
    }

}
