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
import android.util.Pair;

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
        /*
            usedItems contains only the current month's items
            table is cleared each month
         */
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

    /*
        get a sample grocery list for testing. line is commented out in MainActivity
     */
    public void generateSampleItems(){

        insertItem("potatoes",10,"12/25/2017",5,"vegetables");
        insertItem("cereal",1,"3/12/2018",3.45,"grains");
        insertItem("lettuce",2,"12/4/2017",2.30,"vegetables");
        insertItem("chicken",5,"12/31/2017",10.75,"meat");
        insertItem("sugar",1,"9/16/2019",5,"baking");

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
    public void insertMonth (String month, double spent, double wasted) {
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

    }

    /*
        Update the money spent and wasted for a certain month in the yearly spending table
     */
    public void updateMonth (String month, double spent, double wasted) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("spent", spent);
        contentValues.put("wasted", wasted);
        try {
            db.update("yearlySpending", contentValues, "month = ?", new String[]{month});
        }catch(Exception e){
            e.printStackTrace();
        }
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
    public void deleteMonth (String month) {
        SQLiteDatabase db = this.getWritableDatabase();
        try{
        db.delete("yearlySpending",
                "month = ?", new String[]{month});
        } catch(Exception e){
            e.printStackTrace();
        }
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

    /*
        Generate a sample year for testing. Commented out in MainActivity
     */
    public void generateSampleYear(){
        insertMonth("August",203.14, 30.2);
        insertMonth("September",245.77, 52.67);
        insertMonth("October",197.00, 22.50);
        insertMonth("November", 200.5, 10.78);
        insertMonth("December",100, 5);
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
            double wastedT = res.getDouble(2);
            String tagT = res.getString(3);

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

    public void generateSampleUsedItems(){
        insertUsedItem("bread",2.5,0,"grain");
        insertUsedItem("eggs", 3, 1, "meat");
        insertUsedItem("carrots", 1, 4, "vegetables");
        insertUsedItem("bananas", 1, 0.75, "fruit");
        insertUsedItem("strawberries", 3,2.5, "fruit");
        insertUsedItem("pork", 10, 0, "meat");
    }



    //                                                                            //
    //                                                                            //
    //                                                                            //
    //                      HELPER CODE FOR  METRICS                              //
    //                                                                            //
    //                                                                            //


    /*
        Get the total amount of money wasted for the current month
     */
    public double getMoneyWastedThisMonth(String month){
        SQLiteDatabase db = this.getReadableDatabase();
        double moneyWasted = 0;
        Cursor res = null;

        try {
            res =  db.rawQuery( "select wasted from usedItems", null );
        } catch(Exception e){
            e.printStackTrace();
        }

        res.moveToFirst();
        while(res.isAfterLast() == false){
            moneyWasted += res.getDouble(res.getColumnIndex(WASTED));
            res.moveToNext();
        }

        return moneyWasted;
    }


    /*
        Return the amount of money wasted for each tag as a (tag,wasted) pair
     */
    public HashMap<String,Double>  getMoneyWastedByTag(){
        SQLiteDatabase db = this.getReadableDatabase();
        HashMap<String,Double> moneyWastedByTag = null;
        Cursor res = null;

        try {
            res =  db.rawQuery( "select wasted, tag from usedItems", null );
        } catch(Exception e){
            e.printStackTrace();
        }

        res.moveToFirst();
        while(res.isAfterLast() == false){
            String tag = res.getString(res.getColumnIndex("tag"));
            double wasted = res.getDouble(res.getColumnIndex("wasted"));

            if(moneyWastedByTag.containsKey(tag)){
                double wastedAccum = moneyWastedByTag.get(tag) + wasted;
                moneyWastedByTag.put(tag, wastedAccum);
            }

            else{
                moneyWastedByTag.put(tag, wasted);
            }

            res.moveToNext();
        }


        return moneyWastedByTag;
    }

    /*
        Get a list of all items that have not been totally consumed
    */
    public Cursor getWastedItems(){
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
