package com.example.lab3;

import android.provider.BaseColumns;

public final class HotelsDBSchema {
    private HotelsDBSchema () {}
    /* Inner class that defines the table contents */
    public static class HotelsTable implements BaseColumns {
        public static final String TABLE_NAME = "hotels";
        public static final String COLUMN_NAME_HOTEL_NAME = "hotel_name";
        public static final String COLUMN_NAME_HOTEL_ADDRESS = "address";
        public static final String COLUMN_NAME_HOTEL_WEBPAGE = "webpage";
        public static final String COLUMN_NAME_HOTEL_PHONE_NB = "phone_nb";
    }
}

