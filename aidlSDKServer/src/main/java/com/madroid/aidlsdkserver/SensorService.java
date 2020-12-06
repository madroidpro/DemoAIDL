package com.madroid.aidlsdkserver;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class SensorService extends Service implements SensorEventListener {

    private ISensorImpl impl = new ISensorImpl();
    private SensorManager mSensorManager;
    private Sensor mRotationSensor;

    private static final int SENSOR_DELAY = 8 * 1000; // 8ms
    private static final int FROM_RADS_TO_DEGS = -57;
    private float pitch;
    private float roll;

    @Override
    public IBinder onBind(Intent intent) {
        try {
            mSensorManager = (SensorManager) getSystemService(Activity.SENSOR_SERVICE);
            mRotationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
            mSensorManager.registerListener(this, mRotationSensor, SENSOR_DELAY);
        } catch (Exception e) {
            Log.d("info_", "Hardware compatibility issue");
        }
        return impl;
    }

    class ISensorImpl extends ISensorAidlInterface.Stub {

        @Override
        public String getSensorData() throws RemoteException {
            /*Return Sensor information*/
            return "Pitch=" + pitch + " Roll-" + roll;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor == mRotationSensor) {
            Log.d("info_sensor_name", event.sensor.getName());
            if (event.values.length > 4) {
                float[] truncatedRotationVector = new float[4];
                System.arraycopy(event.values, 0, truncatedRotationVector, 0, 4);
                update(truncatedRotationVector);
            } else {
                update(event.values);
            }
        }
    }

    private void update(float[] vectors) {
        float[] rotationMatrix = new float[9];
        SensorManager.getRotationMatrixFromVector(rotationMatrix, vectors);
        int worldAxisX = SensorManager.AXIS_X;
        int worldAxisZ = SensorManager.AXIS_Z;
        float[] adjustedRotationMatrix = new float[9];
        SensorManager.remapCoordinateSystem(rotationMatrix, worldAxisX, worldAxisZ, adjustedRotationMatrix);
        float[] orientation = new float[3];
        SensorManager.getOrientation(adjustedRotationMatrix, orientation);
        pitch = orientation[1] * FROM_RADS_TO_DEGS;
        roll = orientation[2] * FROM_RADS_TO_DEGS;

        Log.d("info_sensor_data", pitch + "--" + roll);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}