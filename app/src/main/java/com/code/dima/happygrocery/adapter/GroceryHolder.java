package com.code.dima.happygrocery.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.code.dima.happygrocery.R;
import com.code.dima.happygrocery.model.GroceryDetails;


public class GroceryHolder extends RecyclerView.ViewHolder {

    private TextView groceryAmount;
    private TextView grocerySupermarket;
    private TextView groceryDate;
    private String currency;

    public GroceryHolder(@NonNull View itemView) {
        super(itemView);
        groceryAmount = itemView.findViewById(R.id.grocery_amount);
        grocerySupermarket = itemView.findViewById(R.id.grocery_supermarket);
        groceryDate = itemView.findViewById(R.id.grocery_date);
        currency = itemView.getResources().getString(R.string.currency);
    }

    public void setDetails(GroceryDetails grocery) {
        String amount = grocery.getAmount() + currency;
        groceryAmount.setText(amount);
        grocerySupermarket.setText(grocery.getSupermarket());
        groceryDate.setText(grocery.getDate());
    }
}
