package com.example.lab3;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ListHotels extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_list_hotels);
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Hotels");
        populateListView();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        populateListView();
    }

    public void addHotel(View view) {
        Intent intent = new Intent(this, NewHotel.class);
        startActivity(intent);
    }

    private void populateListView () {
        Cursor cursor = new HotelsDB(this).getHotels();
        String[] fromColumns = {
                HotelsDBSchema.HotelsTable.COLUMN_NAME_HOTEL_NAME, HotelsDBSchema.HotelsTable.COLUMN_NAME_HOTEL_ADDRESS,
                HotelsDBSchema.HotelsTable.COLUMN_NAME_HOTEL_WEBPAGE, HotelsDBSchema.HotelsTable.COLUMN_NAME_HOTEL_PHONE_NB
        };

        int[] toViews = {
                R.id.list_hotel_name, R.id.list_hotel_address_value,
                R.id.list_hotel_webpage_value, R.id.list_hotel_phone_value
        };

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                this,
                R.layout.single_hotel,
                cursor,
                fromColumns,
                toViews,
                0
        );

        ListView listView = findViewById(R.id.hotels_list_view);
        listView.setAdapter(adapter);
    }

    public void openMap(View view) {
        TextView address_container = findViewById(R.id.list_hotel_address_value);
        String[] address_coordinates = address_container.getText().toString().split(" ");
        Log.e("COORDINATES", "COORD: " + address_coordinates[0] + address_coordinates[1]);
        Uri gmmIntentUri = Uri.parse("google.streetview:cbll=" + address_coordinates[0] + address_coordinates[1]);
        Log.e("COORDINATES", "COORD_URI: " + gmmIntentUri);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }

    public void openWebsite(View view) {
        TextView webpage_container = findViewById(R.id.list_hotel_webpage_value);
        String url = webpage_container.getText().toString();
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        startActivity(intent);
    }

    public void callNumber(View view) {
        View callButtonParent = (View)view.getParent();
        TextView phoneNumberTextView = callButtonParent.findViewById(R.id.list_hotel_phone_value);
        String phoneNumber = "tel:" + phoneNumberTextView.getText().toString();
        Uri number = Uri.parse(phoneNumber);
        Intent callIntent = new Intent (Intent.ACTION_DIAL, number);
        startActivity (callIntent);
    }
}