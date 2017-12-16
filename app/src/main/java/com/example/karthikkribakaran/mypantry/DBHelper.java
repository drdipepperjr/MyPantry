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

public class DBHelper extends SQLiteOpenHelper{

    public static final String DATABASE_NAME = "MyPantry.db";

    public static final String PANTRY_TABLE_NAME = "pantry";
    public static final String ITEM_NAME = "item_name";
    public static final String QTY = "qty";
    public static final String PRICE = "price";
    public static final String EXP_DATE = "exp_date";
    public static final String TAG = "tag";

    public static final String YEARLY_SPENDING_TABLE_NAME = "yearlySpending";
    public static final String MONTH = "month";
    public static final String SPENT = "spent";

    public static final String USED_ITEMS_TABLE_NAME = "usedItems";
    public static final String CONSUMED = "consumed";
    public static final String WASTED = "wasted";

    private HashMap hp;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // create pantry table
        db.execSQL(
                "create table pantry" +
                        "(item_name text, qty double, exp_date text, price double, tag text, primary key(item_name, exp_date))"
        );

        // create yearlySpending table
        db.execSQL(
                "create table yearlySpending" +
                        "(month text,spent double,wasted double)"
        );

        // create usedItems table
        db.execSQL(
                "create table " + USED_ITEMS_TABLE_NAME + "(item_name text, consumed double, wasted double, tag text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //db.execSQL("DROP TABLE IF EXISTS pantry");
        //db.execSQL("DROP TABLE IF EXISTS pantry");
        onCreate(db);
    }


    //                                                                            //
    //                                                                            //
    //                                                                            //
    //                      HELPER CODE FOR PANTRY TABLE                          //
    //                                                                            //
    //                                                                            //
    //                                                                            //

    /*
        Insert an item into the pantry table
        If item already exists, update the quantity
     */
    public void insertItem (String itemName, double qty, String expDate, double price, String tag) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Check if item is in DB
        // TODO: view does not update item qty
        Cursor res = getItem(itemName,expDate);
        if(res.getCount()!=0) {
            res.moveToFirst();
            String itemNameT = res.getString(0);
            double qtyT = res.getDouble(1) + qty;
            String expDateT = res.getString(2);
            double priceT = res.getDouble(3);
            String tagT = res.getString(4);

            updateItem(itemNameT,qtyT,expDateT,priceT,tagT);
        }

        else {
            ContentValues contentValues = new ContentValues();
            contentValues.put(ITEM_NAME, itemName);
            contentValues.put(QTY, qty);
            contentValues.put(EXP_DATE, expDate);
            contentValues.put(PRICE, price);
            contentValues.put(TAG, tag);

            System.out.println(contentValues.toString());
            try {
                db.insert("pantry", null, contentValues);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /*
        Update an item with matching itemName and expDate
     */
    public void updateItem (String itemName, double qty, String expDate, double price, String tag) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ITEM_NAME, itemName);
        contentValues.put(QTY, qty);
        contentValues.put(EXP_DATE, expDate);
        contentValues.put(PRICE, price);
        contentValues.put(TAG, tag);

        try {
            db.update(PANTRY_TABLE_NAME, contentValues, "item_name = ? and exp_date = ?", new String[]{itemName, expDate});
        } catch(Exception e){
            e.printStackTrace();
        }

    }


    /*
        Return a Cursor which contains the values of the item
     */
    public Cursor getItem(String itemName, String expDate) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res = null;
         try {
             res = db.rawQuery("select * from pantry where item_name = ? and exp_date=?", new String[]{itemName, expDate});
         } catch(Exception e){
             e.printStackTrace();
         }
        return res;
    }



    /*
        Remove the item from the table
     */
    public void deleteItem (String itemName, String expDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.delete(PANTRY_TABLE_NAME,
                    "item_name = ? and exp_date = ?", new String[]{itemName, expDate});
        } catch(Exception e){
            e.printStackTrace();
        }
    }


    /*
        Return an arraylist containing all the items in the pantry table
     */
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




    //                                                                            //
    //                                                                            //
    //                                                                            //
    //                 HELPER CODE FOR  YEARLY_SPENDING TABLE                     //
    //                                                                            //
    //                                                                            //
    //                                                                            //

    /*
        Insert a month into the yearly spending table
     */
    public boolean insertMonth (String month, double spent, double wasted) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("month",month);
        contentValues.put("spent", spent);
        contentValues.put("wasted", wasted);
        try {
            db.insert("yearlySpending", null, contentValues);
        } catch(Exception e){
            e.printStackTrace();
        }

        return true;
    }

    /*
        Update the money spent and wasted for a certain month in the yearly spending table
     */
    public boolean updateMonth (String month, double spent, double wasted) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("spent", spent);
        contentValues.put("wasted", wasted);
        try {
            db.update("yearlySpending", contentValues, "month = ?", new String[]{month});
        }catch(Exception e){
            e.printStackTrace();
        }
        return true;
    }

    /*
        return a Cursor which contains the values for a month
     */
    public Cursor getMonth(String month) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = null;
        try{
            res =  db.rawQuery( "select * from pantry where month = ?", new String[]{month} );
        } catch(Exception e){
            e.printStackTrace();
        }
        return res;
    }

    /*
        Delete a month from the yearlySpending table (should only be used if there are already 12 months)
     */
    public Integer deleteMonth (String month) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("yearlySpending",
                "month = ?", new String[]{month});
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



    //                                                                            //
    //                                                                            //
    //                                                                            //
    //                      HELPER CODE FOR  USED ITEMS TABLE                     //
    //                                                                            //
    //                                                                            //


    public void insertUsedItem(String itemName, double consumed, double wasted, String tag){

        SQLiteDatabase db = this.getWritableDatabase();

        // Check if item is in DB
        // TODO: view does not update item qty
        Cursor res = getUsedItem(itemName);
        if(res.getCount() == 1) {
            res.moveToFirst();
            String itemNameT = res.getString(0);
            double consumedT = res.getDouble(1);
            double wastedT = res.getDouble(3);
            String tagT = res.getString(4);

            updateUsedItem(itemNameT,consumedT,wastedT,tagT);
        }

        else {
            ContentValues contentValues = new ContentValues();
            contentValues.put(ITEM_NAME, itemName);
            contentValues.put(CONSUMED, consumed);
            contentValues.put(WASTED, wasted);
            contentValues.put(TAG, tag);

            System.out.println(contentValues.toString());
            try {
                db.insert(USED_ITEMS_TABLE_NAME, null, contentValues);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /*
        Update a usedItem with matching itemName
     */
    public void updateUsedItem (String itemName, double consumed, double wasted, String tag) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ITEM_NAME, itemName);
        contentValues.put(CONSUMED, consumed);
        contentValues.put(WASTED, wasted);
        contentValues.put(TAG, tag);

        try {
            db.update(USED_ITEMS_TABLE_NAME, contentValues, "item_name = ?", new String[]{itemName});
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    /*
        Return a Cursor which contains the values of the item
     */
    public Cursor getUsedItem(String itemName) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res = null;
        try {
            res = db.rawQuery("select * from usedItems where item_name = ?", new String[]{itemName});
        } catch(Exception e){
            e.printStackTrace();
        }
        return res;
    }

    /*
     Get a list of all items that have not been totally consumed
    */
    public Cursor getWastedItems(String date){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = null;
        try {
            res =  db.rawQuery( "select * from usedItems where wasted > 0", null );
        } catch(Exception e){
            e.printStackTrace();
        }

        return res;
    }

}
