package com.code.dima.happygrocery.database;

import android.content.Context;
import android.os.AsyncTask;

import com.code.dima.happygrocery.model.Product;

import java.lang.ref.WeakReference;

public class UpdateProductQuantityInDBTask extends AsyncTask<Void, Void, Void> {

    private WeakReference<Context> context;
    private Product product;
    private int quantity;

    public UpdateProductQuantityInDBTask(Context context, Product product, int quantity) {
        this.context = new WeakReference<>(context);
        this.product = product;
        this.quantity = quantity;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        if (product != null && quantity > 0) {
            DatabaseAdapter adapter = DatabaseAdapter.openInWriteMode(context.get());
            adapter.updateProductQuantity(product, quantity);
            adapter.close();
        }
        return null;
    }
}
