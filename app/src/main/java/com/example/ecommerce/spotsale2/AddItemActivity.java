package com.example.ecommerce.spotsale2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ecommerce.spotsale2.DatabaseClasses.Inventory;
import com.example.ecommerce.spotsale2.DatabaseClasses.Product;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AddItemActivity extends AppCompatActivity {

    private Product product;
    private String subcategory,category;
    private List<Product.SellerDesc> sellers;
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
         subcategory= getIntent().getStringExtra("subcategory");
         category= getIntent().getStringExtra("category");

        //Toast.makeText(AddItemActivity.this,"in add item:"+subcategory,Toast.LENGTH_LONG).show();

    }

    public void addItem(View view){

        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Inventories")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        Inventory inventory = dataSnapshot.getValue(Inventory.class);

                        if(inventory == null) {
                            inventory = new Inventory(FirebaseAuth.getInstance().getCurrentUser().getUid(), 0,
                                    new ArrayList<Inventory.ProductRef>());
                        }

                        String name = ((EditText)findViewById(R.id.name)).getText().toString();
                        String brand = ((EditText)findViewById(R.id.brand)).getText().toString();
                        String description = ((EditText)findViewById(R.id.description)).getText().toString();
                        String imgurl = ((EditText)findViewById(R.id.image_url)).getText().toString();
                        int cost = Integer.parseInt(((EditText)findViewById(R.id.cost)).getText().toString());
                        int qty = Integer.parseInt(((EditText)findViewById(R.id.qty)).getText().toString());


                        Product.SellerDesc sellerDesc = new Product.SellerDesc(FirebaseAuth.getInstance().getCurrentUser().getUid(),cost,0);
                        sellers.add(sellerDesc);
                        product.setCat_id(subcategory);
                        product.setName(name);
                        product.setSellers(sellers);
                        product.setCost(cost);
                        product.setBrand(brand);
                        product.setDescription(description);
                        product.setImageUrl(imgurl);
                        product.setQty(qty);

                        inventory.getProductList().add(new Inventory.ProductRef(product.getItem_id(), 0, cost, qty));
                        inventory.setTotal_items(inventory.getTotal_items() + qty);

                        ref.setValue(inventory);

                        //not sure maybe redundant
                        db.collection("products").add(product);
                        if(db.collection("brands").whereEqualTo("brand", brand).get().getResult().isEmpty()){
                            db.collection("brands").add(brand);
                        }
                        Toast.makeText(getApplicationContext(), "Inserted Successfully", Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

}
