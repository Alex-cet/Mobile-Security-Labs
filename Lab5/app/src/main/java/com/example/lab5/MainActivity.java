package com.example.lab5;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String START_VOICE_RECORDING = "START_VOICE_RECORDING";
    public static final String STOP_VOICE_RECORDING = "STOP_VOICE_RECORDING";
    public static final String START_READING_CONTACTS = "START_READING_CONTACTS";
    public static final String STOP_READING_CONTACTS = "STOP_READING_CONTACTS";
    float[] accelerometerValues;
    float[] magneticFieldValues;
    float oldAzimuth = 0f;

    double latitude;
    double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        String[] requiredPermissions = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.POST_NOTIFICATIONS};

        // Check if we already have all permissions
        boolean allPermissionsGranted = true;
        for (String permission : requiredPermissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                allPermissionsGranted = false;
                break;
            }
        }

        if (allPermissionsGranted) {
            startAppFunctions();
        } else {
            ActivityCompat.requestPermissions(this, requiredPermissions, 100);
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void startAppFunctions() {
        getSensorData();
        getCurrentLocation();
        createResultReceiver();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            boolean locationGranted = false;
            boolean audioGranted = false;
            boolean contactsGranted = false;

            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                int grantResult = grantResults[i];

                if (grantResult == PackageManager.PERMISSION_GRANTED) {
                    if (permission.equals(Manifest.permission.ACCESS_FINE_LOCATION) ||
                            permission.equals(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                        locationGranted = true;
                    } else if (permission.equals(Manifest.permission.RECORD_AUDIO)) {
                        audioGranted = true;
                    } else if (permission.equals(Manifest.permission.READ_CONTACTS)) {
                        contactsGranted = true;
                    }
                }
            }

            if (locationGranted && audioGranted && contactsGranted) {
                startAppFunctions();
            } else {
                TextView maliciousActivityTextView = findViewById(R.id.maliciousActivityText);
                maliciousActivityTextView.setText("This app requires Location, Audio, and Contacts permissions to function.");
            }
        }
    }

    private void getSensorData() {
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor magneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        // Accelerometer
        sensorManager.registerListener(
                new SensorEventListener() {
                    @Override
                    public void onSensorChanged(SensorEvent sensorEvent) {
                        accelerometerValues = sensorEvent.values.clone();
                        rotateCompassImage();
                    }

                    @Override
                    public void onAccuracyChanged(Sensor s, int i) {
                    }
                },
                accelerometer,
                SensorManager.SENSOR_DELAY_NORMAL
        );

        // Magnetic field
        sensorManager.registerListener(
                new SensorEventListener() {
                    @Override
                    public void onSensorChanged(SensorEvent sensorEvent) {
                        magneticFieldValues = sensorEvent.values.clone();
                        rotateCompassImage();
                    }

                    @Override
                    public void onAccuracyChanged(Sensor s, int i) {
                    }
                },
                magneticField,
                SensorManager.SENSOR_DELAY_NORMAL
        );
    }

    private void rotateCompassImage() {
        if (accelerometerValues == null || magneticFieldValues == null) {
            return;
        }

        float[] rotationMatrix = new float[9];
        SensorManager.getRotationMatrix(
                rotationMatrix,
                null,
                accelerometerValues,
                magneticFieldValues
        );

        float[] orientation = new float[3];
        SensorManager.getOrientation(
                rotationMatrix,
                orientation
        );

        float newAzimuth = (float) Math.toDegrees(orientation[0]);

        RotateAnimation rotateAnimation = new RotateAnimation(
                oldAzimuth,
                newAzimuth,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f
        );
        rotateAnimation.setFillAfter(true);
        rotateAnimation.setDuration(250);
        try {
            rotateAnimation.setInterpolator(LinearInterpolator.class.newInstance());
        } catch (IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        }
        ImageView compassImageView = findViewById(R.id.compassImage);
        compassImageView.startAnimation(rotateAnimation);
        oldAzimuth = newAzimuth;
    }

    private void getCurrentLocation() {
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        int priority = com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY;
        CancellationToken cancellationToken = new CancellationTokenSource().getToken();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    100);
            return;
        }

        fusedLocationClient.getCurrentLocation(priority, cancellationToken)
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location == null) {
                            return;
                        }
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();

                        convertParameters();
                    }
                });
    }

    private void convertParameters() {
        Geocoder geocoder = new Geocoder(this);
        String locationString = "Unknown location";

        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    1
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (addresses == null || addresses.isEmpty()) {
            return;
        }

        Address address = addresses.get(0);
        StringBuilder addressSb = new StringBuilder();

        for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
            addressSb
                    .append(address.getAddressLine(i))
                    .append(" ");
        }

        locationString = addressSb.toString();

        TextView currentLocationTextView = findViewById(R.id.currentLocation);
        currentLocationTextView.setText(locationString);

    }

    private void createResultReceiver() {
        ResultReceiver r = new ResultReceiver(new Handler()) {
            @Override
            protected void onReceiveResult(int status, Bundle data) {
                super.onReceiveResult(status, data);
                TextView maliciousActivityTextView = findViewById(R.id.maliciousActivityText);

                String type = data.getString("type");

                if (type == null) return;

                if (type.equals(START_VOICE_RECORDING)) {
                    maliciousActivityTextView.setText("Recording audio");
                } else if (type.equals(START_READING_CONTACTS)) {
                    maliciousActivityTextView.setText("Reading contacts");
                } else if (type.equals(STOP_VOICE_RECORDING) || type.equals(STOP_READING_CONTACTS)) {
                    maliciousActivityTextView.setText("Idle");
                }
            }
        };

        Intent intent = new Intent(this, MyService.class);
        intent.putExtra("resultReceiver", r);
        this.startService(intent);
    }
}