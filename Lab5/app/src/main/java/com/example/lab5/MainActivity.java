package com.example.lab5;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationRequest;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
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

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {
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

        getSensorData();
        getCurrentLocation();
        createNotificationChannel();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
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

        float newAzimuth = (float)Math.toDegrees(orientation[0]);

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
        int priority = LocationRequest.QUALITY_BALANCED_POWER_ACCURACY;
        CancellationToken cancellationToken = new CancellationTokenSource().getToken();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    100);
            return;
        }

        fusedLocationClient.getCurrentLocation(priority, cancellationToken)
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        convertParameters();
                    } else {
                        fusedLocationClient.getLastLocation().addOnSuccessListener(lastLocation -> {
                            if (lastLocation != null) {
                                latitude = lastLocation.getLatitude();
                                longitude = lastLocation.getLongitude();
                                convertParameters();
                            } else {
                                Log.d("LATITUDE", "NO LOCATION AVAILABLE");
                            }
                        });
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

    private void createNotificationChannel() {
        CharSequence name = getString(R.string.nc_name);
        String description = getString(R.string.nc_description);
        int importance = NotificationManager.IMPORTANCE_HIGH;

        NotificationChannel notificationChannel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = new NotificationChannel("my_nc", name, importance);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel.setDescription(description);
        }
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(notificationChannel);
        }

        Intent compassIntent = new Intent(this, MainActivity.class);
        compassIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingCompassIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        compassIntent,
                        PendingIntent.FLAG_IMMUTABLE
                );
    }
}