package com.code.dima.happygrocery.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.RecyclerView;
import android.widget.Filter;

import com.code.dima.happygrocery.core.DashboardActivity;
import com.code.dima.happygrocery.core.ProductActivity;
import com.code.dima.happygrocery.core.ShoppingCartActivity;
import com.code.dima.happygrocery.database.DatabaseAdapter;
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
    public void onBindViewHolder(final ProductHolder holder, int position) {
        final Product product = products.get(position);
        holder.setDetails(product);

        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                holder.removeButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        DatabaseAdapter adapter = DatabaseAdapter.openInWriteMode(context);
                        boolean notEmpty = product.decreaseQuantity();
                        if (notEmpty) {
                            holder.setDetails(product);
                            adapter.updateProductQuantity(product, product.getQuantity());
                       } else {
                            int removedPosition = holder.getAdapterPosition();
                            products.remove(removedPosition);
                            notifyItemRemoved(removedPosition);
                            adapter.deleteProductFromProductList(product);
                        } adapter.close();
                    }
                });
            }
        });

        handler.post(new Runnable() {
            @Override
            public void run() {
                holder.editButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent i = new Intent(context,ProductActivity.class);
                        i.putExtra("activityName","ShoppingCartActivity");
                        context.startActivity(i);
                    }
                });
            }
        });

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
