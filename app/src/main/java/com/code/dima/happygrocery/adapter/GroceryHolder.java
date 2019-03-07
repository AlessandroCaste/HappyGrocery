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
    private TextView groceryClosed;

    public GroceryHolder(@NonNull View itemView) {
        super(itemView);
        groceryAmount = itemView.findViewById(R.id.grocery_amount);
        grocerySupermarket = itemView.findViewById(R.id.grocery_supermarket);
        groceryDate = itemView.findViewById(R.id.grocery_date);
        currency = itemView.getResources().getString(R.string.currency);
        groceryClosed = itemView.findViewById(R.id.grocery_closed);
    }

    public void setDetails(GroceryDetails grocery) {
        String amount = String.format("%.2f", grocery.getAmount()) + currency;
        groceryAmount.setText(amount);
        grocerySupermarket.setText(grocery.getSupermarket());
        groceryDate.setText(grocery.getDate());
        if (grocery.getClosed()) {
            groceryClosed.setText(R.string.closed_grocery);
        } else {
            groceryClosed.setText("");
        }
    }
}
