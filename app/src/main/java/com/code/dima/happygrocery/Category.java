package com.code.dima.happygrocery;

import android.graphics.Color;

public enum Category {
    FOOD,
    BEVERAGE,
    KIDS,
    HOME,
    CLOTHING;

    public static Integer getCategoryColor (Category category) {
        int colorInt = 0;
        switch (category) {
            case FOOD:      colorInt = Color.parseColor("#C62828");
                            break;
            case BEVERAGE:  colorInt = Color.parseColor("#1565C0");
                            break;
            case KIDS:      colorInt = Color.parseColor("#F9A825");
                            break;
            case HOME:      colorInt = Color.parseColor("#2E7D32");
                            break;
            case CLOTHING:  colorInt = Color.parseColor("#6A1B9A");
                            break;
        }
        return colorInt;
    }
}
