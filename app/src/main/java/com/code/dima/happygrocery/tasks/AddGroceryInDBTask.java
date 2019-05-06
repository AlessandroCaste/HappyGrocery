package com.code.dima.happygrocery.tasks;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import com.code.dima.happygrocery.database.DatabaseAdapter;

import java.lang.ref.WeakReference;
import java.util.Calendar;

public class AddGroceryInDBTask extends AsyncTask<Void, Void, Void> {

    private WeakReference<Context> context;
    private String shopName;
    private String date;

    public AddGroceryInDBTask(Context context, String shop) {
        this.context = new WeakReference<>(context);
        this.shopName = shop;
        this.date = "11/07/2018";
    }

    @Override
    protected Void doInBackground(Void... voids) {
        // finds the actual date
        Calendar calendar = Calendar.getInstance();
        String year = String.valueOf(calendar.get(Calendar.YEAR));
        int m = calendar.get(Calendar.MONTH) + 1;
        int d = calendar.get(Calendar.DAY_OF_MONTH);
        String month;
        String day;
        if (m < 10) {
            month = "0" + m;
        } else {
            month = String.valueOf(m);
        }
        if (d < 10) {
            day = "0" + d;
        } else {
            day = String.valueOf(d);
        }
        date = day + "/" + month + "/" + year;
        Log.d("DATABASE", "Adding a new grocery with date: " + date);

        // if other active groceries are found, they are cleared out
        DatabaseAdapter adapter = DatabaseAdapter.openInWriteMode(context.get());
        Cursor c = adapter.querySQL("SELECT * FROM grocery_history WHERE active = 1");
        if (c.getCount() != 0) {
            adapter.clearGrocery();
        }
        adapter.insertNewGrocery(date, shopName);
        adapter.close();
        return null;
    }
}
