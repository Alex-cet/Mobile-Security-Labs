package com.example.lab3;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class HotelsDBHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "Hotels.db";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + HotelsDBSchema.HotelsTable.TABLE_NAME +
                    " (" + HotelsDBSchema.HotelsTable._ID +
                    " INTEGER PRIMARY KEY," + HotelsDBSchema.HotelsTable.COLUMN_NAME_HOTEL_NAME +
                    ", " + HotelsDBSchema.HotelsTable.COLUMN_NAME_HOTEL_ADDRESS + ", " +
                    HotelsDBSchema.HotelsTable.COLUMN_NAME_HOTEL_WEBPAGE + ", " +
                    HotelsDBSchema.HotelsTable.COLUMN_NAME_HOTEL_PHONE_NB + ") ";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + HotelsDBSchema.HotelsTable.TABLE_NAME;

    public HotelsDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}

