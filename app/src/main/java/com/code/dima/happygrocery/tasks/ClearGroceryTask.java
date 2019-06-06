package com.code.dima.happygrocery.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.code.dima.happygrocery.database.DatabaseAdapter;
import com.code.dima.happygrocery.wearable.CommunicationHandler;

import java.lang.ref.WeakReference;


public class ClearGroceryTask extends AsyncTask<Void, Void, Void> {

    private WeakReference<Context> context;

    public ClearGroceryTask(Context context) {
        this.context = new WeakReference<>(context);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        DatabaseAdapter adapter = DatabaseAdapter.openInWriteMode(context.get());
        adapter.clearGrocery();
        adapter.close();
        CommunicationHandler.getInstance(context.get()).notifyGroceryCleared(context.get());
        return null;
    }
}
