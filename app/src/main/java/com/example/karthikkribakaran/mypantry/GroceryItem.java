package com.example.karthikkribakaran.mypantry;

import java.util.Date;

/**
 * Created by Jackey on 12/6/2017.
 */

public class GroceryItem {
    String title;
    double price;
    double quantity;
    Date date;
    String tag;

    public static final String MY_FORMAT = "MM/dd/yy";

    public GroceryItem(String title, double price, double quantity, Date date, String Tag)
    {
        this.title = title;
        this.price = price;
        this.quantity = quantity;
        this.date = date;
        this.tag = Tag;
    }
}
