package com.example.ecommerce.spotsale2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.ecommerce.spotsale2.DatabaseClasses.Inventory;
import com.example.ecommerce.spotsale2.DatabaseClasses.Product;

public class AddItemActivity extends AppCompatActivity {

    private Product product;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        String subcategory= getIntent().getStringExtra("subcategory");
        Toast.makeText(AddItemActivity.this,"in add item:"+subcategory,Toast.LENGTH_LONG).show();
    }
}
