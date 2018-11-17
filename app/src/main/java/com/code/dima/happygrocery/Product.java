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
    private String productor;
    private float weight;
    private float sale;
    private Bitmap image;

    public Product(){
        tipology = "Fruit";
        name = "Fragole di Montagna";
        price = "30 Euro";
        description = "Le fragole di Montagna Esselunga sono il meglio che la nostra azienda offre";
        productor = "Cascine Fragolose";
        weight = 100;
        sale = 0;
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

    public float getSale() { return sale; };

    public String getProduttore() { return productor; }

    public float getWeight() { return weight; }

}
