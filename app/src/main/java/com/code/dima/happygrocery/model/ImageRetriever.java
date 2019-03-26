package com.code.dima.happygrocery.model;

import android.content.Context;
import android.util.Log;

import com.code.dima.happygrocery.R;

import org.apache.commons.text.similarity.JaroWinklerDistance;
import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;

public class ImageRetriever {


    private static ImageRetriever instance = null;
    private ArrayList<ArrayList<String>> tokens;
    private ArrayList<String> names;
    private ArrayList<Integer> ids;

    private final double SIMILARITY_THRESHOLD = 0.75;


    private ImageRetriever(Context context) {
        this.tokens = new ArrayList<>();
        this.ids = new ArrayList<>();
        this.names = new ArrayList<>();
        //loads names and tokens and evaluate the ids
        readData(context);
        Log.d("IMAGE RETRIEVER", "Read a total of " + names.size() + " images");
        for (int i = 0; i < names.size(); i ++) {
            Log.d("IMAGE RETRIEVER", "Fetching id for " + names.get(i));
            ids.add(context.getResources().getIdentifier(names.get(i), "drawable", context.getPackageName()));
        }
        Log.d("IMAGE RETRIEVER", "IDs read: " + ids);
    }


    public static ImageRetriever getInstance(Context context) {
        if (instance == null) {
            instance = new ImageRetriever(context);
        }
        return instance;
    }

    public static void initialize(Context context) {
        if(instance == null)
            instance = new ImageRetriever(context);
    }


    private void readData(Context context) {
        Log.d("IMAGE RETRIEVER", "Reading data");
        try {
            XmlPullParser parser = context.getResources().getXml(R.xml.tokens);

            while(parser.getEventType() != XmlPullParser.END_DOCUMENT) {
                if(parser.getEventType() == XmlPullParser.START_TAG) {
                    String tagName = parser.getName();
                    if (tagName.equals("image")) {
                        Log.d("IMAGE RETRIEVER", "New image found");
                        // I add a new name and a new arraylist in tokens
                        String name = parser.getAttributeValue(0);
                        Log.d("IMAGE RETRIEVER", "Name is: " + name);
                        names.add(name);
                        tokens.add(new ArrayList<String>());
                    } else if (tagName.equals("token")) {
                        // I add a new token in the last arraylist in tokens
                        String token = parser.getAttributeValue(0);
                        Log.d("IMAGE RETRIEVER", "Associated token: " + token);
                        tokens.get(tokens.size() - 1).add(token);
                    }
                }
                parser.next();
            }
        } catch (Exception e) {
            Log.e("PARSER", "Error in parsing");
        }
    }


    public int retrieveImageID(String name, Category category) {
        String[] words = name.split(" ");

        double maxSimilarity = 0f;
        int resultIndex = 0;
        for (String word : words) {
            double maxSimilarityInCol = 0f;
            int index = 0;
            for (int i = 0; i < tokens.size(); i++) {
                double maxSimilarityInRow = 0f;
                for (String token : tokens.get(i)) {
                    double similarity = computeSimilarity(word, token);
                    if (similarity > maxSimilarityInRow)
                        maxSimilarityInRow = similarity;
                }
                if (maxSimilarityInRow > maxSimilarityInCol) {
                    maxSimilarityInCol = maxSimilarityInRow;
                    index = i;
                }
            }
            // selects the most similar token
            if (maxSimilarityInCol > maxSimilarity) {
                maxSimilarity = maxSimilarityInCol;
                resultIndex = index;
            }
        }

        int id;
        if(maxSimilarity < SIMILARITY_THRESHOLD)
            id = getDefaultImageID(category);
        else
            id = ids.get(resultIndex);
        return id;
    }

    private double computeSimilarity(String word, String token) {
        JaroWinklerDistance evaluator = new JaroWinklerDistance();
        return evaluator.apply(word.toLowerCase(), token.toLowerCase());
    }

    private int getDefaultImageID(Category category) {
        int id;
        switch (category) {
            case FOOD:      id = R.drawable.food;
                break;
            case BEVERAGE:  id = R.drawable.beverage;
                break;
            case KIDS:      id = R.drawable.pencil;
                break;
            case HOME:      id = R.drawable.home;
                break;
            case CLOTHING:  id = R.drawable.shirts;
                break;
            default:        id = R.drawable.etc;
        }
        return id;
    }

    public int getNumberOfImages() {
        return ids.size();
    }
}
