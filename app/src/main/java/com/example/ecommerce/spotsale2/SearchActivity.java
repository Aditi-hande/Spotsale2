package com.example.ecommerce.spotsale2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.ecommerce.spotsale2.DatabaseClasses.Product;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager recyclerLayoutManager;
    private ArrayList<Product> products;
    private ProductAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);

            //Snackbar.make(, query, Snackbar.LENGTH_LONG).show();
            Toast.makeText(this, query, Toast.LENGTH_LONG).show();

            recyclerView = (RecyclerView) findViewById(R.id.recylcer);
            recyclerLayoutManager = new GridLayoutManager(this, 1);
            products = new ArrayList<Product>();

            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Searching...");
            progressDialog.setCancelable(true);
            progressDialog.setCanceledOnTouchOutside(false);

            progressDialog.show();

            search(query);

        }
    }

    private void search(String query) {

        FirebaseFirestore.getInstance().collection(getString(R.string.products));
/*
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
            products.add(snapshot.getValue(Product.class));
            adapter = new ProductAdapter(products, new ProductAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(Product product) {
                    startActivity(new Intent(getApplicationContext(), ProductActivity.class).putExtra("product", product));
                }
            });
        }
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(recyclerLayoutManager);
        recyclerView.setAdapter(adapter);
        */

        progressDialog.dismiss();
    }
}
