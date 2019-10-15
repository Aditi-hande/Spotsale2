package com.example.ecommerce.spotsale2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
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

    public void addItem(View view){
        String name = ((EditText)findViewById(R.id.name)).getText().toString();
        String brand = ((EditText)findViewById(R.id.brand)).getText().toString();
        String description = ((EditText)findViewById(R.id.description)).getText().toString();
        String imgurl= ((EditText)findViewById(R.id.image_url)).getText().toString();
        int cost = Integer.parseInt(((EditText)findViewById(R.id.cost)).getText().toString());
        int qty = Integer.parseInt(((EditText)findViewById(R.id.qty)).getText().toString());

        //product = new Product(name, brand, description, )

    }

}
