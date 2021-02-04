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

/**
 * This class deals with determining the orientation
 * of the user, using a rotation vector sensor.
 * <p>
 * It implements the sensorEventListener so it can update the orientation
 * when the rotation vector values gave changed
 * @author James Hanratty
 */
public class Orientation implements SensorEventListener {

    /**
     * Interface listeners must implement in order to use
     * this class
     */
    public interface Listener {
        void onOrientationChanged(float degree);
    }

    private final SensorManager mSensorManager;
    @Nullable
    private final Sensor mRotationSensor; // can be null if phone is missing sensor

    // the last seen accuracy of the sensor
    private int mLastAccuracy;
    // the listener attached to get the orientaiton value
    private Listener mListener;
    // the activity using this object
    Activity activity;

    public Orientation(Activity activity) {
        mSensorManager = (SensorManager) activity.getSystemService(Activity.SENSOR_SERVICE);
        // Can be null if the sensor hardware is not available
        mRotationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        this.activity = activity;
    }

    /**
     * Starts this class listening to the sensor events
     * @param listener  The sensor listener
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

    /**
     * calls to update the angle now pointing when there is new data
     * and that data comes from the roation vector sensor
     * @param sensorEvent   A sensor event
     * @see SensorEvent
     */
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (mListener == null) {
            return;
        }
        if (mLastAccuracy != SensorManager.SENSOR_STATUS_ACCURACY_HIGH) {
            Toast.makeText(activity.getApplicationContext(), "Sensor accuracy is unreliable, move device around!",
                    Toast.LENGTH_LONG).show();
            Log.d("Orientation","Sensor accuracy is too low at: " + mLastAccuracy);
        }
        if (sensorEvent.sensor == mRotationSensor) {
            calculateOrientation(sensorEvent.values);
        }
    }

    /**
     * Calculates the orientation of the device using the
     * rotation vector and sends the data to the listener
     * @param rotationVector    Array of rotation vector values
     * @see Sensor
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
     * @param sensor    The type of sensor
     * @param accuracy  The new accuracy
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.d("Orientation","Accuracy: "+accuracy);
        if (mLastAccuracy != accuracy && sensor == mRotationSensor ) {
            mLastAccuracy = accuracy;
        }
    }

    /**
     * Static method that will convert orientation of [-180,180]
     * to and angle between [0,360]
     * @param degree    Angle between [-180,180]
     * @return          Angle between [0,360]
     */
    public static int convertTo360Degrees(float degree){
        return (int) ((degree < 0) ? (360 - (abs(degree) % 360) ) %360 : (degree % 360));
    }
}
