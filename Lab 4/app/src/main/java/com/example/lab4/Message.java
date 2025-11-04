package com.example.lab4;

import java.util.Date;

public class Message {
    public String recipient_phone_number;
    public String message_date;
    public String message_text;
    public String message_type;

    public Message(String recipient_phone_number, String message_date, String message_text, String message_type) {
        this.recipient_phone_number = recipient_phone_number;
        this.message_date = message_date;
        this.message_text = message_text;
        this.message_type = message_type;
    }
}
