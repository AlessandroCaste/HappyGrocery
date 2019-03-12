package com.code.dima.happygrocery.tasks;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

import com.code.dima.happygrocery.database.DatabaseAdapter;

import java.lang.ref.WeakReference;

public class AddGroceryInDBTask extends AsyncTask<Void, Void, Void> {

    private WeakReference<Context> context;
    private String shopName;
    private String date;

    public AddGroceryInDBTask(Context context, String shop) {
        this.context = new WeakReference<>(context);
        this.shopName = shop;
        this.date = "11/07/2018";
    }

    public AddGroceryInDBTask(Context context, String shop, String date) {
        this.context = new WeakReference<>(context);
        this.shopName = shop;
        this.date = date;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        // workaround to add a new active grocery to database
        DatabaseAdapter adapter = DatabaseAdapter.openInWriteMode(context.get());
        Cursor c = adapter.querySQL("SELECT * FROM grocery_history WHERE active = 1");
        if (c.getCount() == 0) {
            adapter.insertNewGrocery(date, shopName);
        } else {
            adapter.clearGrocery();
        }
        adapter.close();
        return null;
    }
}
