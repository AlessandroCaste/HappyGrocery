package com.code.dima.happygrocery.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;

import com.code.dima.happygrocery.exception.NoSuchProductException;
import com.code.dima.happygrocery.model.Category;
import com.code.dima.happygrocery.model.GroceryDetails;
import com.code.dima.happygrocery.model.Product;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DatabaseAdapter {

    private Context context;
    private DatabaseHelper helper;
    private SQLiteDatabase database;

    private long actualGroceryID;


    private DatabaseAdapter(Context context) {
        this.context = context;
        this.actualGroceryID = -1;
    }


    public static DatabaseAdapter openInReadMode(Context context) throws SQLException

    {
        DatabaseAdapter adapter = new DatabaseAdapter(context);
        adapter.helper = new DatabaseHelper(context);
        adapter.database = adapter.helper.getReadableDatabase();
        return adapter;
    }


    public static DatabaseAdapter openInWriteMode(Context context) throws SQLException {
        DatabaseAdapter adapter = new DatabaseAdapter(context);
        adapter.helper = new DatabaseHelper(context);
        adapter.database = adapter.helper.getWritableDatabase();
        return adapter;
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
        if (actualGroceryID < 0) {
            if (database != null) {
                Cursor cursor = database.rawQuery("SELECT * FROM " + DatabaseConstants.HISTORY_TABLE
                        + " WHERE " + DatabaseConstants.HISTORY_ACTIVE + " = 1", null);
                cursor.moveToNext();
                actualGroceryID = cursor.getLong(cursor.getColumnIndex(DatabaseConstants.HISTORY_ID));
                cursor.close();
            }
        }
        return actualGroceryID;
    }

    private long queryProductIDWithBarcode(String barcode) throws NoSuchProductException {
        long barLong = Long.parseLong(barcode);
        Cursor cursor = database.rawQuery("SELECT * FROM " + DatabaseConstants.PRODUCT_TABLE
                + " WHERE " + DatabaseConstants.PRODUCT_ID + " = " + barLong, null);
        if (cursor.getCount() == 0)
            throw new NoSuchProductException();
        cursor.moveToNext();
        long productID = cursor.getLong(cursor.getColumnIndex(DatabaseConstants.PRODUCT_ID));
        cursor.close();
        return productID;
    }

    private Cursor queryProductAlreadyInGroceryList(long productID, long groceryID) {
        Cursor cursor = database.rawQuery("SELECT * FROM " + DatabaseConstants.LIST_TABLE
                + " WHERE " + DatabaseConstants.LIST_HID + " = " + groceryID
                + " AND " + DatabaseConstants.LIST_PID + " = " + productID, null);
        return cursor;
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

            // choose whether to create a new entry in productlist or to update a previous one
            long groceryID = queryGroceryID();
            Cursor cursor = queryProductAlreadyInGroceryList(productID, groceryID);
            if(cursor.moveToNext()) {
                int oldQuantity = cursor.getInt(cursor.getColumnIndex(DatabaseConstants.LIST_QUANTITY));
                int newQuantity = oldQuantity + product.getQuantity();
                updateProductQuantity(product, newQuantity);
            } else {
                ContentValues values = createProductListContentValues(groceryID, productID, product.getQuantity(), product.getPrice());
                database.insert(DatabaseConstants.LIST_TABLE, null, values);
            }
            cursor.close();
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
    private void updateProduct(Product product) {
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

    public List<Integer> queryNumberOfProductsPerCategory(List<String> categoryNames) {
        Integer[] productsPerCategory = new Integer[categoryNames.size()];
        for (int i = 0; i < productsPerCategory.length; i ++) {
            productsPerCategory[i] = new Integer(0);
        }
        if (database != null) {
            long groceryID = queryGroceryID();
            Cursor cursor = database.rawQuery(DatabaseConstants.QUERY_PRODUCTS_PER_CATEGORY,
                    new String[] {String.valueOf(groceryID)});
            while (cursor.moveToNext()) {
                String category = cursor.getString(cursor.getColumnIndex(DatabaseConstants.PRODUCT_CATEGORY));
                int index = categoryNames.indexOf(category);
                if (index >= 0)
                    productsPerCategory[index] = cursor.getInt(1);
            }
            cursor.close();
        }
        return Arrays.asList(productsPerCategory);
    }

    public List<Product> queryProductList() {
        ArrayList<Product> products = new ArrayList<>();
        if (database != null) {
            long groceryID = queryGroceryID();
            Cursor cursor = database.rawQuery(DatabaseConstants.QUERY_PRODUCT_LIST, new String[] {String.valueOf(groceryID)});
            while (cursor.moveToNext()) {
                String category = cursor.getString(cursor.getColumnIndex(DatabaseConstants.PRODUCT_CATEGORY));
                String name = cursor.getString(cursor.getColumnIndex(DatabaseConstants.PRODUCT_NAME));
                float price = cursor.getFloat(cursor.getColumnIndex(DatabaseConstants.LIST_PRICE));
                String barcode = cursor.getString(cursor.getColumnIndex(DatabaseConstants.PRODUCT_ID));
                float weight = cursor.getFloat(cursor.getColumnIndex(DatabaseConstants.PRODUCT_WEIGHT));
                int quantity = cursor.getInt(cursor.getColumnIndex(DatabaseConstants.LIST_QUANTITY));
                products.add(new Product(Category.valueOf(category), name, price, barcode, weight, quantity, 0));
            }
            cursor.close();
        }
        return products;
    }

    public List<GroceryDetails> queryGroceries() {
        ArrayList<GroceryDetails> groceries = new ArrayList<>();
        if (database != null) {
            Cursor cursor = database.rawQuery(DatabaseConstants.QUERY_GROCERIES, null);
            while (cursor.moveToNext()) {
                float amount = cursor.getFloat(cursor.getColumnIndex(DatabaseConstants.HISTORY_AMOUNT));
                String supermarket = cursor.getString(cursor.getColumnIndex(DatabaseConstants.HISTORY_MARKET));
                String date = cursor.getString(cursor.getColumnIndex(DatabaseConstants.HISTORY_DATE));
                groceries.add(new GroceryDetails(amount, supermarket, date));
            }
            cursor.close();
        }
        return groceries;
    }

    public Cursor querySQL(String sql) {
        Cursor cursor = null;
        if(database != null) {
            cursor = database.rawQuery(sql, null);
        }
        return cursor;
    }
}
