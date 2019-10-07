package com.example.ecommerce.spotsale2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.example.ecommerce.spotsale2.DatabaseClasses.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class CatalogActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    ArrayList<Product> products;

    RecyclerView.LayoutManager recyclerLayoutManager;
    CustomAdapter adapter;

    private ProgressDialog PD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        PD = new ProgressDialog(this);
        PD.setMessage("Loading...");
        PD.setCancelable(true);
        PD.setCanceledOnTouchOutside(false);

        recyclerView = (RecyclerView) findViewById(R.id.recylcer);
        recyclerLayoutManager = new GridLayoutManager(this, 1);
        products = new ArrayList<Product>();

        PD.show();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Products");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    products.add(snapshot.getValue(Product.class));
                    adapter = new CustomAdapter(products, new CustomAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(Product product) {
                            startActivity(new Intent(getApplicationContext(), ProductActivity.class).putExtra("product", product));
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

            }
        });

    }

}
