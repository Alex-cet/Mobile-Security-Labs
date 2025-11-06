package com.example.lab4;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class MessageAdapter extends ArrayAdapter<Message> {
        public MessageAdapter (Context context, ArrayList<Message> messages) {
            super(context, 0, messages);
        }

        @Override
        public View getView (int position, View convertView, ViewGroup parent) {
            Message message = getItem(position);

            if (message == null) {
                return convertView;
            }

            if (convertView == null) {
                convertView = LayoutInflater
                        .from(getContext())
                        .inflate(R.layout.single_sms, parent, false);
            }

            LinearLayout messageBox = (LinearLayout) convertView;

            if ("1".equals(message.message_type)) {
                messageBox.setBackgroundResource(R.drawable.custom_border);
                messageBox.setGravity(android.view.Gravity.START);
            } else {
                messageBox.setBackgroundResource(R.drawable.custom_border_user);
                messageBox.setGravity(android.view.Gravity.END);
            }

            // Get all of the needed elements
            // Example :
            TextView textPreviewTextView = (TextView)convertView.findViewById(R.id.message_text);
            TextView messageDateTextView = (TextView)convertView.findViewById(R.id.message_date);
            TextView recipientPhoneNumberTextView = (TextView)convertView.findViewById(R.id.recipient_phone_number);

            // Set all of the needed fields
            // Example :
            textPreviewTextView.setText(message.message_text);
            messageDateTextView.setText(message.message_date);
            recipientPhoneNumberTextView.setText(message.recipient_phone_number);

            return convertView;
        }
}

