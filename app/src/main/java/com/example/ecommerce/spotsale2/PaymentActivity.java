package com.example.ecommerce.spotsale2;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;

public class PaymentActivity extends AppCompatActivity {

    final int UPI_PAYMENT = 0;
    EditText upi_id,name_sender,amountinput,noteinput;
    Button sendbtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        initializeViews();
        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amount = amountinput.getText().toString();
               // String upi = upi_id.getText().toString();
                String upi="athang213@oksbi";
                String name = name_sender.getText().toString();
                String note = noteinput.getText().toString();
                payUsingUpi(amount, upi, name, note);
            }
        });
    }
    void initializeViews(){
        sendbtn=findViewById(R.id.paybutton);
        //upi_id=findViewById(R.id.upi_id);

        name_sender=findViewById(R.id.name);
        amountinput=findViewById(R.id.amount);
        noteinput=findViewById(R.id.note);

    }

    void payUsingUpi(String amount,String upi,String name, String note){

        Uri uri=Uri.parse("upi://pay").buildUpon().appendQueryParameter("pa",upi)
                .appendQueryParameter("pn",name)
                .appendQueryParameter("tn",note)
                .appendQueryParameter("am",amount)
                .appendQueryParameter("cu","INR")
                .build();

        Intent upiPayintent=new Intent(Intent.ACTION_VIEW);
        upiPayintent.setData(uri);

        //show dialogue to choose app
        Intent chooser= Intent.createChooser(upiPayintent,"Pay with");

        //to check if intent resolves
        if(null!=chooser.resolveActivity(getPackageManager())){
            startActivityForResult(chooser,UPI_PAYMENT);
        }
        else{
            Toast.makeText(PaymentActivity.this,"No UPI app found!",Toast.LENGTH_SHORT).show();
        }

    }

    protected void onActivityResult(int requestCode,int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);

        switch(requestCode){
            case UPI_PAYMENT:
                if((RESULT_OK==resultCode) || (resultCode==1)){
                    if(data!=null){
                        String trxt= data.getStringExtra("response");
                        Log.d("UPI","onActivityResult:"+trxt);
                        ArrayList<String>datalist=new ArrayList<>();
                        datalist.add(trxt);
                        upiPaymentDataOperation(datalist);
                    }else{
                        Log.d("UPI","onActivityResult:"+"return data is null");
                        ArrayList<String>datalist=new ArrayList<>();
                        datalist.add("nothing");// no transaction
                        upiPaymentDataOperation(datalist);
                    }
                }else{
                    Log.d("UPI","onActivityResult:"+"return data is null");//when back is pressed
                    ArrayList<String>datalist=new ArrayList<>();
                    datalist.add("nothing");// no transaction
                    upiPaymentDataOperation(datalist);

                }
                break;
        }
    }


    private void upiPaymentDataOperation(ArrayList<String> data) {
        if (isConnectionAvailable(PaymentActivity.this)) {
            String str = data.get(0);
            Log.d("UPIPAY", "upiPaymentDataOperation: "+str);
            String paymentCancel = "";
            if(str == null) str = "discard";
            String status = "";
            String approvalRefNo = "";
            String response[] = str.split("&");
            for (int i = 0; i < response.length; i++) {
                String equalStr[] = response[i].split("=");
                if(equalStr.length >= 2) {
                    if (equalStr[0].toLowerCase().equals("Status".toLowerCase())) {
                        status = equalStr[1].toLowerCase();
                    }
                    else if (equalStr[0].toLowerCase().equals("ApprovalRefNo".toLowerCase()) || equalStr[0].toLowerCase().equals("txnRef".toLowerCase())) {
                        approvalRefNo = equalStr[1];
                    }
                }
                else {
                    paymentCancel = "Payment cancelled by user.";
                }
            }

            if (status.equals("success")) {
                //Code to handle successful transaction here.
                Toast.makeText(PaymentActivity.this, "Transaction successful.", Toast.LENGTH_SHORT).show();
                Log.d("UPI", "responseStr: "+approvalRefNo);
            }
            else if("Payment cancelled by user.".equals(paymentCancel)) {
                Toast.makeText(PaymentActivity.this, "Payment cancelled by user.", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(PaymentActivity.this, "Transaction failed.Please try again", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(PaymentActivity.this, "Internet connection is not available. Please check and try again", Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean isConnectionAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()
                    && netInfo.isConnectedOrConnecting()
                    && netInfo.isAvailable()) {
                return true;
            }
        }
        return false;
    }

}
