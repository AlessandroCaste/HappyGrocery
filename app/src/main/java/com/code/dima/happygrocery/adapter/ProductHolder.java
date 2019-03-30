package com.code.dima.happygrocery.adapter;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.code.dima.happygrocery.R;
import com.code.dima.happygrocery.model.Product;


public class ProductHolder extends RecyclerView.ViewHolder {

    private TextView info_name;
    private ImageView imageView;
    public FloatingActionButton removeButton;
    public FloatingActionButton editButton;
    private TextView quantity;

    public ProductHolder(View itemView) {
        super(itemView);
        info_name = itemView.findViewById(R.id.info_name);
        imageView = itemView.findViewById(R.id.productImage);
        removeButton = itemView.findViewById(R.id.removeButton);
        editButton = itemView.findViewById(R.id.editButton);
        quantity = itemView.findViewById(R.id.quantityText);
        //info_price = itemView.findViewById(R.id.info_price);
        //info_description = itemView.findViewById(R.id.info_description);
    }
    public void setDetails(Product product) {
        info_name.setText(product.getName());
        quantity.setText(Integer.toString(product.getQuantity()) + "x");
        imageView.setImageResource(product.getImageID());
        Log.w("myApp", "prodotto riconosciuto");
        //info_price.setText(product.getPrice());
        //info_description.setText(product.getDescription());
    }


}
