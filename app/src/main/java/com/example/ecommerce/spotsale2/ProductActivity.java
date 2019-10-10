package com.example.ecommerce.spotsale2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ecommerce.spotsale2.DatabaseClasses.Cart;
import com.example.ecommerce.spotsale2.DatabaseClasses.Product;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProductActivity extends AppCompatActivity {

    private Product product;
    private Cart cart;

    static int count = 0;

    ImageView imageView;
    TextView nameText, costText, descText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        Log.d("ProductActivity", "Started" + (++count));

        product = (Product) getIntent().getSerializableExtra("product");
        cart = (Cart) getIntent().getSerializableExtra("activeCart");

        imageView = findViewById(R.id.image);
        nameText = findViewById(R.id.name);
        costText = findViewById(R.id.cost_text);
        descText = findViewById(R.id.description);

        nameText.setText(product.getName());
        costText.setText(String.valueOf(product.getCost()));
        descText.setText(product.getDescription());

        Picasso.get().load(product.getImageUrl()).into(imageView);

        ((Button) findViewById(R.id.add_to_cartbtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                        .child(getResources().getText(R.string.carts).toString())
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(cart.getCart_id());

                if(cart.getProductList() == null){
                    cart.setProductList(new ArrayList<Product>());
                }
                cart.getProductList().add(product);
                cart.setTotal_sum(cart.getTotal_sum() + product.getCost());
                cart.setTotal_items(cart.getTotal_items() + 1);

                ref.setValue(cart);
            }
        });
    }
}
