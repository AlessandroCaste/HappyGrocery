package com.code.dima.happygrocery.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.RecyclerView;
import android.widget.Filter;

import com.code.dima.happygrocery.model.Product;
import com.example.alessandro.barcodeyeah.R;
import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductHolder> {

    private Context context;
    private List<Product> products;
    CustomFilter filter;

    public ProductAdapter(Context context, List<Product> products) {
        this.context = context;
        this.products = products;
    }

    @Override
    public ProductHolder onCreateViewHolder (ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.product_card,parent,false);
        Log.w("myApp","Opero nel magico mondo!") ;
        return new ProductHolder(view);
    }

    @Override
    public int getItemCount() { return products.size(); }

    @Override
    public void onBindViewHolder(ProductHolder holder, int position) {
        Product product = products.get(position);
        holder.setDetails(product);
    }

    public void set (List<Product> filtered){
        products = filtered;
    }


    //RETURN FILTER OBJ
    public Filter getFilter() {
        if(filter==null)
        {
            filter=new CustomFilter(products,this);
        }
        return filter;
    }

}
