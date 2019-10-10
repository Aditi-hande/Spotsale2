package com.example.ecommerce.spotsale2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ecommerce.spotsale2.DatabaseClasses.Cart;
import com.example.ecommerce.spotsale2.DatabaseClasses.Product;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    //final Spinner spinner = (Spinner) findViewById(R.id.cart_address);

    private Cart cart;

    List<String> addr_list = new ArrayList<String>();
    RecyclerView recyclerView;

    ArrayList<Product> products;

    RecyclerView.LayoutManager recyclerLayoutManager;
    CartAdapter adapter;

    private ProgressDialog PD;

    DatabaseReference cartRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        cart = (Cart) getIntent().getSerializableExtra("activeCart");

        addr_list.add("Select Address");// this will act as hint for spinner

        //add addresses respective to that uid in addr_list

        PD = new ProgressDialog(this);
        PD.setMessage("Loading...");
        PD.setCancelable(true);
        PD.setCanceledOnTouchOutside(false);

        Log.d("CallingActivity", getIntent().getStringExtra("callingActivity"));


        recyclerView = (RecyclerView) findViewById(R.id.cart_recycler);
        recyclerLayoutManager = new GridLayoutManager(this, 1);
        products = new ArrayList<Product>();

        PD.show();

        cartRef = FirebaseDatabase.getInstance().getReference()
                .child(getResources().getText(R.string.carts).toString())
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(cart.getCart_id());


        cartRef.child("productList")
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    products.add(snapshot.getValue(Product.class));
                    adapter = new CartAdapter(products, new CartAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(Product product, View view) {
                            cart.getProductList().remove(product);
                            cart.setTotal_sum(cart.getTotal_sum() - product.getCost());
                            cart.setTotal_items(cart.getTotal_items() - 1);
                            cartRef.setValue(cart);
                        }
                    });
                }
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(recyclerLayoutManager);
                recyclerView.setAdapter(adapter);

                PD.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("OnCancelled", "Cancelled");
            }
        });

/*
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this, R.layout.support_simple_spinner_dropdown_item, addr_list) {
            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        spinnerArrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);
                // If user change the default selection
                // First item is disable and it is used for hint
                if (position > 0) {
                    // Notify the selected item text
                    Toast.makeText
                            (getApplicationContext(), "Selected : " + selectedItemText, Toast.LENGTH_SHORT)
                            .show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/
    }

}

