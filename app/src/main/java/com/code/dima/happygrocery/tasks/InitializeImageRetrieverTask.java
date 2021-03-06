package com.code.dima.happygrocery.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.code.dima.happygrocery.model.ImageRetriever;

import java.lang.ref.WeakReference;

public class InitializeImageRetrieverTask extends AsyncTask<Void, Void, Void> {


    private WeakReference<Context> context;


    public InitializeImageRetrieverTask(Context context) {
        this.context = new WeakReference<>(context);
    }


    @Override
    protected Void doInBackground(Void... voids) {
        ImageRetriever.initialize(context.get());
        Log.d("Happy Grocery", "Image Retriever initialized!");
        return null;
    }
}
