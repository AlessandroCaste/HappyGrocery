package com.code.dima.happygrocery.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.code.dima.happygrocery.model.Product;
import com.example.alessandro.barcodeyeah.R;

public class ProductHolder extends RecyclerView.ViewHolder {

    private TextView info_name, info_price, info_description;

    public ProductHolder(View itemView) {
        super(itemView);
        info_name = itemView.findViewById(R.id.info_name);
        //info_price = itemView.findViewById(R.id.info_price);
        //info_description = itemView.findViewById(R.id.info_description);
    }
    public void setDetails(Product product) {
        info_name.setText(product.getName());
        Log.w("myApp", "prodotto riconosciuto");
        //info_price.setText(product.getPrice());
        //info_description.setText(product.getDescription());
    }
}
