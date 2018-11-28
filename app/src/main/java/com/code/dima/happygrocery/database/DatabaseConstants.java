package com.code.dima.happygrocery.database;

public class DatabaseConstants {


    // this class is not going to be instantiated
    private DatabaseConstants () {
        return;
    }


    // global constants
    public static final String DB_NAME = "happy_grocery.db";
    public static final int DB_VERSION = 1;


    // Grocery history column names
    public static final String HISTORY_TABLE = "grocery_history";
    public static final String HISTORY_ID = "id";
    public static final String HISTORY_DATE = "date";
    public static final String HISTORY_MARKET = "market";
    public static final String HISTORY_AMOUNT = "amount";
    public static final String HISTORY_ACTIVE = "active";

    // Product column names
    public static final String PRODUCT_TABLE = "product";
    public static final String PRODUCT_ID = "barcode";
    public static final String PRODUCT_NAME = "name";
    public static final String PRODUCT_WEIGHT = "weight";
    public static final String PRODUCT_CATEGORY = "category";

    // Product list column names
    public static final String LIST_TABLE = "product_list";
    public static final String LIST_HID = "h_id";
    public static final String LIST_PID = "p_id";
    public static final String LIST_QUANTITY = "quantity";
    public static final String LIST_PRICE = "price";


    // create queries used to instantiate tables in the database
    public static final String CREATE_GROCERY_HISTORY_TABLE =
            "CREATE TABLE " + HISTORY_TABLE + "("
            + HISTORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + HISTORY_DATE + " TEXT, "
            + HISTORY_MARKET + " TEXT, "
            + HISTORY_AMOUNT + " REAL, "
            + HISTORY_ACTIVE + " INTEGER)";

    public static final String CREATE_PRODUCT_TABLE =
            "CREATE TABLE " + PRODUCT_TABLE + "("
                    + PRODUCT_ID + " INTEGER PRIMARY KEY, "
                    + PRODUCT_NAME + " TEXT, "
                    + PRODUCT_WEIGHT + " REAL, "
                    + PRODUCT_CATEGORY + " TEXT)";

    public static final String CREATE_PRODUCT_LIST_TABLE =
            "CREATE TABLE " + LIST_TABLE + "("
                + LIST_HID + " INTEGER REFERENCES grocery_history(id) NOT NULL, "
                + LIST_PID + " INTEGER REFERENCES product(barcode) NOT NULL, "
                + LIST_QUANTITY + " INTEGER, "
                + LIST_PRICE + " REAL, "
                + "PRIMARY KEY (h_id, p_id))";

    public static final String QUERY_PRODUCTS_PER_CATEGORY =
            "SELECT " + PRODUCT_CATEGORY + ", COUNT(*) FROM "
            + LIST_TABLE + " JOIN " + PRODUCT_TABLE + " ON " + LIST_PID + " = " + PRODUCT_ID
            + " WHERE " + HISTORY_ID + " = ? GROUP BY " + PRODUCT_CATEGORY;

    public static final String QUERY_PRODUCT_LIST =
            "SELECT * FROM "
                    + LIST_TABLE + " JOIN " + PRODUCT_TABLE + " ON " + LIST_PID + " = " + PRODUCT_ID
                    + " WHERE " + HISTORY_ID + " = ?";
}
