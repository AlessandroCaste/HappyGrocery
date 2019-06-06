package com.code.dima.happygrocery.wearable;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.code.dima.happygrocery.exception.NoLastProductException;
import com.code.dima.happygrocery.model.Product;
import com.code.dima.happygrocery.model.ShoppingCart;

import java.lang.ref.WeakReference;

public class WearableUpdateTask extends AsyncTask<Void, Void, Void> {

    private WeakReference<Context> context;
    private boolean lastProductNotification;

    public WearableUpdateTask(Context context, boolean lastProductNotification) {
        this.context = new WeakReference<>(context);
        this.lastProductNotification = lastProductNotification;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        CommunicationHandler handler = CommunicationHandler.getInstance(context.get());
        ShoppingCart cart = ShoppingCart.getInstance();
        if (lastProductNotification) {
            try {
                Product lastProduct = cart.getLastProduct();
                handler.notifyNewProductAdded(context.get(), lastProduct);
            } catch (NoLastProductException e) {
                Log.d("Update task", "Error while retrieving last product");
            }
        }
        handler.updateWearableAmount(context.get(), cart.getAmount());
        handler.updateWearableQuantities(context.get(), cart.getNumberOfProductsPerCategory());
        return null;
    }
}
