package com.example.ecommerce.spotsale2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CategoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager recyclerLayoutManager;
    private ArrayList<String> categorieslist = new ArrayList<String>();

    private CategoryAdapter adapter;

    private ProgressDialog PD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        /*    Initialize Progress Dialog    */
        PD = new ProgressDialog(this);
        PD.setMessage("Loading...");
        PD.setCancelable(true);
        PD.setCanceledOnTouchOutside(false);

        /*    Initialize Recycler View    */
        recyclerView = (RecyclerView) findViewById(R.id.category_recycler);
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
                                //Toast.makeText(CategoryActivity.this,category,Toast.LENGTH_LONG).show();
                                Intent intent=new Intent(CategoryActivity.this,SubCategoryActivity.class).putExtra("category",category);
                                startActivity(intent);
                            }
                        });
                        recyclerView.setHasFixedSize(false);
                        recyclerView.setLayoutManager(recyclerLayoutManager);
                        recyclerView.setAdapter(adapter);

                        PD.dismiss();

                    }

                    public void onCancelled( DatabaseError databaseError ) {

                        Toast.makeText(CategoryActivity.this,"error",Toast.LENGTH_LONG).show();
                    }
                });

    }


}
