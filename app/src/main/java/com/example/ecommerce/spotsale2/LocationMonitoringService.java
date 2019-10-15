package com.example.ecommerce.spotsale2;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;

import com.example.ecommerce.spotsale2.DatabaseClasses.Location;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class LocationMonitoringService extends Service
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient googleApiClient;
    private MapLocationCallback locationCallback = new MapLocationCallback();
    private final int locationRequestCode = 1000;
    private LocationRequest locationRequest = new LocationRequest();

    private final String TAG = "LocationService";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(TAG, "onStartCommand: starting");

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        googleApiClient.connect();

        locationRequest.setInterval(Constants.LOCATION_INTERVAL)
                .setFastestInterval(Constants.LOCATION_FASTEST_INTERVAL)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        //return super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        Log.d(TAG, "onConnected: CONNECTED");
        Toast.makeText(this, "Connected Successfully", Toast.LENGTH_SHORT).show();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            /*ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    locationRequestCode);*/

        } else {
            LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(locationRequest, locationCallback, null);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended: SUSPENDED");
        Toast.makeText(this, "Connected Suspended", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed: FAILED");
        Toast.makeText(this, "Connected Failed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {

        LocationServices.getFusedLocationProviderClient(this).removeLocationUpdates(locationCallback);

        googleApiClient.disconnect();

        super.onDestroy();
    }

    class MapLocationCallback extends LocationCallback {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Log.d(TAG, "onLocationResult: Result = " + locationResult.toString());
            FirebaseFirestore.getInstance().collection("locations")
                    .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .set(new Location(locationResult.getLastLocation()));
        }

        @Override
        public void onLocationAvailability(LocationAvailability locationAvailability) {
            super.onLocationAvailability(locationAvailability);
            if(!locationAvailability.isLocationAvailable()) {
                Log.d(TAG, "onLocationAvailability: Location UNAVAILABLE");
            }
        }
    }
}
