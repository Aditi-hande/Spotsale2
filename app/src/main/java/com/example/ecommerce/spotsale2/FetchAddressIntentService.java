package com.example.ecommerce.spotsale2;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FetchAddressIntentService extends IntentService {

    protected ResultReceiver resultReceiver;

    public FetchAddressIntentService() {
        super("FetchAddressIntentService");
    }

    public FetchAddressIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        resultReceiver = intent.getParcelableExtra(Constants.RECEIVER);

        Location location = intent.getParcelableExtra(Constants.LOCATION_DATA_EXTRA);

        ArrayList<Address> addressList = null;
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            if(location == null) {
                addressList = (ArrayList<Address>) geocoder.getFromLocationName("Pune Institute of Computer Technology, Dhankawadi, Pune", 5);
            } else {
                addressList = (ArrayList<Address>) geocoder.getFromLocation(
                        location.getLatitude(),
                        location.getLongitude(),
                        1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(addressList == null || addressList.size() == 0) {
            deliverResultToReceiver(Constants.FAILURE_RESULT, "No Address found");
        } else {

            Log.d("ADDRESSES", "addressList.size() : " + addressList.size());

            deliverResultToReceiver(Constants.SUCCESS_RESULT, addressList);
        }
    }

    private void deliverResultToReceiver(int resultCode, String message) {

        Bundle bundle = new Bundle();
        bundle.putString(Constants.RESULT_DATA_KEY, message);
        resultReceiver.send(resultCode, bundle);
    }

    private void deliverResultToReceiver(int resultCode, ArrayList<Address> messageList) {

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(Constants.RESULT_DATA_KEY, messageList);
        bundle.putInt("COUNT", messageList.size());
        resultReceiver.send(resultCode, bundle);
    }
}
