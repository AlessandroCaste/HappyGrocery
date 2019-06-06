package com.code.dima.happygrocery.utils;

public class DataPaths {
    // paths used in DataMaps
    public static final String AMOUNT_PATH = "/amount";
    public static final String QUANTITIES_PATH = "/quantities";

    //keys of the items in the DataMaps
    public static final String AMOUNT_KEY = "com.code.dima.happygrocery.amount";
    public static final String QUANTITIES_KEY = "com.code.dima.happygrocery.quantities";

    // message keys
    public static final String NOTIFY_CONNECTED = "/notify_connection_from_wearable";
    public static final String NOTIFY_NEW_GROCERY = "/notify_new_grocery_from_phone";
    public static final String NOTIFY_NEW_PRODUCT = "/notify_new_product_from_phone";
    public static final String NOTIFY_GROCERY_CLOSED = "/notify_grocery_closed_from_phone";
    public static final String NOTIFY_GROCERY_CLEARED = "/notify_grocery_cleared_from_phone";

    // capabilities
    public static final String WATCH_SERVER = "watch_server";
    public static final String WATCH_CLIENT = "watch_client";

    // action for broadcast intents sent by communication service
    public static final String ACTION_NOTIFICATION = "notification";
    public static final String ACTION_CONNECTED = "connected";
    public static final String ACTION_DISCONNECTED = "disconnected";
    public static final String ACTION_UPDATE_AMOUNT = "update_amount";
    public static final String ACTION_UPDATE_QUANTITIES = "update_quantities";
}
