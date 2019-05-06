package com.code.dima.happygrocery.database;

import android.content.Context;
import android.os.AsyncTask;

import com.code.dima.happygrocery.model.Product;

import java.lang.ref.WeakReference;

public class DeleteProductFromDBTask extends AsyncTask<Void, Void, Void> {

    private WeakReference<Context> context;
    private Product product;


    public DeleteProductFromDBTask(Context context, Product product) {
        this.context = new WeakReference<>(context);
        this.product = product;
    }


    @Override
    protected Void doInBackground(Void... voids) {
        if (product != null) {
            DatabaseAdapter adapter = DatabaseAdapter.openInWriteMode(context.get());
            adapter.deleteProductFromProductList(product);
            adapter.close();
        }
        return null;
    }
}
