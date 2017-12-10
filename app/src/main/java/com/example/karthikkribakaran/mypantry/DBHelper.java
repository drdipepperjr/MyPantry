package com.example.karthikkribakaran.mypantry;

/**
 * Created by rebeccalee on 12/9/17.
 */
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Pantry.db";
    public static final String CONTACTS_TABLE_NAME = "pantry";
    public static final String ITEM_NAME = "item_name";
    public static final double QTY = 0.0;
    public static final double PRICE = 0.0;
    public static final String EXP_DATE = "02/02/2003";
    public static final String TAG = "household";

    private HashMap hp;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table pantry" +
                        "(item_name text, qty double, exp_date text, price double, tag text, primary key(item_name, exp_date))"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS pantry");
        onCreate(db);
    }

    public boolean insertItem (String itemName, double qty, String expDate, double price, String tag) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("item_name", itemName);
        contentValues.put("qty", qty);
        contentValues.put("exp_date", expDate);
        contentValues.put("price", price);
        contentValues.put("tag", tag);

        System.out.println(contentValues.toString());
        db.insert("pantry", null, contentValues);
        return true;
    }

    public boolean updateItem (String itemName, double qty, String expDate, double price, String tag) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("exp_date", expDate);
        contentValues.put("tag", tag);
        db.update("pantry", contentValues, "item_name = ? and exp_date = ?", new String[]{itemName, expDate});
        return true;
    }

    public Cursor getData(String itemName, String expDate) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from pantry where item_name = ? and exp_date=?", new String[]{itemName,expDate} );
        return res;
    }
/*
    public void testData(String itemName, String expDate) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from pantry where item_name=" +itemName+ " and exp_date=" + expDate +"", null );
        System.out.print("hi");
    }
*/
    public Integer deleteItem (String itemName,String expDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("contacts",
                "item_name = ? and exp_date = ?", new String[]{itemName, expDate});
    }

    public ArrayList<GroceryItem> getAllItems() {
        ArrayList<GroceryItem> array_list = new ArrayList<>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from pantry", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            String title = res.getString(res.getColumnIndex("item_name"));
            String tag = res.getString(res.getColumnIndex("tag"));
            Double qty = res.getDouble(res.getColumnIndex("qty"));
            Double price = res.getDouble(res.getColumnIndex("price"));

            SimpleDateFormat sdf = new SimpleDateFormat(GroceryItem.MY_FORMAT);
            Date exp;
            try {
                exp = sdf.parse(res.getString(res.getColumnIndex("exp_date")));
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

            GroceryItem groceryItem = new GroceryItem(title, qty, price, exp, tag);
            array_list.add(groceryItem);
            res.moveToNext();
        }
        return array_list;
    }
    public void testgetAllItems(String itemName) {

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        //Cursor res =  db.query("pantry",new String[]{itemName},"item_name=?",new String[]{itemName},null, null,null);
        Cursor res =  db.rawQuery( "select * from pantry where item_name=?", new String[]{itemName} );

        res.moveToFirst();

        while(res.isAfterLast() == false){
            System.out.println(res.getString(0));
            res.moveToNext();

        }

    }

    /* CODE THAT GOES IN DB HELPER
    //db.insertItem("test", 111,  "12/9/17", 10, "Test");


        Cursor cursor = db.getData("test", "12/9/17");
        cursor.moveToFirst();
        String itemName = cursor.getString(0);
        double qty = cursor.getDouble(1);
        String expDate = cursor.getString(2);
        double price = cursor.getDouble(3);
        String tag = cursor.getString(4);
        Date testDate = new Date(expDate);

        //System.err.println(itemName);

        //Cursor res =  db.rawQuery( "select * from pantry where item_name =" +itemName+ " and exp_date=" + expDate +"", null );
        GroceryItem test = new GroceryItem(itemName,price,27,testDate);
        //db.testgetAllItems("test");
     */

//todo check if item/date is already in the database
}
