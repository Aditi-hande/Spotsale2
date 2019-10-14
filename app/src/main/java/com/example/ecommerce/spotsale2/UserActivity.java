package com.example.ecommerce.spotsale2;


import android.app.Activity;
import android.app.IntentService;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class UserActivity extends AppCompatActivity {

    Button btnSignOut;
    FirebaseAuth auth;
    FirebaseUser user;

    static final int PICK_IMAGE = 6969;

    private CustomImageView profileImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        btnSignOut = (Button) findViewById(R.id.sign_out_button);

        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override            public void onClick(View view) {
                auth.signOut();
                FirebaseAuth.AuthStateListener authListener = new FirebaseAuth.AuthStateListener() {
                    @Override
                    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user == null) {
                            startActivity(new Intent(UserActivity.this, LoginActivity.class));
                            finish();
                        }
                    }
                };
            }
        });

        profileImageView = (CustomImageView) findViewById(R.id.profile_picture);
        StorageReference ref = FirebaseStorage.getInstance().getReference()
                .child(auth.getCurrentUser().getUid() + "/profile_pic.jpg");
        ref.getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(profileImageView);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        profileImageView.setImageResource(R.mipmap.ic_launcher_round);
                    }
                });

        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                getIntent.setType("image/*");
                //getIntent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"images/jpeg"});

                Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                //pickIntent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"images/jpeg"});

                Intent chooseIntent = Intent.createChooser(getIntent, "Select Image");
                chooseIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

                startActivityForResult(chooseIntent, PICK_IMAGE);
            }
        });

        findViewById(R.id.change_password_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ForgetAndChangePasswordActivity.class).putExtra("Mode", 1));
            }
        });

        findViewById(R.id.change_email_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ForgetAndChangePasswordActivity.class).putExtra("Mode", 2));
            }
        });

        findViewById(R.id.delete_user_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ForgetAndChangePasswordActivity.class).putExtra("Mode", 3));
            }
        });
    }

    @Override
    protected void onResume() {
        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(UserActivity.this, LoginActivity.class));
            finish();
        }
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode,resultCode,data);



        if(requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            profileImageView.setInProgress(true);
            Uri imgUri = data.getData();
            StorageReference storageRef = FirebaseStorage.getInstance().getReference()
                    .child(auth.getCurrentUser().getUid() + "/profile_pic.jpg");
            UploadTask uploadTask = storageRef.putFile(imgUri);
            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            profileImageView.setInProgress(false);
                            Snackbar.make(profileImageView.getRootView(), "Image uploaded Successfully", Snackbar.LENGTH_LONG).show();
                        }
                    });

            try {
                Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imgUri));
                profileImageView.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

    }
}
