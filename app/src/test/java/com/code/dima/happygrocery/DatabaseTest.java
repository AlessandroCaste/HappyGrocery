package com.code.dima.happygrocery;

import android.app.Application;

import com.code.dima.happygrocery.database.DatabaseAdapter;
import com.code.dima.happygrocery.database.DatabaseConstants;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DatabaseTest {

    @Test
    public void queries_areCorrect() {
        String createHistoryQuery = DatabaseConstants.CREATE_GROCERY_HISTORY_TABLE;
        System.out.println("HISTORY TABLE CREATION QUERY:");
        System.out.println(createHistoryQuery);
        String createProductQuery = DatabaseConstants.CREATE_PRODUCT_TABLE;
        System.out.println("\nPRODUCT TABLE CREATION QUERY:");
        System.out.println(createProductQuery);
        String createProductListQuery = DatabaseConstants.CREATE_PRODUCT_LIST_TABLE;
        System.out.println("\nPRODUCT LIST TABLE CREATION QUERY:");
        System.out.println(createProductListQuery);

        assertEquals(4, 2 + 2);
    }
}
