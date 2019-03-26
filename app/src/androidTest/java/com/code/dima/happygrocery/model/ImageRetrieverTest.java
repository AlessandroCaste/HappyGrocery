package com.code.dima.happygrocery.model;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.code.dima.happygrocery.R;

import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.TestCase.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)

public class ImageRetrieverTest {

    @Test
    public void correctImportTest () {
        Context appContext = InstrumentationRegistry.getTargetContext();
        int images = ImageRetriever.getInstance(appContext).getNumberOfImages();
        System.out.println("Number of images: " + images);
        assertEquals(8, images);
    }

    @Test
    public void initializeTest () {
        Context appContext = InstrumentationRegistry.getTargetContext();
        ImageRetriever.initialize(appContext);
        int images = ImageRetriever.getInstance(appContext).getNumberOfImages();
        System.out.println("Number of images: " + images);
        assertEquals(8, images);
    }

    @Test
    public void cocaTest() {
        Context appContext = InstrumentationRegistry.getTargetContext();
        String name = "coca";
        Category category = Category.BEVERAGE;
        int id = ImageRetriever.getInstance(appContext).retrieveImageID(name, category);
        assertEquals(R.drawable.coca_cola, id);
    }

    @Test
    public void pizzaTest() {
        Context appContext = InstrumentationRegistry.getTargetContext();
        String name = "pizza margherita";
        Category category = Category.FOOD;
        int id = ImageRetriever.getInstance(appContext).retrieveImageID(name, category);
        assertEquals(R.drawable.pizza, id);
    }

    @Test
    public void biberonTest() {
        Context appContext = InstrumentationRegistry.getTargetContext();
        String name = "biberon maam";
        Category category = Category.KIDS;
        int id = ImageRetriever.getInstance(appContext).retrieveImageID(name, category);
        assertEquals(R.drawable.biberon, id);
    }

    @Test
    public void shirtTest() {
        Context appContext = InstrumentationRegistry.getTargetContext();
        String name = "t-shirt bianca";
        Category category = Category.CLOTHING;
        int id = ImageRetriever.getInstance(appContext).retrieveImageID(name, category);
        assertEquals(R.drawable.shirts, id);
    }

    @Test
    public void homeTest() {
        Context appContext = InstrumentationRegistry.getTargetContext();
        String name = "swiffer";
        Category category = Category.HOME;
        int id = ImageRetriever.getInstance(appContext).retrieveImageID(name, category);
        assertEquals(R.drawable.home, id);
    }

    @Test
    public void wineTest() {
        Context appContext = InstrumentationRegistry.getTargetContext();
        String name = "vino Chianti 2L";
        Category category = Category.BEVERAGE;
        int id = ImageRetriever.getInstance(appContext).retrieveImageID(name, category);
        assertEquals(R.drawable.wine, id);
    }

    @Test
    public void penTest() {
        Context appContext = InstrumentationRegistry.getTargetContext();
        String name = "penna blu";
        Category category = Category.KIDS;
        int id = ImageRetriever.getInstance(appContext).retrieveImageID(name, category);
        assertEquals(R.drawable.pencil, id);
    }

    @Test
    public void phoneTest() {
        Context appContext = InstrumentationRegistry.getTargetContext();
        String name = "Apple phone cover";
        Category category = Category.OTHER;
        int id = ImageRetriever.getInstance(appContext).retrieveImageID(name, category);
        assertEquals(R.drawable.phone_cover, id);
    }

    @Test
    public void ufoTest() {
        Context appContext = InstrumentationRegistry.getTargetContext();
        String name = "UFO robot";
        Category category = Category.OTHER;
        int id = ImageRetriever.getInstance(appContext).retrieveImageID(name, category);
        assertEquals(R.drawable.etc, id);
    }
}
