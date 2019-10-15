package com.example.ecommerce.spotsale2;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
//import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.ecommerce.spotsale2.DatabaseClasses.Location;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private final int locationRequestCode = 1000;

    private AddressResultReceiver resultReceiver;
    private ArrayList<Address> addresses;

    private Address sourceAddress, destinationAddress;
    private DocumentReference sourceRef, destRef;

    private Marker sourceMarker, destMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        resultReceiver = new MapsActivity.AddressResultReceiver(new Handler());

        sourceRef = FirebaseFirestore.getInstance().collection("locations")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid());

        sourceRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(e != null) {
                    Log.d("SnapshotListener", "Listen Failed");
                }
                if(documentSnapshot != null && documentSnapshot.exists()) {
                    startIntentService(documentSnapshot.toObject(Location.class));
                }
            }
        });

        destRef = FirebaseFirestore.getInstance().collection("locations")
                .document("");

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    locationRequestCode);
        } else {
            startLocationService();
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this)
                .setNeutralButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        MapsActivity.super.onBackPressed();
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        MapsActivity.super.onBackPressed();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .setMessage("Do you wish to stop Location Service?");
        alertBuilder.create().show();
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

    protected void startLocationService() {

        if(!Globals.isLocationMonitoringServiceRunning) {
            startService(new Intent(this, LocationMonitoringService.class));
            Globals.isLocationMonitoringServiceRunning = true;
        }
        Toast.makeText(this, "Location Updates ON", Toast.LENGTH_LONG).show();
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
                sourceAddress = addresses.get(0);
                LatLng source = new LatLng(sourceAddress.getLatitude(), sourceAddress.getLongitude());
                bounds.include(source);
                if (sourceMarker == null) {
                    sourceMarker = mMap.addMarker(new MarkerOptions().position(source).title("You").snippet("This is you"));
                } else {
                    sourceMarker.setPosition(source);
                }
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(source, 16.0f));
                //mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 10));

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
