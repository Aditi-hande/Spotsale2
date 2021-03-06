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
import android.widget.TextView;
import android.widget.Toast;


import com.example.ecommerce.spotsale2.DatabaseClasses.Cart;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ecommerce.spotsale2.DatabaseClasses.Users;

public class PaymentActivity extends AppCompatActivity {

    final int UPI_PAYMENT = 0;
    Button sendbtn;

    private Users user;
    private Cart cart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        FirebaseDatabase.getInstance().getReference()
                .child(getResources().getText(R.string.users).toString())
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        user = (Users) dataSnapshot.getValue(Users.class);
                        cart = (Cart) getIntent().getSerializableExtra("cart");

                        ((TextView)findViewById(R.id.name)).setText(user.getUsername());
                        ((TextView)findViewById(R.id.amount)).setText(String.valueOf(cart.getTotal_sum()));
                        ((TextView)findViewById(R.id.note)).setText("Order ID: " + cart.getCart_id());

                        ((Button) findViewById(R.id.paybutton)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String amount = String.valueOf(cart.getTotal_sum()%50);
                                String upi="athang213@oksbi";
                                String name = user.getUsername();
                                String note = "Order ID: " + cart.getCart_id();
                                payUsingUpi(amount, upi, name, note);
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

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
