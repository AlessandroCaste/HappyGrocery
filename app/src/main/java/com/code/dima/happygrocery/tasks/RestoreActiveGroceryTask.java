package com.code.dima.happygrocery.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.code.dima.happygrocery.database.DatabaseAdapter;
import com.code.dima.happygrocery.model.Product;
import com.code.dima.happygrocery.model.ShoppingCart;

import java.util.List;

public class RestoreActiveGroceryTask extends AsyncTask<Void, Void, Void> {

    private Context context;

    public RestoreActiveGroceryTask(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        ShoppingCart cart = ShoppingCart.getInstance();
        DatabaseAdapter adapter = DatabaseAdapter.openInWriteMode(context);
        List<Product> productList = adapter.queryProductList();
        for(Product product: productList)
            cart.addProduct(product);
        adapter.close();
        return null;
    }
}
