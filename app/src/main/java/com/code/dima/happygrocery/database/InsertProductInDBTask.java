package com.code.dima.happygrocery.database;

import android.content.Context;
import android.os.AsyncTask;

import com.code.dima.happygrocery.model.Product;

import java.lang.ref.WeakReference;

public class InsertProductInDBTask extends AsyncTask<Void, Void, Void> {

    private WeakReference<Context> context;
    private Product product;
    private int quantity;

    public InsertProductInDBTask(Context context, Product product) {
        this.context = new WeakReference<>(context);
        this.product = product;
        this.quantity = product.getQuantity();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        if (product != null) {
            DatabaseAdapter adapter = DatabaseAdapter.openInWriteMode(context.get());
            adapter.insertProductIntoProductList(product);
            adapter.updateProductQuantity(product, quantity);
            adapter.close();
        }
        return null;
    }
}
