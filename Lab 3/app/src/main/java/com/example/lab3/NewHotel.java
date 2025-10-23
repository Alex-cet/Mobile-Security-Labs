package com.example.lab3;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class NewHotel extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_hotel);

        Toolbar toolbar = findViewById(R.id.new_hotel_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Hotels");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void saveHotel(View view) {
        EditText hotel_name_container = findViewById(R.id.hotel_name);
        EditText hotel_address_container = findViewById(R.id.hotel_address);
        EditText hotel_webpage_container = findViewById(R.id.hotel_webpage);
        EditText hotel_phone_number_container = findViewById(R.id.hotel_phone_number);
        String hotel_name = hotel_name_container.getText().toString();
        String hotel_address = hotel_address_container.getText().toString();
        String hotel_webpage = hotel_webpage_container.getText().toString();
        String hotel_phone_number = hotel_phone_number_container.getText().toString();

        HotelsDB hotelsDB = new HotelsDB(this);
        hotelsDB.insertHotel(hotel_name, hotel_address, hotel_webpage, hotel_phone_number);
        Toast.makeText(this, hotel_name + " saved successfully!", Toast.LENGTH_SHORT).show();
        finish();
    }
}