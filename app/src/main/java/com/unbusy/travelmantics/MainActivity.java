package com.unbusy.travelmantics;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.VisibilityAwareImageButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static int uiState = 0;

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private TravelDealAdapter travelDealAdapter;
    private WishListAdapter wishListAdapter;
    private Toolbar toolbar;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (FirebaseUtils.isAdmin == true){
//                    fab.setVisibility(View.VISIBLE);
                }

                // if trip list is currently open go to InsertdealActivity else go to trip list
                if (uiState == 0) {
                    Intent intent =
                            new Intent(MainActivity.this, InsertDealActivity.class);
                    startActivity(intent);
                } else {
                    uiState = 1;
//                    displayWishList();
                }

            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().findItem(R.id.nav_trips).setChecked(true);


        initialiseDisplayContent();
    }

    private void initialiseDisplayContent() {
        recyclerView = findViewById(R.id.trip_recyclerview);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        travelDealAdapter = new TravelDealAdapter();
        wishListAdapter = new WishListAdapter();

        displayTravelDeals();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null){
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseUtils.detachListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseUtils.attachListener();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void showMenu(){
        invalidateOptionsMenu();
    }

    private void displayTravelDeals() {
        toolbar.setTitle("Travel deals");
        recyclerView.setAdapter(travelDealAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);

//        Toast.makeText(this, "Travel Deals", Toast.LENGTH_LONG).show();


        selectNavigationMenuItem(R.id.nav_trips);
    }

    private void selectNavigationMenuItem(int p) {
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.getMenu().findItem(p).setChecked(true);
    }

    private void displayWishList() {
        toolbar.setTitle("Wish list");
        recyclerView.setAdapter(wishListAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);

//        Toast.makeText(this, "Wish list", Toast.LENGTH_LONG).show();

        selectNavigationMenuItem(R.id.my_wishlist);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_trips) {
            uiState = 0;
            displayTravelDeals();
        } else if (id == R.id.my_account) {
            Intent intent = new Intent(this, AccountActivity.class);
            startActivity(intent);

        } else if (id == R.id.my_wishlist) {
            uiState = 1;
            displayWishList();
        } else if (id == R.id.logout) {
            selectNavigationMenuItem(R.id.nav_trips);
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
