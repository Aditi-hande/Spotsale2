package com.example.ecommerce.spotsale2;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.example.ecommerce.spotsale2.DatabaseClasses.Location;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class LocationMonitoringService extends Service
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient googleApiClient;
    private MapLocationCallback locationCallback = new MapLocationCallback();
    private final int locationRequestCode = 1000;
    private LocationRequest locationRequest = new LocationRequest();

    private final String TAG = "LocationService";
    private static final int NOTIFICATION_ID = 1;

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
            showForegroundNotification("Location Service is ON");
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
        Toast.makeText(this, "Connected Failed", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroy() {

        LocationServices.getFusedLocationProviderClient(this).removeLocationUpdates(locationCallback);

        googleApiClient.disconnect();

        Toast.makeText(this, "Location Services OFF", Toast.LENGTH_LONG).show();

        FirebaseFirestore.getInstance().collection("locations")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid()).delete();

        Globals.isLocationMonitoringServiceRunning = false;

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

    private void showForegroundNotification(String contentText) {
        // Create intent that will bring our app to the front, as if it was tapped in the app
        // launcher
        Intent showTaskIntent = new Intent(getApplicationContext(), MeetUpActivity.class);
        showTaskIntent.setAction(Intent.ACTION_MAIN);
        showTaskIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        showTaskIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent contentIntent = PendingIntent.getActivity(
                getApplicationContext(),
                0,
                showTaskIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            String NOTIFICATION_CHANNEL_ID = "com.example.ecommerce.spotsale2";
            String channelName = "Location Service";
            NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
            chan.setLightColor(Color.BLUE);
            chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            assert manager != null;
            manager.createNotificationChannel(chan);

            Notification.Builder notifBuilder = new Notification.Builder(getApplicationContext(), NOTIFICATION_CHANNEL_ID)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText(contentText)
                    .setSmallIcon(R.mipmap.ic_logo_round)
                    .setWhen(System.currentTimeMillis())
                    .setContentIntent(contentIntent);
            startForeground(NOTIFICATION_ID, notifBuilder.build());

        } else {

            Notification.Builder notifBuilder = new Notification.Builder(getApplicationContext())
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText(contentText)
                    .setSmallIcon(R.mipmap.ic_logo_round)
                    .setWhen(System.currentTimeMillis())
                    .setContentIntent(contentIntent);
            startForeground(NOTIFICATION_ID, notifBuilder.build());

        }
    }
}
