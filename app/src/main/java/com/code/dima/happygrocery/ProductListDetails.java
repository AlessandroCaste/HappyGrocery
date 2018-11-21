package com.code.dima.happygrocery;

import java.util.ArrayList;
import java.util.List;

public class ProductListDetails {


    private static ProductListDetails instance = null;
    private ArrayList<Integer> productPerCategory;
    private Product lastProduct;


    private ProductListDetails() {
        productPerCategory = new ArrayList<>();
        for (int i = 0; i < Category.values().length; i ++) {
            productPerCategory.add(new Integer(1));
        }
        lastProduct = new Product();

    }


    public static ProductListDetails getInstance() {
        if (instance == null)
            instance = new ProductListDetails();
        return instance;
    }


    public int getNumberOfCategories() {
        return productPerCategory.size();
    }


    public void addProduct(Category category) {
        int newValue = productPerCategory.get(category.ordinal()) + 1;
        productPerCategory.set(category.ordinal(), newValue);
    }


    public List<Integer> getProductPerCategory() {
        return (List<Integer>) productPerCategory.clone();
    }


    public Product getLastProduct() {
        return lastProduct;
    }

    public void setLastProduct(Product lastProduct) {
        this.lastProduct = lastProduct;
    }
}
