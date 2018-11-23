package com.code.dima.happygrocery.exception;

public class NoSuchProductException extends Exception {
    public NoSuchProductException() {
        super("The product you referred to doesn't exist in the product list");
    }
}
