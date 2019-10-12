package com.example.ecommerce.spotsale2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.FragmentTransaction;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.example.ecommerce.spotsale2.DatabaseClasses.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Locale;

public class SellerActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager recyclerLayoutManager;
    private ArrayList<String> categorieslist = new ArrayList<String>();

    private CategoryAdapter adapter;

    private ProgressDialog PD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller);

        /*    Initialize Progress Dialog    */
        PD = new ProgressDialog(this);
        PD.setMessage("Loading...");
        PD.setCancelable(true);
        PD.setCanceledOnTouchOutside(false);

        /*    Initialize Recycler View    */
        recyclerView = (RecyclerView) findViewById(R.id.seller_recycler);
        recyclerLayoutManager = new GridLayoutManager(this, 1);

        PD.show();

        /*    Get data from Database to populate Recycler View    */
        FirebaseDatabase.getInstance().getReference().child("Categories")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String cat = (String) snapshot.getKey();
                            categorieslist.add(cat);
                            Log.v("category",categorieslist.toString());
                        }

                        adapter = new CategoryAdapter(categorieslist, new CategoryAdapter.OnItemClickListener() {

                            public void onItemClick(String category) {
                                Toast.makeText(SellerActivity.this,category,Toast.LENGTH_LONG);
                            }
                        });
                        recyclerView.setHasFixedSize(false);
                        recyclerView.setLayoutManager(recyclerLayoutManager);
                        recyclerView.setAdapter(adapter);

                        PD.dismiss();

                    }

                    public void onCancelled( DatabaseError databaseError ) {

                        Toast.makeText(SellerActivity.this,"error",Toast.LENGTH_LONG).show();
                    }
                });

    }


}
