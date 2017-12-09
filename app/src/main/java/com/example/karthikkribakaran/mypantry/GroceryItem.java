package com.example.karthikkribakaran.mypantry;

import java.util.Date;

/**
 * Created by Jackey on 12/6/2017.
 */

public class GroceryItem {
    String Title;
    double Price;
    int Quantity;
    Date Date;

    public static final String MY_FORMAT = "MM/dd/yy";

    public GroceryItem(String title, double price, int quantity, Date date)
    {
        this.Title = title;
        this.Price = price;
        this.Quantity = quantity;
        this.Date = date;
    }
}
