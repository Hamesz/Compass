package com.example.compass;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import static java.lang.Math.abs;

public class Orientation implements SensorEventListener {

    public interface Listener {
        void onOrientationChanged(float degree);
    }

    private final SensorManager mSensorManager;
    @Nullable
    private final Sensor mRotationSensor; //can be null if phone is missing sensor

    private int mLastAccuracy;
    private Listener mListener;

    Activity activity;

    public Orientation(Activity activity) {
        mSensorManager = (SensorManager) activity.getSystemService(Activity.SENSOR_SERVICE);
        // Can be null if the sensor hardware is not available
        mRotationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
    }

    /**
     * Starts this class listening to the sensor events
     * @param listener: The sensor listener
     */
    public void startListening(Listener listener) {
        if (mListener == listener) {
            return;
        }
        mListener = listener;
        if (mRotationSensor == null) {
            Log.i("Orientation","Rotation vector sensor is not available!");
            return;
        }
        mSensorManager.registerListener(this, mRotationSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    /**
     * Stops the class from listening to sensor events
     */
    public void stopListening() {
        mSensorManager.unregisterListener(this);
        mListener = null;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (mListener == null) {
            return;
        }
        if (mLastAccuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) {
            Toast.makeText(activity.getApplicationContext(), "Sensor accuracy is unreliable, move device around!",
                    Toast.LENGTH_LONG).show();
        }
        if (sensorEvent.sensor == mRotationSensor) {
            calculateOrientation(sensorEvent.values);
        }
    }

    /**
     * Calculates the orientation of the device using the
     * rotation vecotr and sends the data to the listener
     * @param rotationVector: array of rotation vector values
     */
    private void calculateOrientation(float[] rotationVector) {
        float[] R = new float[9];
        float[] values = new float[3];
        SensorManager.getRotationMatrixFromVector(R, rotationVector);
        SensorManager.getOrientation(R, values);
        float degree = (float) Math.toDegrees(values[0]);
        mListener.onOrientationChanged(degree);
    }

    /**
     * Called when the accuracy of a sensor changes
     * @param sensor: the type of sensor
     * @param i: the new accuracy
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        if (mLastAccuracy != i && sensor == mRotationSensor ) {
            mLastAccuracy = i;
        }
    }

    public static int convertTo360Degrees(float degree){
        return (int) ((degree < 0) ? (360 - (abs(degree) % 360) ) %360 : (degree % 360));
    }
}
