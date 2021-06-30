package com.example.callback.drowsiness;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import androidx.fragment.app.FragmentActivity;

public class SensorActivity extends FragmentActivity implements SensorEventListener {
    private SensorManager sensorManager;
    Sensor accelerometer;
    double xAcceleration, yAcceleration, zAcceleration;
    private static int samplingPeriodUs = 2000000;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sensorManager=(SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer=sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        sensorManager.registerListener(this, accelerometer, samplingPeriodUs);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.d("TAG","X: "+event.values[0]+" Y: "+event.values[1]+" Z: "+event.values[2]);
        xAcceleration = event.values[0];
        yAcceleration = event.values[1];
        zAcceleration = event.values[2];
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
