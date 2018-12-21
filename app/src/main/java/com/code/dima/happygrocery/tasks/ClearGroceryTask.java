package com.code.dima.happygrocery.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.code.dima.happygrocery.database.DatabaseAdapter;


public class ClearGroceryTask extends AsyncTask<Void, Void, Void> {

    private Context context;

    public ClearGroceryTask(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        DatabaseAdapter adapter = DatabaseAdapter.openInWriteMode(context);
        adapter.clearGrocery();
        adapter.close();
        return null;
    }
}
