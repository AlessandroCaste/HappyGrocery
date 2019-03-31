package com.code.dima.happygrocery.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import com.code.dima.happygrocery.R;
import com.code.dima.happygrocery.core.ProductActivity;
import com.code.dima.happygrocery.database.DeleteProductFromDBTask;
import com.code.dima.happygrocery.database.UpdateProductQuantityInDBTask;
import com.code.dima.happygrocery.exception.NoSuchProductException;
import com.code.dima.happygrocery.model.Product;
import com.code.dima.happygrocery.model.ShoppingCart;

import java.lang.ref.WeakReference;
import java.util.List;

public class ProductCheckoutAdapter extends RecyclerView.Adapter<ProductHolder> {

    private WeakReference<Context> context;
    private List<Product> products;
    private CustomFilter filter;

    public ProductCheckoutAdapter(Context context, List<Product> products) {
        this.context = new WeakReference<>(context);
        this.products = products;
    }

    @Override
    public ProductHolder onCreateViewHolder (ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context.get()).inflate(R.layout.product_checkout_card,parent,false);
        Log.w("myApp","Opero nel magico mondo!") ;
        return new ProductHolder(view);
    }

    @Override
    public int getItemCount() { return products.size(); }

    @Override
    public void onBindViewHolder(final ProductHolder holder, final int position) {
        final Product product = products.get(position);
        holder.setDetails(product);
    }

    public void set (List<Product> filtered){
        products = filtered;
        super.notifyDataSetChanged();
    }

}
