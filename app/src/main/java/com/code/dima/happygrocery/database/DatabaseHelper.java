package com.code.dima.happygrocery.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DatabaseHelper extends SQLiteOpenHelper {

    private static DatabaseHelper instance = null;


    public DatabaseHelper(Context context) {
        super(context, DatabaseConstants.DB_NAME, null, DatabaseConstants.DB_VERSION);
    }

    public static DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DatabaseConstants.CREATE_GROCERY_HISTORY_TABLE);
        db.execSQL(DatabaseConstants.CREATE_PRODUCT_TABLE);
        db.execSQL(DatabaseConstants.CREATE_PRODUCT_LIST_TABLE);
    }


    @Override
    public void onConfigure(SQLiteDatabase db) {
        db.setForeignKeyConstraintsEnabled(true);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + DatabaseConstants.HISTORY_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + DatabaseConstants.PRODUCT_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + DatabaseConstants.LIST_TABLE);
            onCreate(db);
        }
    }
}
