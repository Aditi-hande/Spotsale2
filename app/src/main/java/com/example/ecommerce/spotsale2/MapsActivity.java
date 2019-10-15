package com.example.ecommerce.spotsale2;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.shapes.Shape;
import android.location.Address;
//import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.ecommerce.spotsale2.DatabaseClasses.Location;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private final int locationRequestCode = 1000;

    private AddressResultReceiver resultReceiver;
    private ArrayList<Address> addresses;

    private Address sourceAddress, destAddress;
    private DocumentReference sourceRef, destRef;
    private PicassoMarker sourceMarker, destMarker;

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
                    startIntentService(documentSnapshot.toObject(Location.class), true);
                }
            }
        });

        destRef = FirebaseFirestore.getInstance().collection("locations")
                .document("ljf6IUFgS6USd3EulemhEFyTWXe2");

        destRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(e != null) {
                    Log.d("SnapshotListener", "Listen Failed");
                }
                if(documentSnapshot != null && documentSnapshot.exists()) {
                    startIntentService(documentSnapshot.toObject(Location.class), false);
                }
            }
        });

        FirebaseStorage.getInstance().getReference()
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid() + "/profile_pic.jpg")
                .getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        if(sourceMarker == null) {
                            sourceMarker = new PicassoMarker(mMap.addMarker(new MarkerOptions().position(new LatLng(0,0)).title("You").snippet("This is you")));
                        }
                        Picasso.get().load(uri)
                                .resize(120, 160)
                                .into(sourceMarker);
                    }
                });

        FirebaseStorage.getInstance().getReference()
                .child("ljf6IUFgS6USd3EulemhEFyTWXe2" + "/profile_pic.jpg")
                .getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        if(destMarker == null) {
                            destMarker = new PicassoMarker(mMap.addMarker(new MarkerOptions().position(new LatLng(0,0)).title("You").snippet("This is you")));
                        }
                        Picasso.get().load(uri)
                                .resize(120,160)
                                .into(destMarker);
                    }
                });

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
    protected void onResume() {
        super.onResume();

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
                        stopService(new Intent(MapsActivity.this, LocationMonitoringService.class));
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

    protected void startIntentService(Location location, boolean isSource) {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, resultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, location);
        intent.putExtra(Constants.DOCUMENT_REFERENCE, isSource);
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
                if(resultData.getBoolean(Constants.DOCUMENT_REFERENCE)) {
                    sourceAddress = addresses.get(0);
                    LatLng source = new LatLng(sourceAddress.getLatitude(), sourceAddress.getLongitude());
                    bounds.include(source);
                    if (sourceMarker == null) {
                        sourceMarker = new PicassoMarker(mMap.addMarker(new MarkerOptions().position(source).title("You").snippet("This is you")));
                    } else {
                        sourceMarker.mMarker.setPosition(source);
                    }
                } else {
                    destAddress = addresses.get(0);
                    LatLng dest = new LatLng(destAddress.getLatitude(), destAddress.getLongitude());
                    bounds.include(dest);
                    if (destMarker == null) {
                        destMarker = new PicassoMarker(mMap.addMarker(new MarkerOptions().position(dest).title("Them").snippet("This is Them")));
                    } else {
                        destMarker.mMarker.setPosition(dest);
                    }
                }

                if(sourceAddress != null && destAddress != null) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 120));
                } else if(sourceAddress != null) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(sourceAddress.getLatitude(), sourceAddress.getLongitude()), 16.0f));
                } else if(destAddress != null) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(destAddress.getLatitude(), destAddress.getLongitude()), 16.0f));
                }

                Log.d("ADDRESSES", "received addreses count:" + resultData.getInt("COUNT"));
            } else {
                Toast.makeText(MapsActivity.this, resultData.getString(Constants.RESULT_DATA_KEY), Toast.LENGTH_LONG).show();
            }

        }

    }

    public static class PicassoMarker implements Target {
        Marker mMarker;

        PicassoMarker(Marker marker) {
            mMarker = marker;
        }

        @Override
        public int hashCode() {
            return mMarker.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if(o instanceof PicassoMarker) {
                Marker marker = ((PicassoMarker) o).mMarker;
                return mMarker.equals(marker);
            } else {
                return false;
            }
        }

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            mMarker.setIcon(BitmapDescriptorFactory.fromBitmap(bitmap));
        }

        @Override
        public void onBitmapFailed(Exception e, Drawable errorDrawable) {
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
