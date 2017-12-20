package com.example.karthikkribakaran.mypantry;

/**
 * Created by karthikkribakaran on 12/19/17.
 */

public class Recipe {
    String label;
    int calories;
    String recipeURL;

    Recipe(String label, int calories, String url) {
        this.label = label;
        this.calories = calories;
        this.recipeURL = url;
    }
}
