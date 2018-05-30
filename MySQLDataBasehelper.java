package com.example.joelwasserman.androidbletutorial;

import java.util.LinkedList;
import java.util.List;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLDataBasehelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
        private static final String DATABASE_NAME = "BookDB";
    public MySQLDataBasehelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL statement to create book table
        String CREATE_BOOK_TABLE = "CREATE TABLE books ( " +
                "bdaddr TEXT , " +
                "rssi INTEGER, "+
                "loc INTEGER )";
        // create books table
        db.execSQL(CREATE_BOOK_TABLE);
    }
    // This fn must be written. I think clss SQLiteOpenHelper is abstract due to this fn
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older books table if existed
        db.execSQL("DROP TABLE IF EXISTS books");
        // create fresh books table
        this.onCreate(db);
    }
    // We gotta write some helper fn

    // Books table name
    private static final String TABLE_BOOKS = "books";

    // Books Table Columns names
    private static final String BD_ADDR = "bdaddr";
    private static final String RSSI = "rssi";
    private static final String LOC = "loc";

    private static final String[] COLUMNS = {BD_ADDR,RSSI,LOC};

    public void addBook(Book book){
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(BD_ADDR, book.getBdAdrr());
        values.put(RSSI, book.getRSSI());
        values.put(LOC,book.getLoc());


        // 3. insert
        db.insert(TABLE_BOOKS, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        // 4. close
        db.close();
    }

    public int getBook(int id){

        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

        // 2. build query
       // Cursor cursor =db.query(TABLE_BOOKS,new String[] {BD_ADDR,RSSI,LOC},null,null,null,null,null);
        Cursor cursor =     db.query(TABLE_BOOKS, // a. table
                       COLUMNS, // b. column names
                        " rssi = ?", // c. selections
                        new String[] { Integer.toString(id) }, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit

        // 3. if we got results get the first one
        if (cursor != null)
            cursor.moveToFirst();
       /* while (cursor.isAfterLast()){
            String bd=cursor.getString(0);
            int rssi=cursor.getInt(1);
            int loc=cursor.getInt(2);
            Log.i("vivek",bd);Log.i("Vivek",Integer.toString(rssi));Log.i("Vivek",Integer.toString(loc));
            cursor.moveToNext();
        }*/

        //String i=cursor.getString(2);
        //db.close();

    return Integer.parseInt(cursor.getString(2));
    }


}
