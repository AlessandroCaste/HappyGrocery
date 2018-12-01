package com.code.dima.happygrocery;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.code.dima.happygrocery.database.DatabaseAdapter;
import com.code.dima.happygrocery.database.DatabaseConstants;
import com.code.dima.happygrocery.model.Category;
import com.code.dima.happygrocery.model.Product;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class DatabaseTest {

    @Before
    public void setUp() throws Exception {
        InstrumentationRegistry.getTargetContext().deleteDatabase(DatabaseConstants.DB_NAME);
    }


    @Test
    public void databaseCreationTest() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        try {
            DatabaseAdapter adapter = DatabaseAdapter.openInReadMode(appContext);
            adapter.close();
            assertTrue(true);
        } catch (SQLException e) {
            fail(e.getMessage());
        }
    }


    @Test
    public void databaseInsertGroceryTest() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        boolean answer = false;

        try {
            DatabaseAdapter adapter = DatabaseAdapter.openInWriteMode(appContext);
            adapter.insertNewGrocery("11/12/2018", "Carrefour");
            adapter.finishGrocery();
            Cursor cursor = adapter.querySQL("SELECT * FROM " + DatabaseConstants.HISTORY_TABLE);
            if (cursor.getCount() > 0)
                answer = true;
            adapter.close();
            assertTrue(answer);
        } catch (SQLException e) {
            fail(e.getMessage());
        }
    }


    @Test
    public void databaseInsertProductTest() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        boolean answer = false;

        try {
            DatabaseAdapter adapter = DatabaseAdapter.openInWriteMode(appContext);
            Product product = new Product(Category.FOOD, "Fragole", 1.2f, "900111223", 0.4f, 1, 0);
            adapter.insertNewGrocery("11/12/2018", "Carrefour");
            adapter.insertProductIntoProductList(product);
            Cursor cursor = adapter.querySQL("SELECT * FROM " + DatabaseConstants.LIST_TABLE);
            if (cursor.getCount() > 0)
                answer = true;
            adapter.finishGrocery();
            adapter.close();
            assertTrue(answer);
        } catch (SQLException e) {
            fail(e.getMessage());
        }
    }


    @Test
    public void databaseProductDeleteTest() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        boolean answer = false;

        try {
            DatabaseAdapter adapter = DatabaseAdapter.openInWriteMode(appContext);
            adapter.insertNewGrocery("11/12/2018", "Carrefour");

            Product product1 = new Product(Category.FOOD, "Fragole", 1.2f, "900111223", 0.4f, 1, 0);
            Product product2 = new Product(Category.FOOD, "Kiwy", 0.8f, "900111224", 0.5f, 1, 0);

            adapter.insertProductIntoProductList(product1);
            adapter.insertProductIntoProductList(product2);
            adapter.deleteProductFromProductList(product1);

            Cursor cursor = adapter.querySQL("SELECT * FROM " + DatabaseConstants.LIST_TABLE);
            if (cursor.getCount() == 1)
                answer = true;
            adapter.finishGrocery();
            adapter.close();
            assertTrue(answer);
        } catch (SQLException e) {
            fail(e.getMessage());
        }
    }


    @Test
    public void databaseUpdateQuantityTest() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        int qty = 0;

        try {
            DatabaseAdapter adapter = DatabaseAdapter.openInWriteMode(appContext);
            adapter.insertNewGrocery("11/12/2018", "Carrefour");

            Product product1 = new Product(Category.FOOD, "Fragole", 1.2f, "900111223", 0.4f, 1, 0);

            adapter.insertProductIntoProductList(product1);
            adapter.updateProductQuantity(product1, 2);
            Cursor cursor = adapter.querySQL("SELECT * FROM " + DatabaseConstants.LIST_TABLE);
            if (cursor.getCount() == 1) {
                cursor.moveToNext();
                qty = cursor.getInt(cursor.getColumnIndex(DatabaseConstants.LIST_QUANTITY));
            }
            adapter.finishGrocery();
            adapter.close();
            assertTrue(2 == qty);
        } catch (SQLException e) {
            fail(e.getMessage());
        }
    }


    @Test
    public void databaseProductPerCategoryTest() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        boolean answer = false;

        try {
            DatabaseAdapter adapter = DatabaseAdapter.openInWriteMode(appContext);
            adapter.insertNewGrocery("11/12/2018", "Carrefour");

            Product product1 = new Product(Category.FOOD, "Fragole", 1.2f, "900111223", 0.4f, 1, 0);
            Product product2 = new Product(Category.BEVERAGE, "Vino", 12f, "900111224", 1.1f, 1, 0);
            Product product3 = new Product(Category.CLOTHING, "Tshirt", 15f, "900111225", 0.6f, 1, 0);

            adapter.insertProductIntoProductList(product1);
            adapter.insertProductIntoProductList(product2);
            adapter.insertProductIntoProductList(product3);

            ArrayList<String> names = new ArrayList<>();
            names.add(Category.FOOD.name());
            names.add(Category.BEVERAGE.name());
            names.add(Category.CLOTHING.name());
            List<Integer> prodPerCategory = adapter.queryNumberOfProductsPerCategory(names);
            Log.d("PRODUCTS_PER_CATEGORY",prodPerCategory.toString());
            if (prodPerCategory.size() == 3 && prodPerCategory.get(0) == 1
                    && prodPerCategory.get(1) == 1 && prodPerCategory.get(2) == 1)
                answer = true;

            adapter.finishGrocery();
            adapter.close();
            assertTrue(answer);
        } catch (SQLException e) {
            fail(e.getMessage());
        }
    }


    @Test
    public void databaseProductListTest() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        boolean answer = false;

        try {
            DatabaseAdapter adapter = DatabaseAdapter.openInWriteMode(appContext);
            adapter.insertNewGrocery("11/12/2018", "Carrefour");

            Product product1 = new Product(Category.FOOD, "Fragole", 1.2f, "900111223", 0.4f, 1, 0);
            Product product2 = new Product(Category.BEVERAGE, "Vino", 12f, "900111224", 1.1f, 1, 0);
            Product product3 = new Product(Category.CLOTHING, "Tshirt", 15f, "900111225", 0.6f, 1, 0);

            adapter.insertProductIntoProductList(product1);
            adapter.insertProductIntoProductList(product2);
            adapter.insertProductIntoProductList(product3);

            List<Product> list = adapter.queryProductList();
            Log.d("PRODUCT_LIST",list.toString());
            if (list.size() == 3)
                answer = true;

            adapter.finishGrocery();
            adapter.close();
            assertTrue(answer);
        } catch (SQLException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void newQueryTest() {
        try {
            Context appContext = InstrumentationRegistry.getTargetContext();
            DatabaseAdapter adapter = DatabaseAdapter.openInWriteMode(appContext);
            Cursor c = adapter.querySQL("SELECT * FROM grocery_history WHERE active = 1");
            if (c.getCount() == 0) {
                adapter.insertNewGrocery("11/07/2018", "Esselungone");
            }
            adapter.close();
            assertTrue(true);
        } catch (SQLException e) {
            fail(e.getMessage());
        }
    }

}
