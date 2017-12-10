package com.example.karthikkribakaran.mypantry;

/**
 * Created by rebeccalee on 12/9/17.
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

public class DBHelper extends SQLiteOpenHelper{

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

        db.execSQL(
                "create table yearlySpending" +
                        "(month text,spent double,wasted double)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        //db.execSQL("DROP TABLE IF EXISTS pantry");
        //db.execSQL("DROP TABLE IF EXISTS pantry");
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

        db.insert("pantry", null, contentValues);
        return true;
    }

    public boolean insertMonth (String month, double spent, double wasted) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("month",month);
        contentValues.put("spent", spent);
        contentValues.put("wasted", wasted);
        db.insert("yearlySpending", null, contentValues);
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

    public boolean updateMonth (String month, double spent, double wasted) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("spent", spent);
        contentValues.put("wasted", wasted);
        db.update("yearlySpending", contentValues, "month = ?", new String[]{month});
        return true;
    }

    public Cursor getData(String itemName, String expDate) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from pantry where item_name = ? and exp_date=?", new String[]{itemName,expDate} );
        return res;
    }

    public Cursor getAMonth(String month) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from pantry where month = ?", new String[]{month} );
        return res;
    }

    public Integer deleteItem (String itemName,String expDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("contacts",
                "item_name = ? and exp_date = ?", new String[]{itemName, expDate});
    }

    public Integer deleteMonth (String month) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("yearlySpending",
                "month = ?", new String[]{month});
    }

    public ArrayList<String> getAllItems() {
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from pantry", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex(ITEM_NAME)));
            res.moveToNext();
        }
        return array_list;
    }

    public ArrayList<String> getAllMonths() {
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from yearlySpending", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex(ITEM_NAME)));
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

//todo check if item/date is already in the database
}
