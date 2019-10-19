package com.mangosoft.gotravelssms.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.android.material.tabs.TabLayout;
import com.mangosoft.gotravelssms.model.Person;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "dashboard";
    private static final String TABLE = "smshistory";


    private static final String CREATE_TABLE = "create table "+TABLE+" (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "name VARCHAR(50)," +
            "phone VARCHAR(50),"+
            "timestamp DATE DEFAULT CURRENT_DATE);";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ TABLE);
        onCreate(sqLiteDatabase);
    }



    public long insertSMSContact(String name, String phone) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("name", name);
        values.put("phone", phone);

        // insert row
        long id = db.insert(TABLE, null, values);

        // close db connection
        db.close();

        // return newly inserted row id
        return id;
    }


    public List<Person> getAllSMSContacts(String date) {
        List<Person> persons = new ArrayList<>();

        // Select All Query
        //String selectQuery = "SELECT name,phone from " + TABLE + " where timestamp="+date;
        String[] selectionArgs = { date };
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE,null, "timestamp = ?",selectionArgs,null,null,null);

        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String phone = cursor.getString(cursor.getColumnIndex("phone"));
                Person person = new Person(name,phone);

                persons.add(person);
            } while (cursor.moveToNext());
        }

        db.close();

        return persons;
    }

}
