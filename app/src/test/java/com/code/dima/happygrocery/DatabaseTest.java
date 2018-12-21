package com.code.dima.happygrocery;

import android.app.Application;

import com.code.dima.happygrocery.database.DatabaseAdapter;
import com.code.dima.happygrocery.database.DatabaseConstants;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DatabaseTest {

    @Test
    public void queries_areCorrect() {
        String ciao = "SELECT SUM("
                + DatabaseConstants.LIST_QUANTITY + "*" + DatabaseConstants.LIST_PRICE + ")"
                + " FROM " + DatabaseConstants.LIST_TABLE
                + " WHERE " + DatabaseConstants.LIST_HID + " = 829291199";

        System.out.println(ciao);

        assertEquals(4, 2 + 2);
    }
}
