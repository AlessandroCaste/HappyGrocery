package com.code.dima.happygrocery.model;

import com.code.dima.happygrocery.exception.NoLastProductException;
import com.code.dima.happygrocery.exception.NoSuchProductException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.stream.Stream;

import static org.junit.Assert.*;

public class ShoppingCartTest {


    private Product dummyProduct1 = new Product(Category.FOOD, "Fragole", 15.5f, "1234567890100", 0.3f, 1, 5);
    private Product dummyProduct2 = new Product(Category.BEVERAGE, "Cola", 15.5f, "9874567890111", 0.3f, 1, 5);
    private Product dummyProduct3 = new Product(Category.CLOTHING, "Felpa", 15.5f, "1333256890100", 0.3f, 1, 5);

    @Test
    public void testInsertion() throws NoLastProductException, NoSuchProductException {
        //Adding a single product and checking it has been inserted correctly
        ShoppingCart sc = ShoppingCart.getInstance();
        sc.addProduct(dummyProduct1);

        assertEquals(dummyProduct1, sc.getLastProduct());

        //Removing product
        sc.removeProduct(dummyProduct1);
        sc.clearShoppingCart();
    }

    //Checking integrity with a huge number of products
    @Test
    public void testMultipleInsertion() {

        ShoppingCart sc = ShoppingCart.getInstance();

        for(int i = 0; i< 350; i++) {
            sc.addProduct(new Product(Category.FOOD, "Fragole", 15.5f, "1234567890100" + i, 0.3f, 1, 5));
            sc.addProduct(new Product(Category.BEVERAGE, "Mele", 15.5f, "9874567890111" + i, 0.3f, 1, 5));
            sc.addProduct(new Product(Category.CLOTHING, "Penne", 15.5f, "1333256890100" + i, 0.3f, 1, 5));
        }

        int counter = 0;

        for (Category val  : Category.values()) {
            counter += sc.getProductsInCategory(val).size();
        }
        System.out.println(counter);
        assertEquals(counter,350*3);
        sc.clearShoppingCart();
    }

    @Test
    public void updateQuantity() throws NoSuchProductException, NoLastProductException {

        ShoppingCart sc = ShoppingCart.getInstance();

        sc.addProduct(dummyProduct2);
        sc.updateQuantity(dummyProduct2,20);

        int counter = sc.getLastProduct().getQuantity();
        assertEquals(20, counter);
        sc.clearShoppingCart();
    }


    /*
    @Test
    public void removalTest() throws NoSuchProductException, NoLastProductException {

        ShoppingCart sc = ShoppingCart.getInstance();

        sc.addProduct(dummyProduct1);
        sc.addProduct(dummyProduct2);
        sc.addProduct(dummyProduct3);

        sc.removeProduct(dummyProduct3);
        assertEquals(sc.getLastProduct(),dummyProduct2);
        sc.clearShoppingCart();
    } */

    @Test
    public void clearing() {

        ShoppingCart sc = ShoppingCart.getInstance();

        sc.addProduct(dummyProduct1);
        sc.addProduct(dummyProduct2);
        sc.addProduct(dummyProduct3);

        sc.clearShoppingCart();
        sc = ShoppingCart.getInstance();
        sc.getProductsInCategory(Category.FOOD);

        int counter = 0;

        for (Category val  : Category.values()) {
            counter += sc.getProductsInCategory(val).size();
        }

        assertEquals(0,counter);
        sc.clearShoppingCart();
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }
}