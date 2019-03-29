package com.code.dima.happygrocery.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.code.dima.happygrocery.database.DatabaseAdapter;

import java.lang.ref.WeakReference;

public class EndGroceryTask extends AsyncTask<Void, Void, Void> {

    private WeakReference<Context> context;

    public EndGroceryTask(Context context) {
        this.context = new WeakReference<>(context);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        DatabaseAdapter adapter = DatabaseAdapter.openInWriteMode(context.get());
        adapter.finishGrocery();
        adapter.close();
        return null;
    }
}
