package com.rcprata.driveuino.core;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;

import com.rcprata.driveuino.utils.Globals;

/**
 * Created by rcprata on 7/11/13.
 */
public class AccelerometerService implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mSensor;
    private Handler mHandler;

    public AccelerometerService(Context context, Handler handler) {
        mHandler = handler;
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    public boolean registerAccelerometerServiceListener() {
        return mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

    public void unregisterAccelerometerServiceListener() {
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float[] values = new float[3];
        for (int i = 0; i < 3; i++)
            values[i] = sensorEvent.values[i];

        mHandler.obtainMessage(Globals.HANDLER_STATE_ACCELEROMETER, values).sendToTarget();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
