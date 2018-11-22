package com.code.dima.happygrocery.model;

import com.code.dima.happygrocery.model.Category;
import com.example.alessandro.barcodeyeah.R;

public class Product {

    private Category category;
    private String tipology;
    private String name;
    private String price;
    private String description;
    private int imageID;


    public Product(){
        category = Category.FOOD;
        tipology = "Fruit";
        name = "Fragole di Montagna";
        price = "30 Euro";
        description = "Le fragole di Montagna Esselunga sono il meglio che la nostra azienda offre";
        imageID = R.drawable.fragole;
    }


    public Category getCategory() {
        return category;
    }

    public String getTipology() {
        return tipology;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public String getDescription(){
        return description;
    }

    public int getImageID() {
        return imageID;
    }

}
