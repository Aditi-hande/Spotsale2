package com.example.ecommerce.spotsale2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;

public class AddressActivity extends AppCompatActivity {

    private final int locationRequestCode = 1000;

    private AddressResultReceiver resultReceiver;
    private ArrayList<Address> addresses;

    private FusedLocationProviderClient fusedLocationClient;

    private Address sourceAddress, destinationAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        resultReceiver = new AddressResultReceiver(new Handler());

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    locationRequestCode);

        } else {
            startLocationService();
        }

        startIntentService(null);

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

    protected void startLocationService() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        Toast.makeText(AddressActivity.this, "Getting Addresses ...", Toast.LENGTH_LONG).show();
                        if(location != null) {
                            startIntentService(location);
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddressActivity.this, "Failed to get Location", Toast.LENGTH_LONG).show();
                    }
                });
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

    class AddressResultReceiver extends ResultReceiver {

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

                for (Address address : addresses) {
                    Log.d("ADDRESSES", "String: " + getAddressString(address));
                    Log.d("ADDRESSES", "Coords: " + address.getLatitude() + "," + address.getLongitude());
                }

                if(sourceAddress == null){
                    sourceAddress = addresses.get(0);
                } else {
                    destinationAddress = addresses.get(0);
                }

                Log.d("ADDRESSES", "received addreses count:" + resultData.getInt("COUNT"));
            } else {
                Toast.makeText(AddressActivity.this, resultData.getString(Constants.RESULT_DATA_KEY), Toast.LENGTH_LONG).show();
            }

        }
    }
/*
    protected void startGoogleMaps() {

        try {
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                    Uri.parse("geo:" + destinationAddress.getLatitude()
                            + "," + destinationAddress.getLongitude()
                            + "?q=" + sourceAddress.getLatitude()
                            + "," + sourceAddress.getLongitude()
                            + "(" + "My Location" + ")"));
            intent.setComponent(new ComponentName(
                    "com.google.android.apps.maps",
                    "com.google.android.maps.MapsActivity"));
            getApplicationContext().startActivity(intent);
        } catch (ActivityNotFoundException e) {

            try {
                getApplicationContext().startActivity(new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=com.google.android.apps.maps")));
            } catch (android.content.ActivityNotFoundException anfe) {
                getApplicationContext().startActivity(new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=com.google.android.apps.maps")));
            }

            e.printStackTrace();
        }
    }
*/
}
