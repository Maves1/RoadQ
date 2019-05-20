package ru.mavesoft.roadq;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class ShakeProcessor implements SensorEventListener {

    static final float LSHAKE_THRESHOLD_G = 0.05f;
    static final float LMSHAKE_THRESHOLD_G = 0.1f;
    static final float MSHAKE_THRESHOLD_G = 0.15f;
    static final float MHSHAKE_THRESHOLD_G = 0.2f;
    static final float HSHAKE_THRESHOLD_G = 0.3f;

    static final int HSHAKE_TYPE = 4;
    static final int MHSHAKE_TYPE = 3;
    static final int MSHAKE_TYPE = 2;
    static final int LMSHAKE_TYPE = 1;
    static final int LSHAKE_TYPE = 0;

    static final int SHAKE_DETECTION_FREQUENCY = 100;

    long lastTime;
    private OnShakeListener onShakeListener;

    public interface OnShakeListener {
        public void onShake(double gForce);
    }

    public void setOnShakeListener(OnShakeListener onShakeListener) {
        this.onShakeListener = onShakeListener;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        long currTime = System.currentTimeMillis();
            if (currTime - lastTime >= SHAKE_DETECTION_FREQUENCY) {

            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            float gX = x / SensorManager.GRAVITY_EARTH;
            float gY = y / SensorManager.GRAVITY_EARTH;
            float gZ = z / SensorManager.GRAVITY_EARTH;

            double gForce = Math.sqrt(gX * gX + gY * gY + gZ * gZ);

            onShakeListener.onShake(gForce);

        }
        lastTime = System.currentTimeMillis();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
