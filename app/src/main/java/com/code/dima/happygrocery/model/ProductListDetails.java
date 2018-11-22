package com.code.dima.happygrocery.model;

import java.util.ArrayList;
import java.util.List;

public class ProductListDetails {

    private static ProductListDetails instance = null;

    private ArrayList<Integer> productPerCategory;
    private ArrayList<String> categoryNames;
    private Product lastProduct;

    private ProductListDetails() {
        productPerCategory = new ArrayList<>();
        categoryNames = new ArrayList<>();
        for (Category category : Category.values()) {
            productPerCategory.add(new Integer(1));
            categoryNames.add(category.name());
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
        String label = category.name();
        if (categoryNames.contains(label)) {
            int index = categoryNames.indexOf(label);
            int newValue = productPerCategory.get(index) + 1;
            productPerCategory.set(index, newValue);
        }
    }


    public List<Integer> getProductPerCategory() {
        return (List<Integer>) productPerCategory.clone();
    }

    public List<String> getCategoriesNames() {
        return (List<String>) categoryNames.clone();
    }


    public Product getLastProduct() {
        return lastProduct;
    }

    public void setLastProduct(Product lastProduct) {
        this.lastProduct = lastProduct;
    }
}
