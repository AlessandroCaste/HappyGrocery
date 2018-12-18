package com.code.dima.happygrocery.tasks;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

import com.code.dima.happygrocery.database.DatabaseAdapter;

public class AddGroceryInDBTask extends AsyncTask<Void, Void, Void> {

    private Context context;

    public AddGroceryInDBTask(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        // workaround to add a new active grocery to database
        DatabaseAdapter adapter = DatabaseAdapter.openInWriteMode(context);
        Cursor c = adapter.querySQL("SELECT * FROM grocery_history WHERE active = 1");
        if (c.getCount() == 0) {
            adapter.insertNewGrocery("11/07/2018", "Esselungone");
        }
        adapter.close();
        return null;
    }
}
