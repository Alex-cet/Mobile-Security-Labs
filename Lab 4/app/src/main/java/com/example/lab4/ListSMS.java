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

    private BroadcastReceiver smsReceivedBR = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent intent) {
            String addr = intent.getStringExtra("sender");
            String ts = intent.getStringExtra("timestamp");
            String body = intent.getStringExtra("message");
            String type = "2"; // incoming message

            Message message = new Message(addr, ts, body, type);

            // Add the new message to the adapter
            // Call notifyDataSetChanged on the adapter
        }
    };
    private static final int READ_SMS_PERMISSION_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_list_sms);

        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Messaging Application");

        LocalBroadcastManager.getInstance(this).registerReceiver(smsReceivedBR, new IntentFilter("NEW_SMS_RECEIVED"));
        Intent newSmsReceived = new Intent("NEW_SMS_RECEIVED");
        newSmsReceived.putExtra("address", address);
        newSmsReceived.putExtra("date", date);
        newSmsReceived.putExtra("body", body);

        LocalBroadcastManager.getInstance(context)
                .sendBroadcast(newSmsReceived);


        ArrayList<Message> messages = new ArrayList<>();
        MessageAdapter messageAdapter = new MessageAdapter(this, messages);
        ListView listView = findViewById(R.id.sms_list_view);
        listView.setAdapter(messageAdapter);

        int readSmsPerm = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS);
        if (readSmsPerm == PackageManager.PERMISSION_GRANTED) {
            // Read the SMS messages
            } else {
            // Request the READ_SMS permission
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
            ContentResolver contentResolver = getContentResolver();
            String[] projection = new String[]{
                    Telephony.Sms.DATE, Telephony.Sms.TYPE,
                    Telephony.Sms.ADDRESS, Telephony.Sms.BODY,
            };

            Cursor cursor = contentResolver.query(
                    Telephony.Sms.CONTENT_URI,
                    projection,
                    null,
                    null,
                    null,
                    null
            );

            if (cursor == null) {
                return;
            }

            if (cursor.moveToFirst()) {
                do {
                    // Iteration over the SMS messages

                    //
                    // Retrieve the fields of interest :
                    // Telephony . Sms . ADDRESS , Telephony . Sms . BODY ,
                    // Telephony . Sms . TYPE and Telephony . Sms . DATE
                    //
                    // Example :
                    String body = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.BODY));

                    // Save the message in an array

                } while (cursor.moveToNext());
            }

            cursor.close();
        }
    }

    public void createNewMessage(View view) {
        Intent intent = new Intent(this, NewMessage.class);
        Toolbar toolbar = findViewById(R.id.conversation_toolbar);
        toolbar.setTitle(findViewById(R.id.recipient_phone_number));
        startActivity(intent);
    }
}