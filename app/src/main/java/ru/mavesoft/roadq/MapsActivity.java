package ru.mavesoft.roadq;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private ShakeProcessor shakeProcessor;

    private LocationManager locationManager;
    private LocationListener locationListener;

    TextView tvGForce;

    double currGForce;
    int currGFType;

    LatLng lastPosition;

    float metersPerMeasure = 10.0f;
    long timePerMeasure = 200;


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == 1)
        {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, timePerMeasure, metersPerMeasure, locationListener);
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        tvGForce = findViewById(R.id.tvGForce);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        shakeProcessor = new ShakeProcessor();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                LatLng currPosition = new LatLng(location.getLatitude(), location.getLongitude());

                if (lastPosition != null) {
                    switch (currGFType) {
                        case ShakeProcessor.HSHAKE_TYPE:
                            Polyline polyline = mMap.addPolyline(new PolylineOptions()
                                    .add(lastPosition, currPosition)
                            .width(8)
                            .color(Color.MAGENTA));
                            /*
                            mMap.addMarker(new MarkerOptions()
                                    .position(currPosition)
                                    .title("Your location")
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
                                    */
                            break;
                        case ShakeProcessor.MHSHAKE_TYPE:
                            Polyline polyline1 = mMap.addPolyline(new PolylineOptions()
                                    .add(lastPosition, currPosition)
                                    .width(8)
                                    .color(Color.RED));
                            break;
                        case ShakeProcessor.MSHAKE_TYPE:
                            Polyline polyline2 = mMap.addPolyline(new PolylineOptions()
                                    .add(lastPosition, currPosition)
                                    .width(8)
                                    .color(Color.rgb(255,165,0)));
                            break;
                        case ShakeProcessor.LMSHAKE_TYPE:
                            Polyline polyline3 = mMap.addPolyline(new PolylineOptions()
                                    .add(lastPosition, currPosition)
                                    .width(8)
                                    .color(Color.YELLOW));
                            break;
                        case ShakeProcessor.LSHAKE_TYPE:
                            Polyline polyline4 = mMap.addPolyline(new PolylineOptions()
                                    .add(lastPosition, currPosition)
                                    .width(8)
                                    .color(Color.GREEN));
                            break;
                    }
                }
                mMap.moveCamera(CameraUpdateFactory.newLatLng(currPosition));
                lastPosition = currPosition;

                mMap.setMyLocationEnabled(true);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        shakeProcessor.setOnShakeListener(new ShakeProcessor.OnShakeListener() {
            @Override
            public void onShake(double gForce) {
                tvGForce.setText( Double.toString(Math.round(gForce * 100.0) / 100.0) );
                Log.i("G - Force", Double.toString(Math.round(gForce * 100.0) / 100.0));

                if (Math.abs(1.0 - gForce) >= ShakeProcessor.HSHAKE_THRESHOLD_G) {
                    currGFType = ShakeProcessor.HSHAKE_TYPE;
                } else if (Math.abs(1.0 - gForce) >= ShakeProcessor.MHSHAKE_THRESHOLD_G) {
                    currGFType = ShakeProcessor.MHSHAKE_TYPE;
                } else if (Math.abs(1.0 - gForce) >= ShakeProcessor.MSHAKE_THRESHOLD_G) {
                    currGFType = ShakeProcessor.MSHAKE_TYPE;
                } else if (Math.abs(1.0 - gForce) >= ShakeProcessor.LMSHAKE_THRESHOLD_G) {
                    currGFType = ShakeProcessor.LMSHAKE_TYPE;
                } else if (Math.abs(1.0 - gForce) >= ShakeProcessor.LSHAKE_THRESHOLD_G) {
                    currGFType = ShakeProcessor.LSHAKE_TYPE;
                }
            }
        });

        // Checking for permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, timePerMeasure, metersPerMeasure, locationListener);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    public void startClick(View view) {
        sensorManager.registerListener(shakeProcessor, accelerometer, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
