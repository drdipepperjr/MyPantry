package com.example.karthikkribakaran.mypantry;

/**
 * Created by Jackey on 12/6/2017.
 */

public class GroceryItem {
    String Title;
    double Price;
    int Quantity;
    String Date;

    public GroceryItem(String title, double price, int quantity, String date)
    {
        this.Title = title;
        this.Price = price;
        this.Quantity = quantity;
        this.Date = date;
    }
}
