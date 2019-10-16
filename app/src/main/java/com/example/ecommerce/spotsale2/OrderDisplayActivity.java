package com.example.ecommerce.spotsale2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.TextView;

import com.example.ecommerce.spotsale2.DatabaseClasses.Cart;
import com.example.ecommerce.spotsale2.DatabaseClasses.Product;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class OrderDisplayActivity extends AppCompatActivity {

    private Cart cart;
    List<String> products;
    List<Product> productList;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager recyclerLayoutManager;
    OrderDisplayAdapter adapter;
    TextView cartSumText;

    private ProgressDialog PD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_display);

        productList = new ArrayList<>();
        cartSumText = (TextView) findViewById(R.id.disp_order_cart_sum);
        recyclerView = (RecyclerView) findViewById(R.id.order_display_recycler);
        recyclerLayoutManager = new GridLayoutManager(this, 1);


        cart = (Cart) getIntent().getSerializableExtra("cart");
        products = cart.getProductList();
        if(products == null) {
            products = new ArrayList<>();
        }
        final int[] count = {products.size()};

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading");
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        for (final String product : products) {
            FirebaseFirestore.getInstance().collection("products")
                    .document(product)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            productList.add(documentSnapshot.toObject(Product.class));
                            if(--count[0] == 0){
                                progressDialog.dismiss();
                                adapter =  new OrderDisplayAdapter(productList);
                                recyclerView.setHasFixedSize(false);
                                recyclerView.setLayoutManager(recyclerLayoutManager);
                                recyclerView.setAdapter(adapter);

                                cartSumText.setText("Total: " + cart.getTotal_sum());
                            }
                        }
                    });
        }
    }
}
