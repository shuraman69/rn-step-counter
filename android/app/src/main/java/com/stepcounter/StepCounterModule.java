package com.stepcounter;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class StepCounterModule extends ReactContextBaseJavaModule implements SensorEventListener {
    private final ReactApplicationContext reactContext;
    private SensorManager sensorManager;
    private Sensor stepSensor;
    private int totalSteps = 0;

    public StepCounterModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
        sensorManager = (SensorManager) reactContext.getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        }

        if (stepSensor == null) {
            Log.e("StepCounterModule", "Step counter sensor not available");
        }
    }

    @NonNull
    @Override
    public String getName() {
        return "StepCounterModule";
    }

    @ReactMethod
    public void startStepCounter() {
        if (stepSensor != null) {
            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @ReactMethod
    public void stopStepCounter() {
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            int newSteps = (int) event.values[0];
            int stepsSinceLastUpdate = newSteps - totalSteps;
            totalSteps = newSteps;

            // Send step count to JavaScript side
            reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                    .emit("onStepCountChange", stepsSinceLastUpdate);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do nothing for now
    }
}
