package com.code.dima.happygrocery.exception;

public class NoSuchCategoryException extends Exception {
    public NoSuchCategoryException() {
        super("There's no such category");
    }
}
