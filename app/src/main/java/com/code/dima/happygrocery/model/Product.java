package com.code.dima.happygrocery.model;

import com.example.alessandro.barcodeyeah.R;

import java.security.Policy;

public class Product {

    private Category category;
    private String name;
    private String price;
    private float weight;
    private int imageID;


    public Product(int id){
        if(id == 0) {
            category = Category.FOOD;
            name = "Fragole di Montagna";
            price = "30 Euro";
            imageID = R.drawable.fragole;
            weight = 0.9f;
        }

        if(id == 1) {
            category = Category.BEVERAGE;
            name = "Vino Buono";
            price = "50 Euro";
            imageID = R.drawable.wine;
            weight = 1.2f;
        }

        if(id == 2) {
            category = Category.HOME;
            name = "Swiffer";
            price = "10 Euro";
            imageID = R.drawable.swiffer;
            weight = 0.7f;
        }

    }



    public Category getCategory() {
        return category;
    }


    public String getName() {
        return name;
    }


    public String getPrice() {
        return price;
    }


    public int getImageID() {
        return imageID;
    }


    public float getWeight() {
        return weight;
    }


    @Override
    public boolean equals(Object other){
        boolean answer = false;
        //if (other == null) return false;
        if (other == this) answer = true;
        // if (!(other instanceof Product))return false;
        if (other instanceof Policy) {
            Product otherP = (Product) other;
            answer = (
                    this.name == otherP.name &&
                    this.category == otherP.category &&
                    this.imageID == otherP.imageID &&
                    this.price == otherP.price &&
                    this.weight == otherP.weight
                    );
        }
        return answer;
    }

}
