package com.code.dima.happygrocery.wearable;

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

    // capabilities
    public static final String WATCH_SERVER = "watch_server";
    public static final String WATCH_CLIENT = "watch_client";

    // action for broadcast intents sent by communication service
    public static final String ACTION_NOTIFICATION = "notification";
}
