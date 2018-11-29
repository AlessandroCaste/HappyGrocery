package com.code.dima.happygrocery.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.code.dima.happygrocery.model.Product;
import com.example.alessandro.barcodeyeah.R;

public class ProductHolder extends RecyclerView.ViewHolder {

    private TextView info_name, info_price, info_description;
    private ImageView imageView;

    public ProductHolder(View itemView) {
        super(itemView);
        info_name = itemView.findViewById(R.id.info_name);
        imageView = itemView.findViewById(R.id.productImage);
        //info_price = itemView.findViewById(R.id.info_price);
        //info_description = itemView.findViewById(R.id.info_description);
    }
    public void setDetails(Product product) {
        info_name.setText(product.getName());
        imageView.setImageResource(product.getImageID());
        Log.w("myApp", "prodotto riconosciuto");
        //info_price.setText(product.getPrice());
        //info_description.setText(product.getDescription());
    }
}
