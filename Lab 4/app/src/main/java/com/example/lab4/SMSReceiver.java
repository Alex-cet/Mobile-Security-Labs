package com.example.lab4;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class SMSReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle == null) {
            return;
        }

        Object[] pdus = (Object[]) bundle.get("pdus");
        if (pdus == null) {
            return;
        }

        String format = bundle.getString("format");

        long date = 0;
        String address = null;
        StringBuilder body = new StringBuilder();

        for (Object pdu : pdus) {
            SmsMessage receivedSms = SmsMessage.createFromPdu((byte[]) pdu, format);

            String bodyComp = receivedSms.getMessageBody();
            body.append(bodyComp);

            if (address == null) {
                address = receivedSms.getOriginatingAddress();
            }

            if (date == 0) {
                date = receivedSms.getTimestampMillis();
            }
        }

        Intent newSmsReceived = new Intent("NEW_SMS_RECEIVED");
        newSmsReceived.putExtra("sender", address);
        newSmsReceived.putExtra("timestamp", String.valueOf(date));
        newSmsReceived.putExtra("message", body.toString());

        LocalBroadcastManager.getInstance(context).sendBroadcast(newSmsReceived);
    }
}
