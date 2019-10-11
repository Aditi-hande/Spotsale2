package com.example.ecommerce.spotsale2;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.appyvet.materialrangebar.RangeBar;
import com.example.ecommerce.spotsale2.DatabaseClasses.Cart;
import com.example.ecommerce.spotsale2.DatabaseClasses.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import android.view.View;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class CatalogActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager recyclerLayoutManager;
    private ArrayList<Product> products;
    private ProductAdapter adapter;

    private ProgressDialog PD;

    private Cart activeCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        /*    Initialize Toolbar    */
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //((CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar)).setTitle("Spotsale");

        /*    Initialize Floating Action Button    */
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setUpActiveCartForActivity(new Intent(getApplicationContext(), CartActivity.class)
                        .putExtra("callingActivity", "CatalogActivity"));
            }
        });

        /*    Initialize Drawer Layout    */
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        /*    Initialize Navigation View for Drawer Layout    */
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getHeaderView(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), UserActivity.class));
            }
        });
        ((TextView) navigationView.getHeaderView(0).findViewById(R.id.nav_header_name))
                .setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        ((TextView) navigationView.getHeaderView(0).findViewById(R.id.nav_header_email))
                .setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());

        /*    Initialize Progress Dialog    */
        PD = new ProgressDialog(this);
        PD.setMessage("Loading...");
        PD.setCancelable(true);
        PD.setCanceledOnTouchOutside(false);

        /*    Initialize Recycler View    */
        recyclerView = (RecyclerView) findViewById(R.id.recylcer);
        recyclerLayoutManager = new GridLayoutManager(this, 1);
        products = new ArrayList<Product>();

        PD.show();

        /*    Initialize Search bar    */
        SearchView searchView = (SearchView) findViewById(R.id.search_view);
        searchView.setSearchableInfo(
                ((SearchManager) getSystemService(Context.SEARCH_SERVICE))
                        .getSearchableInfo(getComponentName())
        );
        searchView.setIconifiedByDefault(false);

        /*    Get data from Database to populate Recycler View    */
        FirebaseFirestore.getInstance()
                .collection(getResources().getText(R.string.products).toString())
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
                                    setUpActiveCartForActivity(new Intent(getApplicationContext(), ProductActivity.class)
                                            .putExtra("product", product));
                                }
                            });
                            recyclerView.setHasFixedSize(true);
                            recyclerView.setLayoutManager(recyclerLayoutManager);
                            recyclerView.setAdapter(adapter);

                            PD.dismiss();
                        }
                    }
                });

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_filter) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(R.layout.dialog_filter);
            builder.setPositiveButton("Apply", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(getApplicationContext(), "Apply Filters not yet available", Toast.LENGTH_LONG).show();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(getApplicationContext(), "Cancel Filtering", Toast.LENGTH_SHORT).show();
                }
            });
            final AlertDialog dialog = builder.create();

            ((RangeBar) dialog.findViewById(R.id.range_bar)).setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
                @Override
                public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex, int rightPinIndex, String leftPinValue, String rightPinValue) {
                    ((TextView) dialog.findViewById(R.id.range_start)).setText(leftPinValue);
                    ((TextView) dialog.findViewById(R.id.range_end)).setText(rightPinValue);
                }

                @Override
                public void onTouchStarted(RangeBar rangeBar) {

                }

                @Override
                public void onTouchEnded(RangeBar rangeBar) {

                }
            });

            dialog.show();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_wishlist) {

        } else if (id == R.id.nav_categories) {

        } else if (id == R.id.nav_orders) {

        } else if (id == R.id.nav_Help) {

        } else if (id == R.id.nav_additem) {

        } else if (id == R.id.nav_viewrepo) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setUpActiveCartForActivity(final Intent intent) {

        FirebaseDatabase.getInstance().getReference()
                .child(getResources().getText(R.string.carts).toString())
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .orderByChild("status")
                .equalTo(Cart.Status.ACTIVE.toString())
                .limitToFirst(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChildren()){
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                activeCart = snapshot.getValue(Cart.class);
                                startActivity(intent.putExtra("activeCart", activeCart));
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

}
