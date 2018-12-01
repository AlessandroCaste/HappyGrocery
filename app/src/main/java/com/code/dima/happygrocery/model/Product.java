package com.code.dima.happygrocery.model;

import com.example.alessandro.barcodeyeah.R;

import java.security.Policy;

public class Product {

    private Category category;
    private String name;
    private float price;
    private String barcode;
    private float weight;
    private int quantity;
    private int imageID;


    public Product(){
        category = Category.FOOD;
        barcode = "1234567890100";
        name = "Fragole di Montagnaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
        price = 30f;
        imageID = R.drawable.fragole;
        weight = 0.4f;
        quantity = 2;
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


    public int getImageID() {
        return imageID;
    }


    @Override
    public boolean equals(Object other){
        boolean answer = false;
        //if (other == null) return false;
        if (other == this) answer = true;
        // if (!(other instanceof Product))return false;
        if (other instanceof Product) {
            Product otherP = (Product) other;
            answer = (
                    this.name == otherP.name &&
                    this.category == otherP.category &&
                    this.barcode == otherP.barcode &&
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

}
