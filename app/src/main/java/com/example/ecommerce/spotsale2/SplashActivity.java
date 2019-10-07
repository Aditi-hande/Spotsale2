package com.example.ecommerce.spotsale2;

import android.content.Intent;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;


public class SplashActivity extends AppCompatActivity {
    private static int splash_timeout = 4000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ImageView image = (ImageView)findViewById(R.id.imageView);
        Animation animation1 =
                AnimationUtils.loadAnimation(getApplicationContext(),
                        R.anim.splash);
        image.startAnimation(animation1);

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run(){
                Intent homeintent=new Intent(SplashActivity.this,HomeActivity.class);
                startActivity(homeintent);
                finish();
            }
        },splash_timeout);
    }

}
