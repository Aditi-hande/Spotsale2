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

import com.example.ecommerce.spotsale2.DatabaseClasses.Cart;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class OrdersActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager recyclerLayoutManager;
    private ArrayList<Cart> cartlist = new ArrayList<Cart>();

    private OrderAdapter adapter;

    private ProgressDialog PD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        String status = getIntent().getStringExtra("status");

        /*    Initialize Progress Dialog    */
        PD = new ProgressDialog(this);
        PD.setMessage("Loading...");
        PD.setCancelable(true);
        PD.setCanceledOnTouchOutside(false);

        /*    Initialize Recycler View    */
        recyclerView = (RecyclerView) findViewById(R.id.orders_recycler);
        recyclerLayoutManager = new GridLayoutManager(this, 1);

        PD.show();

        /*    Get data from Database to populate Recycler View    */
        FirebaseDatabase.getInstance().getReference().child("Carts").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .orderByChild("status")
                .equalTo(status)
                .addValueEventListener(new ValueEventListener() {
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Cart cart1 = (Cart) snapshot.getValue();
                            cartlist.add(cart1);
                            Log.v("cart", cartlist.toString());

                            adapter = new OrderAdapter(cartlist, new OrderAdapter.OnItemClickListener() {

                                public void onItemClick(Cart cart1) {
                                    //Toast.makeText(SubCategoryActivity.this,subcategory,Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(OrdersActivity.this, OrderDisplayActivity.class).putExtra("cart", cart1);
                                    startActivity(intent);
                                }
                            });
                            recyclerView.setHasFixedSize(false);
                            recyclerView.setLayoutManager(recyclerLayoutManager);
                            recyclerView.setAdapter(adapter);

                            PD.dismiss();
                        }
                        }


                        public void onCancelled(DatabaseError databaseError) {

                            Toast.makeText(OrdersActivity.this,"error",Toast.LENGTH_LONG).show();
                        }
                    });


                }

    }

