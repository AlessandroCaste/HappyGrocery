package com.code.dima.happygrocery.exception;

public class NoLastProductException extends Exception {
    public NoLastProductException() {
        super("There's no last product yet");
    }
}
