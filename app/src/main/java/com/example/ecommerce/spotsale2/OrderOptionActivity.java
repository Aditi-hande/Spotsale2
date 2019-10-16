package com.example.ecommerce.spotsale2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import static com.example.ecommerce.spotsale2.DatabaseClasses.Cart.Status.ACTIVE;
import static com.example.ecommerce.spotsale2.DatabaseClasses.Cart.Status.DELIVERED;
import static com.example.ecommerce.spotsale2.DatabaseClasses.Cart.Status.PENDING;

public class OrderOptionActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_option);
    }

   
    public void pending(View view){
        startActivity(new Intent(getApplicationContext(),OrdersActivity.class).putExtra("status",PENDING.toString()));
    }

    public void delivered(View view){
        startActivity(new Intent(getApplicationContext(),OrdersActivity.class).putExtra("status",DELIVERED.toString()));
    }
}
