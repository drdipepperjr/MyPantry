package com.example.karthikkribakaran.mypantry;

/**
 * Created by rebeccalee on 12/9/17.
 */
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
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

    public static final String PREVIOUS_DATE_TABLE_NAME = "previousDate";

    public int NUM_RESULTS = 5;


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

        /*
            Create a table for the previous time the app was opened
            Used for updating the month list
         */
        db.execSQL(
                "create table " + PREVIOUS_DATE_TABLE_NAME + "(month text, year int)"
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
        Cursor res = getItem(itemName,expDate);
        if(res.getCount()!=0) {
            res.moveToFirst();
            String itemNameT = res.getString(0);
            double qtyT = res.getDouble(1) + qty;
            String expDateT = res.getString(2);
            double priceT = res.getDouble(3);
            String tagT = res.getString(4);

            updateItem(itemName, expDate,itemNameT,qtyT,expDateT,priceT,tagT);
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
    public void updateItem (String itemName, String expDate, String newItemName, double newQty, String newExpDate, double newPrice, String newTag) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ITEM_NAME, newItemName);
        contentValues.put(QTY, newQty);
        contentValues.put(EXP_DATE, newExpDate);
        contentValues.put(PRICE, newPrice);
        contentValues.put(TAG, newTag);

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

            GroceryItem groceryItem = new GroceryItem(title, price, qty, exp, tag);
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
        if there are already 12 months, remove one
        Called in mainactivity
     */
    public void insertMonth (String month, double spent, double wasted) {
        SQLiteDatabase db = this.getWritableDatabase();

        // if month already exists in DB, remove it first
        if(getMonth(month).getCount() != 0){
            deleteMonth(month);
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put("month",month);
        contentValues.put("spent", spent);
        contentValues.put("wasted", wasted);
        try {
            db.insert(YEARLY_SPENDING_TABLE_NAME, null, contentValues);
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    /*
        Update the money spent and wasted for a certain month in the yearly spending table
     */
    public void updateMonth (String month, double spent, double wasted) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor res = getMonth(month);
        res.moveToFirst();
        double newSpent = res.getDouble(res.getColumnIndex("spent")) + spent;
        double newWasted = res.getDouble(res.getColumnIndex("wasted")) + wasted;

        ContentValues contentValues = new ContentValues();
        contentValues.put("spent", newSpent);
        contentValues.put("wasted", newWasted);
        try {
            db.update(YEARLY_SPENDING_TABLE_NAME, contentValues, "month = ?", new String[]{month});
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
            res =  db.rawQuery( "select * from " + YEARLY_SPENDING_TABLE_NAME + " where month = ?", new String[]{month} );
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

    /*
        Clear the yearlySpending table in preparation for a new year. Called in Mainactivity
     */
    public void clearYearlySpending(){
        SQLiteDatabase db = this.getReadableDatabase();

        try {
            db.execSQL("delete from "+ YEARLY_SPENDING_TABLE_NAME);
        } catch(Exception e){
            e.printStackTrace();
        }
    }


    /*
        Generate a sample year for testing. Commented out in MainActivity
     */
    public void generateSampleYear(){
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
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM");
        Date date = new Date();
        String month_name = new DateFormatSymbols().getMonths()[Integer.parseInt(dateFormat.format(date))-1];

        // Check if item is in DB
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

        // update the money spent/wasted this month
        updateMonth(month_name,consumed,wasted);
    }


    /*
        Update a usedItem with matching itemName
     */
    public void updateUsedItem (String itemName, double consumed, double wasted, String tag) {

        SQLiteDatabase db = this.getReadableDatabase();


        Cursor res = null;

        try {
            res = db.rawQuery("select * from usedItems where item_name = ?", new String[]{itemName});
        } catch(Exception e){
            e.printStackTrace();
        }
        res.moveToFirst();
        double oldConVal=res.getDouble(1);
        double oldWasteVal=res.getDouble(2);

        ContentValues contentValues = new ContentValues();
        contentValues.put(ITEM_NAME, itemName);
        contentValues.put(CONSUMED, consumed+oldConVal);
        contentValues.put(WASTED, wasted+oldWasteVal);
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
        Clear the usedItems table in preparation for a new month
     */
    public void clearUsedItems(){
        SQLiteDatabase db = this.getReadableDatabase();

        try {
            db.execSQL("delete from "+ USED_ITEMS_TABLE_NAME);
        } catch(Exception e){
            e.printStackTrace();
        }
    }


    public void generateSampleUsedItems(){
        insertUsedItem("bread",2.5,0,"grain");
        insertUsedItem("eggs", 3, 1, "meat");
        insertUsedItem("carrots", 1, 4, "vegetables");
        insertUsedItem("bananas", 1, 0.75, "fruit");
        insertUsedItem("strawberries", 3,2.5, "fruit");
        insertUsedItem("pork", 10, 0, "meat");
        insertUsedItem("steak", 35, 5, "meat");
        insertUsedItem("broccoli", 10, 0, "vegetables");
        insertUsedItem("cheetos", 5, 15, "snacks");
        insertUsedItem("pop tarts", 11, 1, "snacks");
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
    public double getMoneyWastedThisMonth(){
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
        HashMap<String,Double> moneyWastedByTag = new HashMap<String,Double>();
        Cursor res = null;

        try {
            res =  db.rawQuery( "select wasted, tag from usedItems where wasted > 0", null );
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
        Return a Cursor containing all the months in the DB
     */
    public Cursor getAllMonths(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = null;

        try {
            res =  db.rawQuery( "select * from " + YEARLY_SPENDING_TABLE_NAME, null );
        } catch(Exception e){
            e.printStackTrace();
        }

        return res;
    }



    //                                                                            //
    //                                                                            //
    //                                                                            //
    //                      HELPER CODE FOR  HABITS                               //
    //                                                                            //
    //                                                                            //


    /*
        Return a Cursor containing all items that have not been totally consumed
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



    /*
        Return a Cursor with the top x most wasted items this month as (item_name, wasted) pair
     */
    public Cursor getMostWasted(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = null;

        try {
            res =  db.rawQuery( "select " +ITEM_NAME + ", " + WASTED + " from " + USED_ITEMS_TABLE_NAME + " order by " + WASTED  + " DESC limit " + NUM_RESULTS, null);
        } catch(Exception e){
            e.printStackTrace();
        }

        return res;
    }


    /*
        Return a Cursor with the top x least wasted items this month as (item_name, wasted) pair
     */
    public Cursor getLeastWasted(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = null;

        try {
            res =  db.rawQuery( "select " +ITEM_NAME + ", " + WASTED + " from " + USED_ITEMS_TABLE_NAME + " where wasted !=0 " + " order by " + WASTED  + " ASC limit " + NUM_RESULTS, null);
        } catch(Exception e){
            e.printStackTrace();
        }

        return res;
    }


    /*
        Return a Cursor with the top x most total spent (consumed + wasted) items as a (item_name, total_spent) pair
     */
    public Cursor getMostTotalSpent(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = null;

        try {
            res =  db.rawQuery( "select " +ITEM_NAME + ", (consumed + wasted) as total_spent from " +
                    USED_ITEMS_TABLE_NAME + " order by total_spent DESC limit " + NUM_RESULTS, null);
        } catch(Exception e){
            e.printStackTrace();
        }

        return res;
    }


    /*
        Return a Cursor with the top x most wasted tags as a (tag, wasted) pair
     */
    public Cursor getMostWastedByTag(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = null;

        try {
            res =  db.rawQuery( "select " + TAG + ", " + WASTED + " from " +
                    USED_ITEMS_TABLE_NAME + " group by tag order by sum(wasted) ", null);
        } catch(Exception e){
            e.printStackTrace();
        }

        return res;
    }


    /*
        Print the results of a Cursor containing usedItems
     */
    public void printUsedItemsResults(Cursor res){
        if(res != null) {
            res.moveToFirst();
            while (res.isAfterLast() == false) {
                String name = res.getString(0);
                double value = res.getDouble(1);
                System.out.println("Item: " + name + ", value: " + value);
                res.moveToNext();
            }
        }
    }



    //                                                                            //
    //                                                                            //
    //                                                                            //
    //                      HELPER CODE FOR  DATE                                 //
    //                                                                            //
    //                                                                            //



    /*
         Change the date in the previousDate table to match the current date
      */
    public void changeDate(String month, int year){
        SQLiteDatabase db = this.getWritableDatabase();

        // delete the previous date
        try {
            db.execSQL("delete from "+ PREVIOUS_DATE_TABLE_NAME);
        } catch(Exception e){
            e.printStackTrace();
        }


        // insert the new date
        ContentValues contentValues = new ContentValues();
        contentValues.put("month", month);
        contentValues.put("year", year);

        try {
            db.insert(PREVIOUS_DATE_TABLE_NAME, null, contentValues);
            System.err.println("new Date is " + month + ", " + year);
        } catch(Exception e){
            e.printStackTrace();
        }

    }


    /*
        check the current date and see if the month/year is different from the previous date
     */
    public void checkDate(){
        SQLiteDatabase db = this.getWritableDatabase();

        // Get the current month and year
        Calendar now = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM");
        Date date = new Date();
        String current_month_name = new DateFormatSymbols().getMonths()[Integer.parseInt(dateFormat.format(date))-1];
        int current_year = now.get(Calendar.YEAR);
        System.out.println("Current Year is : " + current_year);
        System.out.println("Current Month is : " +current_month_name);

        // get the previous date
        Cursor res = null;
        try {
            res = db.rawQuery("select * from " + PREVIOUS_DATE_TABLE_NAME, null);
        } catch(Exception e){
            e.printStackTrace();
        }

        // literally the first time the app is opened
        if(res.getCount() == 0){
            System.out.println("No previous date found");
            ContentValues contentValues = new ContentValues();
            contentValues.put("month", current_month_name);
            contentValues.put("year", current_year);

            try {
                db.insert(PREVIOUS_DATE_TABLE_NAME, null, contentValues);
                insertMonth(current_month_name,0,0);
            } catch(Exception e){
                e.printStackTrace();
            }

            return;
        }

        // otherwise check the date and see if its different
        else{
            res.moveToFirst();
            String month = res.getString(0);
            int year = res.getInt(1);
            System.out.println("Previous date was " + month + ", " + year);


            // if the month is different, clear the used items table
            if(!month.equals(current_month_name)){
                System.err.println("clearing used items");
                clearUsedItems();
                insertMonth(current_month_name,0,0);
            }

            // if the year is different, delete the yearlySpending table
            if(year!=current_year){
                clearYearlySpending();
            }
            changeDate(current_month_name,current_year);
        }
    }

    /*
        put a sample date into the DB. ONLY CALL IF APP HAS YET TO BE INSTALLED
     */
    public void generateSamplePreviousDate(){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("month", "November");
        contentValues.put("year", 2017);

        try {
            db.insert(PREVIOUS_DATE_TABLE_NAME, null, contentValues);
        } catch(Exception e){
            e.printStackTrace();
        }
    }
}
