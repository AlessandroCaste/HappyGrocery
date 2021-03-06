package com.code.dima.happygrocery.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.code.dima.happygrocery.database.DatabaseAdapter;
import com.code.dima.happygrocery.model.ImageRetriever;
import com.code.dima.happygrocery.model.Product;
import com.code.dima.happygrocery.model.ShoppingCart;

import java.lang.ref.WeakReference;
import java.util.List;

public class RestoreActiveGroceryTask extends AsyncTask<Void, Void, Void> {

    private WeakReference<Context> context;

    public RestoreActiveGroceryTask(Context context) {
        this.context = new WeakReference<>(context);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        ShoppingCart cart = ShoppingCart.getInstance();
        DatabaseAdapter adapter = DatabaseAdapter.openInWriteMode(context.get());
        List<Product> productList = adapter.queryProductList();
        ImageRetriever imageRetriever = ImageRetriever.getInstance(context.get());
        int id;
        for(Product product: productList) {
            id = imageRetriever.retrieveImageID(product.getName(), product.getCategory());
            product.setImageID(id);
            cart.addProduct(product);
        }
        adapter.close();
        return null;
    }
}
