package com.code.dima.happygrocery;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import com.code.dima.common.GraphicOverlay;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;

public class BarcodeGraphic extends GraphicOverlay.Graphic {

    private static final float STROKE_WIDTH = 8.0f;

    private final Paint rectPaint;
    private final FirebaseVisionBarcode barcode;

    BarcodeGraphic(GraphicOverlay overlay, FirebaseVisionBarcode barcode) {
        super(overlay);

        this.barcode = barcode;

        rectPaint = new Paint();
        rectPaint.setColor(Color.RED);
        rectPaint.setStyle(Paint.Style.STROKE);
        rectPaint.setStrokeWidth(STROKE_WIDTH);


        // Redraw the overlay, as this graphic has been added.
        postInvalidate();
    }

    /**Crea un frame intorno al barcode trovato */
    @Override
    public void draw(Canvas canvas) {
        if (barcode == null) {
            throw new IllegalStateException("Attempting to draw a null barcode.");
        }

        RectF rect = new RectF(barcode.getBoundingBox());
        rect.left = translateX(rect.left);
        rect.top = translateY(rect.top);
        rect.right = translateX(rect.right);
        rect.bottom = translateY(rect.bottom);
        canvas.drawRect(rect, rectPaint);

    }
}
