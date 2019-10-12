package com.example.ecommerce.spotsale2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.ecommerce.spotsale2.DatabaseClasses.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SellerActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager recyclerLayoutManager;
    private ArrayList<Product> products;
    private ProductAdapter adapter;

    private ProgressDialog PD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller);

        /*    Initialize Progress Dialog    */
        PD = new ProgressDialog(this);
        PD.setMessage("Loading...");
        PD.setCancelable(true);
        PD.setCanceledOnTouchOutside(false);

        /*    Initialize Recycler View    */
        recyclerView = (RecyclerView) findViewById(R.id.seller_recycler);
        recyclerLayoutManager = new GridLayoutManager(this, 1);
        String subcategory=getIntent().getStringExtra("subcategory");
        if(products == null) {
            Log.d("APPLY_FAIL", "products is null");
            products = new ArrayList<Product>();
            Log.d("APPLY_FAIL", products.toString());
        }
        PD.show();

        if(products.isEmpty()) {
            FirebaseFirestore.getInstance()
                    .collection(getResources().getText(R.string.products).toString())
                     .whereEqualTo("cat_id",subcategory)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot doc : task.getResult()) {
                                    products.add(doc.toObject(Product.class));
                                }
                                adapter = new ProductAdapter(products, new ProductAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(final Product product) {
                                        Log.v("subcat products ",product.toString());
                                    }
                                });
                                recyclerView.setHasFixedSize(false);
                                recyclerView.setLayoutManager(recyclerLayoutManager);
                                recyclerView.setAdapter(adapter);

                                PD.dismiss();
                            }
                        }
                    });
        }

    }
}
