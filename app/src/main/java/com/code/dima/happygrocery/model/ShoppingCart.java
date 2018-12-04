package com.code.dima.happygrocery.model;

import com.code.dima.happygrocery.exception.NoLastProductException;
import com.code.dima.happygrocery.exception.NoSuchCategoryException;
import com.code.dima.happygrocery.exception.NoSuchProductException;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCart {

    private static ShoppingCart instance = null;

    private ArrayList<String> categoryNames;
    private ArrayList<ArrayList<Product>> shoppingCart;

    private Product lastProduct;


    private ShoppingCart() {
        shoppingCart = new ArrayList<>();
        categoryNames = new ArrayList<>();
        for (Category category : Category.values()) {
            ArrayList<Product> initialList = new ArrayList<>();
            shoppingCart.add(initialList);
            categoryNames.add(category.name());
        }
        lastProduct = new Product();

    }


    public static ShoppingCart getInstance() {
        if (instance == null)
            instance = new ShoppingCart();
        return instance;
    }


    private int getCategoryIndex (String categoryName) throws NoSuchCategoryException {
        if (! categoryNames.contains(categoryName))
            throw new NoSuchCategoryException();
        return categoryNames.indexOf(categoryName);
    }

    public int getNumberofProducts() {
        int count = 0;
        for (ArrayList<Product> list : shoppingCart)
            count += list.size();
        return count;
    }
    public int getNumberOfCategories() {
        return shoppingCart.size();
    }


    public void addProduct(Product product) {
        int index;
        try {
            index = getCategoryIndex(product.getCategory().name());
        } catch (NoSuchCategoryException e) {
            index = categoryNames.indexOf(Category.OTHER.name());
        }
        shoppingCart.get(index).add(product);
        this.lastProduct = product;
    }

    public void removeProduct(Product product) throws NoSuchProductException {
        int index;
        try {
            index = getCategoryIndex(product.getCategory().name());
        } catch (NoSuchCategoryException e) {
            index = categoryNames.indexOf(Category.OTHER.name());
        }

        if (shoppingCart.get(index).contains(product)) {
            shoppingCart.remove(product);
        } else
            throw new NoSuchProductException();
        if (lastProduct == product) {
            lastProduct = null;
        }
    }


    public List<Product> getProductsInCategory(Category category) throws NoSuchCategoryException {
        int index = getCategoryIndex(category.name());
        return shoppingCart.get(index);
    }

    public List<Product> getProductsInCategory(String categoryName) throws NoSuchCategoryException {
        int index = getCategoryIndex(categoryName);
        return shoppingCart.get(index);
    }


    public List<Integer> getNumberOfProductsPerCategory() {
        ArrayList<Integer> productsPerCategory = new ArrayList<>();
        for (int i = 0; i < shoppingCart.size(); i ++) {
            productsPerCategory.add(shoppingCart.get(i).size());
        }
        return productsPerCategory;
    }

    public int getNumberOfProductsPerCategory(String category) throws NoSuchCategoryException{
        int index, count;
        index = getCategoryIndex(category);
        count = shoppingCart.get(index).size();
        return count;
    }


    public List<String> getCategoryNames() {
        return (List<String>) categoryNames.clone();
    }


    public Product getLastProduct() throws NoLastProductException {
        if (lastProduct == null)
            throw new NoLastProductException();
        return lastProduct;
    }

    public void setLastProduct(Product lastProduct) {
        this.lastProduct = lastProduct;
    }

    public Product get(String category, int position) throws NoSuchCategoryException {
        int index = getCategoryIndex(category);
        return shoppingCart.get(index).get(position);
    }
}
