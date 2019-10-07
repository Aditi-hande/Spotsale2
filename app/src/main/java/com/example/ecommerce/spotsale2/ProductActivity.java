package com.example.ecommerce.spotsale2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ecommerce.spotsale2.DatabaseClasses.Product;
import com.squareup.picasso.Picasso;

public class ProductActivity extends AppCompatActivity {

    private Product product;

    ImageView imageView;
    TextView nameText, costText, descText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        product = (Product) getIntent().getSerializableExtra("product");
        Log.d("ProductActivity", product.toString());

        imageView = (ImageView) findViewById(R.id.image);
        nameText = (TextView) findViewById(R.id.name);
        costText = (TextView) findViewById(R.id.cost_text);
        descText = (TextView) findViewById(R.id.description);

        nameText.setText(product.getName());
        costText.setText(String.valueOf(product.getCost()));
        descText.setText(product.getDescription());

        Picasso.get().load(product.getImageUrl()).into(imageView);
    }
}
