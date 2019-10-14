package com.example.ecommerce.spotsale2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ecommerce.spotsale2.DatabaseClasses.Inventory;
import com.example.ecommerce.spotsale2.DatabaseClasses.Product;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
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
            public void onClick(View v) {DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                    .child("Inventories".toString())
                    .child(inventory.getInventory_id());

                final int qty = Integer.parseInt( qtyView.getText().toString());
                final int sellingcost= Integer.parseInt(sellingcostView.getText().toString());


                if(product.getCost()>sellingcost){
                if(inventory.getProductList() == null){
                    inventory.setProductList(new ArrayList<Product>());
                }
                product.setCost(sellingcost);
                inventory.getProductList().add(product);
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
}
