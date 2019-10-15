package com.example.ecommerce.spotsale2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity
        implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private MapLocationCallback locationCallback = new MapLocationCallback();

    private final int locationRequestCode = 1000;
    private final int UPDATE_INTERVAL = 5000, FASTEST_INTERVAL = 2500;

    private AddressResultReceiver resultReceiver;
    private ArrayList<Address> addresses;

    private Address sourceAddress, destinationAddress;
    private DocumentReference sourceRef, destRef;

    private Marker sourceMarker, destMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        resultReceiver = new MapsActivity.AddressResultReceiver(new Handler());

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(googleApiClient != null) {
            googleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(googleApiClient != null && googleApiClient.isConnected()) {
            LocationServices.getFusedLocationProviderClient(this).removeLocationUpdates(locationCallback);
            googleApiClient.disconnect();
        }
        sourceRef.delete();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(googleApiClient != null && googleApiClient.isConnected()) {
            LocationServices.getFusedLocationProviderClient(this).removeLocationUpdates(locationCallback);
            googleApiClient.disconnect();
        }
        sourceRef.delete();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case locationRequestCode: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startLocationService();
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }


    protected void startIntentService(Location location) {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, resultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, location);
        startService(intent);
    }

    protected String getAddressString(@NonNull Address address) {
        ArrayList<String> addressFragments = new ArrayList<>();
        for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
            addressFragments.add(address.getAddressLine(i));
        }
        return TextUtils.join(System.getProperty("line.separator"), addressFragments);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        Log.d("ADDRESSES", "onConnected: CONNECTED");
        Toast.makeText(this, "Connected Successfully", Toast.LENGTH_SHORT).show();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    locationRequestCode);

        } else {
            LocationServices.getFusedLocationProviderClient(this).getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            startIntentService(location);
                        }
                    });

            startLocationService();
        }
    }

    protected void startLocationService() {
        LocationRequest locationRequest = new LocationRequest()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);

        Log.d("ADDRESSES", "startLocationService: STARTED");
        Toast.makeText(this, "Location Updates ON", Toast.LENGTH_LONG).show();

        LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(locationRequest, locationCallback, null);

        sourceRef = FirebaseFirestore.getInstance().collection("locations")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        //destRef = FirebaseFirestore.getInstance().collection("locations").document(seller.getUID());
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    class MapLocationCallback extends LocationCallback {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            startIntentService(locationResult.getLastLocation());
        }

        @Override
        public void onLocationAvailability(LocationAvailability locationAvailability) {
            super.onLocationAvailability(locationAvailability);
            if(!locationAvailability.isLocationAvailable()) {
                Toast.makeText(MapsActivity.this, "Location Not Available", Toast.LENGTH_SHORT).show();
            }
        }
    }

    class AddressResultReceiver extends ResultReceiver {

        LatLngBounds.Builder bounds = new LatLngBounds.Builder();

        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            if(resultData == null) {
                return;
            }

            if(resultCode == Constants.SUCCESS_RESULT) {
                addresses = resultData.getParcelableArrayList(Constants.RESULT_DATA_KEY);
                if (addresses == null) {
                    addresses = new ArrayList<>();
                    return;

                }

                //Toast.makeText(MapsActivity.this, "Location Update Received", Toast.LENGTH_LONG).show();

                for (Address address : addresses) {
                    Log.d("ADDRESSES", "String: " + getAddressString(address));
                    Log.d("ADDRESSES", "Coords: " + address.getLatitude() + "," + address.getLongitude());
                }

                //if(sourceAddress == null){
                    sourceAddress = addresses.get(0);
                    LatLng source = new LatLng(sourceAddress.getLatitude(), sourceAddress.getLongitude());
                    sourceRef.set(source);
                    bounds.include(source);
                    if(sourceMarker == null) {
                        sourceMarker = mMap.addMarker(new MarkerOptions().position(source).title("You"));
                    } else {
                        sourceMarker.setPosition(source);
                    }
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(source, 16.0f));
                /*} else {
                    destinationAddress = addresses.get(0);
                    LatLng dest = new LatLng(destinationAddress.getLatitude(), destinationAddress.getLongitude());
                    bounds.include(dest);
                    if(destMarker == null) {
                        destMarker = mMap.addMarker(new MarkerOptions().position(dest).title("Them"));
                    } else {
                        destMarker.setPosition(dest);
                    }
                    mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 10));
                }*/

                Log.d("ADDRESSES", "received addreses count:" + resultData.getInt("COUNT"));
            } else {
                Toast.makeText(MapsActivity.this, resultData.getString(Constants.RESULT_DATA_KEY), Toast.LENGTH_LONG).show();
            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
