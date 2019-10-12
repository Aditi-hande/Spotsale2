package com.example.ecommerce.spotsale2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.view.View;
import android.widget.Toast;

public class AddressActivity extends AppCompatActivity {

    private AddressResultReceiver resultReceiver;

    private String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        resultReceiver = new AddressResultReceiver(new Handler());
        startIntentService();

    }

    protected void startIntentService() {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, resultReceiver);
        startService(intent);
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

            address = resultData.getString(Constants.RESULT_DATA_KEY);
            if(address == null) {
                address = "";
            }
            Toast.makeText(AddressActivity.this, "Address: " + address, Toast.LENGTH_SHORT).show();
        }
    }
}
