package com.code.dima.happygrocery;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

import com.example.alessandro.barcodeyeah.R;

public class Product {

    private String tipology;
    private String name;
    private String price;
    private String description;
    private Bitmap image;

    public Product(){
        tipology = "Fruit";
        name = "Fragole di Montagna";
        price = "30 Euro";
        description = "Le fragole di Montagna Esselunga sono il meglio che la nostra azienda offre";
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

}
