package com.pickle.googleservicetester;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String TAG = "MAIN_ACTIVITY_LOG";
    private static final float DEFAULT_ZOOM = 12;
    private GoogleMap mMap;
    private Button mLocalizeMeButton;

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    private Boolean mLocationPermissionGranted = false;

    private Location mLastKnowLocation;

    private FusedLocationProviderClient mFusedLocationProviderClient;

    private GeoDataClient mGeoDataClient;

    private PlaceDetectionClient mPlaceDetectionClient;
    private LatLng mDefaultLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mGeoDataClient = Places.getGeoDataClient(this);
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        mDefaultLocation = new LatLng(37.960214, 58.329259);

        mLocalizeMeButton = findViewById(R.id.localizeMeButton);
        mLocalizeMeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //launch procedure to localize me
                getLocationPermission();
                if(mLocationPermissionGranted){
                    updateLocationUI();
                    getDeviceLocation();
                }
            }
        });
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


    }

    private void updateLocationUI() {
        if(mMap == null){
            return;
        }

        try {
            if(mLocationPermissionGranted){
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            }else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnowLocation = null;
                getLocationPermission();
            }
        }catch( SecurityException e){
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getDeviceLocation() {
        try {
            if(mLocationPermissionGranted){
                @SuppressLint("MissingPermission") Task locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            mLastKnowLocation = (Location) task.getResult();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(
                                            mLastKnowLocation.getLatitude(),
                                            mLastKnowLocation.getLongitude()
                                    ), DEFAULT_ZOOM
                            )
                            );
                        }else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e){
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            mLocationPermissionGranted = false;
            switch (requestCode) {
                case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                    // If request is cancelled, the result arrays are empty.
                    if (grantResults.length > 0
                            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        mLocationPermissionGranted = true;
                    }
                }
            }
            updateLocationUI();

    }

    protected void getLocationPermission(){
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }
}
