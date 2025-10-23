package com.example.lab3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class HotelsDB {
    private HotelsDBHelper hotelsDbHelper;

    public HotelsDB(Context context) {
        hotelsDbHelper = new HotelsDBHelper(context);
    }

    public void insertHotel(String hotelName, String hotelAddress, String hotelWebpage, String hotelPhone) {
        SQLiteDatabase db = hotelsDbHelper.getWritableDatabase();

        ContentValues contentVals = new ContentValues();
        contentVals.put(HotelsDBSchema.HotelsTable.COLUMN_NAME_HOTEL_NAME, hotelName);
        contentVals.put(HotelsDBSchema.HotelsTable.COLUMN_NAME_HOTEL_ADDRESS, hotelAddress);
        contentVals.put(HotelsDBSchema.HotelsTable.COLUMN_NAME_HOTEL_WEBPAGE, hotelWebpage);
        contentVals.put(HotelsDBSchema.HotelsTable.COLUMN_NAME_HOTEL_PHONE_NB, hotelPhone);

        db.insert(HotelsDBSchema.HotelsTable.TABLE_NAME, null, contentVals);
    }

    public Cursor getHotels() {
        SQLiteDatabase db = hotelsDbHelper.getReadableDatabase();
        String[] columns = {
                HotelsDBSchema.HotelsTable._ID, HotelsDBSchema.HotelsTable.COLUMN_NAME_HOTEL_NAME, HotelsDBSchema.HotelsTable.COLUMN_NAME_HOTEL_ADDRESS,
                HotelsDBSchema.HotelsTable.COLUMN_NAME_HOTEL_WEBPAGE, HotelsDBSchema.HotelsTable.COLUMN_NAME_HOTEL_PHONE_NB
        };

        String sortBy = HotelsDBSchema.HotelsTable.COLUMN_NAME_HOTEL_NAME;

        return db.query(
                HotelsDBSchema.HotelsTable.TABLE_NAME,
                columns,
                null,
                null,
                null,
                null,
                sortBy
        );
    }
}
