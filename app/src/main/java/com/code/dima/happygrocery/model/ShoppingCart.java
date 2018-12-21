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
    private float amount;


    private ShoppingCart() {
        shoppingCart = new ArrayList<>();
        categoryNames = new ArrayList<>();
        for (Category category : Category.values()) {
            ArrayList<Product> initialList = new ArrayList<>();
            shoppingCart.add(initialList);
            categoryNames.add(category.name());
        }
        lastProduct = null;
        amount = 0f;
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


    public void addProduct(Product product) {
        // if the product is already in list, simply update it
        int newQuantity = product.getQuantity();
        try {
            Product productInCart = getProductInCartCorrespondentTo(product);
            int previousQuantity = productInCart.getQuantity();
            productInCart.setQuantity(newQuantity + previousQuantity);
            this.amount += newQuantity * product.getPrice();
            //this.lastProduct = product;
        } catch (NoSuchProductException e) {
            // the product isn't already in the list -> add it
            int index;
            try {
                index = getCategoryIndex(product.getCategory().name());
            } catch (NoSuchCategoryException e2) {
                index = categoryNames.indexOf(Category.OTHER.name());
            }
            shoppingCart.get(index).add(product);
            float price = product.getPrice() * newQuantity;
            this.amount += price;
            //this.lastProduct = product;
        }
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
            float price = product.getPrice() * product.getQuantity();
            this.amount -= price;
        } else
            throw new NoSuchProductException();
        if (lastProduct == product) {
            lastProduct = null;
        }
    }


    public List<Product> getProductsInCategory(Category category) {
        int index;
        try {
            index = getCategoryIndex(category.name());
        } catch (NoSuchCategoryException e) {
            index = 0;
        }
        return shoppingCart.get(index);
    }

    public List<Product> getProductsInCategory(String categoryName) {
        int index;
        try {
            index = getCategoryIndex(categoryName);
        } catch (NoSuchCategoryException e) {
            index = 0;
        }
        return shoppingCart.get(index);
    }


    public List<Integer> getNumberOfProductsPerCategory() {
        ArrayList<Integer> productsPerCategory = new ArrayList<>();
        for (int i = 0; i < shoppingCart.size(); i ++) {
            productsPerCategory.add(shoppingCart.get(i).size());
        }
        return productsPerCategory;
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


    public float getAmount() {
        return amount;
    }

    public Product getProductWithBarcode(String barcode) {
        Product product = null;
        for (int i = 0; i < shoppingCart.size() && product == null; i ++) {
            List<Product> list = shoppingCart.get(i);
            for (int j = 0; j < list.size() && product == null; j ++) {
                if (list.get(j).getBarcode().equals(barcode))
                    product = list.get(j);
            }
        }
        return product;
    }

    public void updateQuantity (Product product, int newQuantity) throws NoSuchProductException {
        // find the product in list correspondent to product
        Product productInCart = getProductInCartCorrespondentTo(product);
        int previousQuantity = productInCart.getQuantity();
        productInCart.setQuantity(newQuantity);
        int delta = newQuantity - previousQuantity;
        this.amount += delta * product.getPrice();
    }

    private Product getProductInCartCorrespondentTo(Product product) throws NoSuchProductException {
        // find the product in list correspondent to product
        Product productInCart = null;
        int index;
        try {
            index = getCategoryIndex(product.getCategory().name());
        } catch (NoSuchCategoryException e) {
            index = categoryNames.indexOf(Category.OTHER.name());
        }
        List<Product> list = shoppingCart.get(index);
        for (Product listProduct : list) {
            if (listProduct.equals(product))
                productInCart = listProduct;
        }
        if (productInCart == null)
            throw new NoSuchProductException();
        return productInCart;
    }
}
