package com.code.dima.happygrocery.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.code.dima.happygrocery.R;
import com.code.dima.happygrocery.model.GroceryDetails;

import java.util.List;

public class GroceryAdapter extends RecyclerView.Adapter<GroceryHolder> {

    private Context context;
    private List<GroceryDetails> groceries;

    public GroceryAdapter(Context context, List<GroceryDetails> groceries) {
        this.context = context;
        this.groceries = groceries;
    }

    @NonNull
    @Override
    public GroceryHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.grocery_card,viewGroup,false);
        return new GroceryHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroceryHolder groceryHolder, int position) {
        GroceryDetails grocery = groceries.get(position);
        groceryHolder.setDetails(grocery);
    }

    @Override
    public int getItemCount() {
        return groceries.size();
    }
}
