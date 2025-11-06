package com.example.lab4;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.Telephony;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class Conversation extends AppCompatActivity {
    private MessageAdapter adapter;
    private ArrayList<Message> conversationMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_conversation);

        Toolbar toolbar = findViewById(R.id.conversation_toolbar);
        setSupportActionBar(toolbar);
        String currentSender = getIntent().getStringExtra("RECIPIENT_PHONE_NUMBER");
        getSupportActionBar().setTitle(currentSender);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Setup List and Adapter similar to ListSMS
        ListView conversationListView = findViewById(R.id.sms_list_view_conversation);
        conversationMessages = new ArrayList<>();
        adapter = new MessageAdapter(this, conversationMessages);
        conversationListView.setAdapter(adapter);

        loadConversation(currentSender);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void loadConversation(String senderNum) {
        ContentResolver contentResolver = getContentResolver();
        String selection = Telephony.Sms.ADDRESS + " = ?";
        String[] selectionArgs = { senderNum };
        String sortOrder = Telephony.Sms.DATE + " ASC";

        Cursor cursor = contentResolver.query(
                Telephony.Sms.CONTENT_URI,
                null,
                selection,
                selectionArgs,
                sortOrder
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String address = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.ADDRESS));
                String body = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.BODY));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.DATE));
                String type = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.TYPE));
                conversationMessages.add(new Message(address, date, body, type));
            } while (cursor.moveToNext());
            cursor.close();
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}