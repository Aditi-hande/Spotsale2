package com.example.ecommerce.spotsale2;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ecommerce.spotsale2.DatabaseClasses.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword,inputUsername,inputAddress;
    private FirebaseAuth auth;
    private Button btnSignUp, btnLogin;

    DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().child("Users");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);



        auth = FirebaseAuth.getInstance();
//if user is signed in then redirect to the main activity.
        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(RegisterActivity.this, UserActivity.class));
            finish();
        }

        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        inputUsername=(EditText)findViewById(R.id.username);
        inputAddress=(EditText)findViewById(R.id.address);
        btnSignUp = (Button) findViewById(R.id.sign_up_button);
        btnLogin = (Button) findViewById(R.id.sign_in_button);


        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = inputEmail.getText().toString();
                final String password = inputPassword.getText().toString();
                final String username = inputUsername.getText().toString();
                final String address = inputAddress.getText().toString();


                try {
                    if (password.length() > 0 && email.length() > 0 && username.length()>0 && address.length()>0 ) {

                        auth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (!task.isSuccessful()) {
                                            Toast.makeText(
                                                    RegisterActivity.this,
                                                    "Authentication Failed",
                                                    Toast.LENGTH_LONG).show();
                                            Log.v("error", task.getResult().toString());
                                        } else {

                                            Users obj=new Users(username,address,email);
                                            databaseReference.child(auth.getUid()).setValue(obj);
                                            Intent intent = new Intent(RegisterActivity.this, UserActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }

                                    }
                                });
                    } else {
                        Toast.makeText(
                                RegisterActivity.this,
                                "Fill All Fields",
                                Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }}