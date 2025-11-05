package com.example.lab4;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.Telephony;
import android.view.View;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.ArrayList;

public class ListSMS extends AppCompatActivity {
    private MessageAdapter messageAdapter;
    private ArrayList<Message> messages;
    private static final int READ_SMS_PERMISSION_CODE = 101;
    private BroadcastReceiver smsReceivedBR = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent intent) {
            String addr = intent.getStringExtra("sender");
            String ts = intent.getStringExtra("timestamp");
            String body = intent.getStringExtra("message");
            String type = "1"; // incoming message

            Message message = new Message(addr, ts, body, type);

            // Add the new message to the top of the adapter and refresh the view
            messages.add(0, message);
            messageAdapter.notifyDataSetChanged();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_list_sms);

        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Messaging Application");

        this.messages = new ArrayList<>();
        this.messageAdapter = new MessageAdapter(this, messages);
        ListView listView = findViewById(R.id.sms_list_view);
        listView.setAdapter(messageAdapter);

        LocalBroadcastManager.getInstance(this).registerReceiver(smsReceivedBR, new IntentFilter("NEW_SMS_RECEIVED"));
        int readSmsPerm = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS);
        if (readSmsPerm == PackageManager.PERMISSION_GRANTED) {
            readMessage();
        } else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_SMS}, READ_SMS_PERMISSION_CODE);
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    public void onRequestPermissionsResult (int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_SMS_PERMISSION_CODE &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            readMessage();
        }
    }

    public void createNewMessage(View view) {
        Intent intent = new Intent(this, NewMessage.class);
        startActivity(intent);
    }

    public void readMessage() {
        messages.clear();

        ContentResolver contentResolver = getContentResolver();
        String[] projection = new String[] {
                Telephony.Sms.DATE, Telephony.Sms.TYPE,
                Telephony.Sms.ADDRESS, Telephony.Sms.BODY,
        };

        String sortOrder = Telephony.Sms.DATE + " DESC";

        Cursor cursor = contentResolver.query(
                Telephony.Sms.CONTENT_URI,
                projection,
                null,
                null,
                sortOrder,
                null
        );

        if (cursor == null) {
            return;
        }

        if (cursor.moveToFirst()) {
            do {
                // Retrieve the fields of interest :
                String address = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.ADDRESS));
                String body = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.BODY));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.DATE));
                String type = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.TYPE));

                // Create a new Message object
                Message message = new Message(address, date, body, type);

                // Save the message in an array
                messages.add(message);

            } while (cursor.moveToNext());
        }

        cursor.close();
        this.messageAdapter.notifyDataSetChanged(); // Refresh the ListView
    }
}