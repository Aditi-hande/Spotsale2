package com.example.ecommerce.spotsale2;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

    }





    public void login(View view)
    {
        Intent loginintent = new Intent(HomeActivity.this, LoginActivity.class);
        startActivity(loginintent);
    }
    public void register(View view)
    {
        Intent registerintent = new Intent(HomeActivity.this, RegisterActivity.class);
        startActivity(registerintent);
    }

}
