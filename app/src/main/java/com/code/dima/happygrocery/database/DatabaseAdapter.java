package com.code.dima.happygrocery.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;

import com.code.dima.happygrocery.exception.NoSuchProductException;
import com.code.dima.happygrocery.model.Product;

import java.util.ArrayList;

public class DatabaseAdapter {

    private Context context;
    private DatabaseHelper helper;
    private SQLiteDatabase database;


    public DatabaseAdapter(Context context) {
        this.context = context;
    }


    public DatabaseAdapter openInReadMode(Context context) {
        helper = new DatabaseHelper(context);
        database = helper.getReadableDatabase();
        return this;
    }


    public DatabaseAdapter openInWriteMode(Context context) {
        helper = new DatabaseHelper(context);
        database = helper.getWritableDatabase();
        return this;
    }


    public void close() {
        helper.close();
    }


    private ContentValues createGroceryContentValues(String date, String market) {
        ContentValues values = new ContentValues();
        values.put(DatabaseConstants.HISTORY_DATE, date);
        values.put(DatabaseConstants.HISTORY_MARKET, market);
        values.put(DatabaseConstants.HISTORY_AMOUNT, 0f);
        values.put(DatabaseConstants.HISTORY_ACTIVE, 1);
        return values;
    }

    private ContentValues createProductContentValues(String barcode, String name, float weight, String category) {
        ContentValues values = new ContentValues();
        values.put(DatabaseConstants.PRODUCT_ID, Long.parseLong(barcode));
        values.put(DatabaseConstants.PRODUCT_NAME, name);
        values.put(DatabaseConstants.PRODUCT_WEIGHT, weight);
        values.put(DatabaseConstants.PRODUCT_CATEGORY, category);
        return values;
    }

    private ContentValues createProductListContentValues(long groceryID, long productID, int quantity, float price) {
        ContentValues values = new ContentValues();
        values.put(DatabaseConstants.LIST_HID, groceryID);
        values.put(DatabaseConstants.LIST_PID, productID);
        values.put(DatabaseConstants.LIST_QUANTITY, quantity);
        values.put(DatabaseConstants.LIST_PRICE, price);
        return values;
    }

    private long queryGroceryID() {
        long groceryID;
        Cursor cursor = database.rawQuery("SELECT * FROM " + DatabaseConstants.HISTORY_TABLE
                + " WHERE " + DatabaseConstants.HISTORY_ACTIVE + " = 1", null);
        groceryID = cursor.getLong(cursor.getColumnIndex(DatabaseConstants.HISTORY_ID));
        cursor.close();
        return groceryID;
    }

    private long queryProductIDWithBarcode(String barcode) throws NoSuchProductException {
        long barLong = Long.parseLong(barcode);
        Cursor cursor = database.rawQuery("SELECT * FROM " + DatabaseConstants.PRODUCT_TABLE
                + " WHERE " + DatabaseConstants.PRODUCT_ID + " = " + barLong, null);
        if (cursor.getCount() == 0)
            throw new NoSuchProductException();
        long productID = cursor.getLong(cursor.getColumnIndex(DatabaseConstants.PRODUCT_ID));
        cursor.close();
        return productID;
    }


    public void insertNewGrocery(String date, String market) {
        if (database != null) {
            ContentValues values = createGroceryContentValues(date, market);
            database.insert(DatabaseConstants.HISTORY_TABLE, null, values);
        }
    }

    // used everytime the user puts a product into the product list
    public void insertProductIntoProductList(Product product) {
        if (database != null) {
            // choose whether the product must be added to database
            long productID;
            try {
                productID = queryProductIDWithBarcode(product.getBarcode());
            } catch (NoSuchProductException e) {
                ContentValues pValues = createProductContentValues(
                        product.getBarcode(), product.getName(),
                        product.getWeight(), product.getCategory().name());
                productID = database.insert(DatabaseConstants.PRODUCT_TABLE, null, pValues);
            }

            long groceryID = queryGroceryID();

            ContentValues values = createProductListContentValues(groceryID, productID, product.getQuantity(), product.getPrice());
            database.insert(DatabaseConstants.LIST_TABLE, null, values);
        }
    }

    // used everytime the user removes a product from the product list
    public void deleteProductFromProductList(Product product) {
        if (database != null) {
            long groceryID = queryGroceryID();
            try {
                long productID = queryProductIDWithBarcode(product.getBarcode());
                database.delete(DatabaseConstants.LIST_TABLE,
                        DatabaseConstants.LIST_HID + " = " + groceryID
                                + " AND " + DatabaseConstants.LIST_PID + " = " + productID, null);
            } catch (NoSuchProductException e) {
                e.printStackTrace();
            }
        }
    }

    // used everytime the user modifies the quantity of a product in the product list
    public void updateProductQuantity(Product product, int newQuantity) {
        if (database != null) {
            long groceryID = queryGroceryID();
            try {
                long productID = queryProductIDWithBarcode(product.getBarcode());
                ContentValues values = new ContentValues();
                values.put(DatabaseConstants.LIST_QUANTITY, newQuantity);
                database.update(DatabaseConstants.LIST_TABLE, values,
                        DatabaseConstants.LIST_HID + " = ? AND "
                         + DatabaseConstants.LIST_PID + " = ?",
                        new String[] {String.valueOf(groceryID), String.valueOf(productID)});
            } catch (NoSuchProductException e) {
                e.printStackTrace();
            }
        }
    }

    // used by the system to update information about a product
    public void updateProduct(Product product) {
        if (database != null) {
            try {
                long productID = queryProductIDWithBarcode(product.getBarcode());
                ContentValues values = createProductContentValues(
                        product.getBarcode(), product.getName(), product.getWeight(), product.getCategory().name()
                );
                database.update(DatabaseConstants.PRODUCT_TABLE, values,
                        DatabaseConstants.PRODUCT_ID + " = " + productID, null);
            } catch (NoSuchProductException e) {
                e.printStackTrace();
            }
        }
    }

    // set attribute "active" of the active grocery to 0
    public void finishGrocery() {
        if (database != null) {
            long groceryID = queryGroceryID();
            ContentValues value = new ContentValues();
            value.put(DatabaseConstants.HISTORY_ACTIVE, 0);
            database.update(DatabaseConstants.HISTORY_TABLE, value,
                    DatabaseConstants.HISTORY_ID + " = " + groceryID, null);
        }
    }

    public ArrayList<Integer> queryNumberOfProductsPerCategory() {
        return null;
    }

    public Cursor queryProductList() {
        return null;
    }
}