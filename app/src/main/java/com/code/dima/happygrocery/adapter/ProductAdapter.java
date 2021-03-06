package com.code.dima.happygrocery.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.Filter;

import com.code.dima.happygrocery.R;
import com.code.dima.happygrocery.core.ProductActivity;
import com.code.dima.happygrocery.database.DeleteProductFromDBTask;
import com.code.dima.happygrocery.database.UpdateProductQuantityInDBTask;
import com.code.dima.happygrocery.exception.NoSuchProductException;
import com.code.dima.happygrocery.model.Product;
import com.code.dima.happygrocery.model.ShoppingCart;
import com.code.dima.happygrocery.wearable.WearableUpdateTask;

import java.lang.ref.WeakReference;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductHolder> {

    private WeakReference<Context> context;
    private List<Product> products;
    private CustomFilter filter;

    public ProductAdapter(Context context, List<Product> products) {
        this.context = new WeakReference<>(context);
        this.products = products;
    }

    @Override
    public ProductHolder onCreateViewHolder (@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context.get()).inflate(R.layout.product_card,parent,false);
        return new ProductHolder(view);
    }

    @Override
    public int getItemCount() { return products.size(); }

    @Override
    public void onBindViewHolder(@NonNull final ProductHolder holder, final int position) {
        final Product product = products.get(position);
        holder.setDetails(product);

        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                holder.removeButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        int newQuantity = product.getQuantity() - 1;
                        if (newQuantity > 0) {
                            try {
                                ShoppingCart.getInstance().updateQuantity(product, newQuantity);
                            } catch (NoSuchProductException e) {
                                e.printStackTrace();
                            }
                            holder.setDetails(product);
                            new UpdateProductQuantityInDBTask(context.get(), product, newQuantity).execute();
                       } else {
                            try {
                                ShoppingCart.getInstance().removeProduct(product);
                            } catch (NoSuchProductException e) {
                                e.printStackTrace();
                            }
                            int removedPosition = holder.getAdapterPosition();
                            //products.remove(removedPosition);
                            notifyItemRemoved(removedPosition);
                            new DeleteProductFromDBTask(context.get(), product).execute();
                        }
                        // in both cases, I notify the wearable of the update/deletion
                        new WearableUpdateTask(context.get(), false).execute();
                    }
                });
            }
        });

        handler.post(new Runnable() {
            @Override
            public void run() {
                holder.editButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent i = new Intent(context.get(),ProductActivity.class);
                        i.putExtra("barcode", product.getBarcode());
                        i.putExtra("activityName","ShoppingCartActivity");
                        ((Activity) context.get()).startActivityForResult(i,context.get().getResources().getInteger(R.integer.CART_REQUEST_CODE));
                    }
                });
            }
        });

    }

    public void set (List<Product> filtered){
        products = filtered;
        super.notifyDataSetChanged();
    }


    //RETURN FILTER OBJposition
    public Filter getFilter() {
        if(filter==null)
        {
            filter=new CustomFilter(products,this);
        }
        return filter;
    }


}
