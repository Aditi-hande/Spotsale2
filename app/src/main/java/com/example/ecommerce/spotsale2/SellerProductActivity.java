package com.example.ecommerce.spotsale2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ecommerce.spotsale2.DatabaseClasses.Inventory;
import com.example.ecommerce.spotsale2.DatabaseClasses.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SellerProductActivity extends AppCompatActivity {
    private Product product;
    private Inventory inventory;

    ImageView imageView;
    TextView nameText, costText,descText;
    EditText qtyView,sellingcostView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_product);

        product = (Product) getIntent().getSerializableExtra("product");
        inventory=(Inventory)getIntent().getSerializableExtra("inventory") ;

        final int mrp = product.getCost();

        imageView = findViewById(R.id.image);
        nameText = findViewById(R.id.name);
        costText = findViewById(R.id.cost_text);
        descText = findViewById(R.id.description);
        qtyView=findViewById(R.id.sell_qty);
        sellingcostView=findViewById(R.id.sell_cost);

        nameText.setText(product.getName());
        costText.setText(String.valueOf(product.getCost()));
        descText.setText(product.getDescription());

        Picasso.get().load(product.getImageUrl()).into(imageView);

        final CollectionReference db = FirebaseFirestore.getInstance()
                .collection(getResources().getText(R.string.products).toString());

        ((Button) findViewById(R.id.add_to_inventorybtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                    .child("Inventories".toString())
                    .child(inventory.getInventory_id());

                final int qty = Integer.parseInt( qtyView.getText().toString());
                final int sellingcost= Integer.parseInt(sellingcostView.getText().toString());


                if(mrp >= sellingcost) {
                    if (inventory.getProductList() == null) {
                        inventory.setProductList(new ArrayList<Product>());
                    }
                    product.setCost(sellingcost);
                    Log.d("INVENTORY", "getProductList: " + inventory.getProductList().toString());
                    inventory.getProductList().add(product);
                    Log.d("INVENTORY", "getProductList: " + inventory.getProductList().toString());
                    inventory.setTotal_items(inventory.getTotal_items() + qty);

                    ref.setValue(inventory);

                    /*    Decrease product qty from stocks    */
                    //product.setQty(product.getQty() - 1);
                    //db.document(product.getItem_id()).set(product);

                    Snackbar.make(v, "Added to Inventory", Snackbar.LENGTH_LONG).show();
                } else{
                    Snackbar.make(v, "Cost should be less than MRP !", Snackbar.LENGTH_LONG).show();
                }

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        final int[] count = {2};

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(true);
        progressDialog.show();

        FirebaseDatabase.getInstance().getReference()
                .child("Inventories")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            inventory = dataSnapshot.getValue(Inventory.class);
                        } else {
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                                    .child("Inventories".toString())
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                            inventory = new Inventory();
                            inventory.setInventory_id(FirebaseAuth.getInstance().getCurrentUser().getUid());
                            ref.setValue(inventory);
                        }
                        if(--count[0] == 0) {
                            progressDialog.dismiss();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        FirebaseFirestore.getInstance()
                .collection(getResources().getText(R.string.products).toString())
                .document(product.getItem_id())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            product = task.getResult().toObject(Product.class);
                            if(--count[0] == 0) {
                                progressDialog.dismiss();
                            }
                        }
                    }
                });

    }
}
