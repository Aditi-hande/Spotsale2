package com.example.ecommerce.spotsale2;




import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ecommerce.spotsale2.DatabaseClasses.Cart;
import com.example.ecommerce.spotsale2.DatabaseClasses.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    private FirebaseAuth auth;
    private Button btnSignUp, btnLogin;
    private ProgressDialog PD;


    @Override    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        PD = new ProgressDialog(this);
        PD.setMessage("Loading...");
        PD.setCancelable(true);
        PD.setCanceledOnTouchOutside(false);
        PD.show();
        auth = FirebaseAuth.getInstance();

        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        btnSignUp = (Button) findViewById(R.id.sign_up_button);
        btnLogin = (Button) findViewById(R.id.sign_in_button);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override            public void onClick(View view) {
                final String email = inputEmail.getText().toString();
                final String password = inputPassword.getText().toString();

                if(!PD.isShowing()) {
                    PD.show();
                }

                try {

                    if (password.length() > 0 && email.length() > 0) {
                        auth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (!task.isSuccessful()) {
                                            Toast.makeText(
                                                    LoginActivity.this,
                                                    "Authentication Failed",
                                                    Toast.LENGTH_LONG).show();
                                            Log.v("error", task.getResult().toString());
                                            PD.dismiss();
                                        } else {
                                            FirebaseDatabase.getInstance().getReference()
                                                    .child(getString(R.string.users))
                                                    .child(auth.getCurrentUser().getUid())
                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            Intent intent = new Intent(LoginActivity.this, CatalogActivity.class)
                                                                    .putExtra("user", dataSnapshot.getValue(Users.class))
                                                                    .putExtra("activity", "LoginActivity");
                                                            startActivity(intent);
                                                            finish();
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                                            Toast.makeText(LoginActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        }
                                        PD.dismiss();
                                    }
                                });
                    } else {
                        Toast.makeText(LoginActivity.this,"Fill All Fields", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.forget_password_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ForgetAndChangePasswordActivity.class).putExtra("Mode", 0));
            }
        });

    }

    @Override
    protected void onResume() {
        if (auth.getCurrentUser() != null) {
            FirebaseDatabase.getInstance().getReference()
                    .child(getString(R.string.users))
                    .child(auth.getCurrentUser().getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Intent intent = new Intent(LoginActivity.this, CatalogActivity.class)
                                    .putExtra("user", dataSnapshot.getValue(Users.class))
                                    .putExtra("activity", "LoginActivity");
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(LoginActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            PD.dismiss();
        }
        super.onResume();
    }
}