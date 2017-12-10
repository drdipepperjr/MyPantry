package com.example.karthikkribakaran.mypantry;

import java.util.Date;

/**
 * Created by Jackey on 12/6/2017.
 */

public class GroceryItem {
    int id;
    String title;
    double price;
    double quantity;
    Date date;
    String tag;

    public static final String MY_FORMAT = "MM/dd/yy";

    public GroceryItem(String title, double price, double quantity, Date date, int Id, String Tag)
    {
        this.title = title;
        this.price = price;
        this.quantity = quantity;
        this.date = date;
        this.id = id;
        this.tag = Tag;
    }
}
