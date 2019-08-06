package com.unbusy.travelmantics;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class TripDetailsActivity extends AppCompatActivity {


    TextView tripCountry;
    TextView tripName;
    TextView tripDescription;
    TextView tripCost;
    TextView tripDiscount;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ArrayList<TravelDeal> travelDeals;
    private TravelDeal deal;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FirebaseUtils.openFbReference("wishlist");
        firebaseDatabase = FirebaseUtils.firebaseDatabase;
        databaseReference = FirebaseUtils.databaseReference;

        fab = findViewById(R.id.fab);
        tripCountry = findViewById(R.id.detail_trip_country);
        tripDescription = findViewById(R.id.detail_trip_description);
        tripName = findViewById(R.id.detail_trip_name);
        tripCost = findViewById(R.id.detail_trip_cost);
        tripDiscount = findViewById(R.id.detail_trip_discount);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        deal = intent.getParcelableExtra(TravelDealAdapter.TRAVEL_DEAL);

        tripName.setText(deal.tripName);
        toolbar.setTitle(deal.tripName);
        tripCountry.setText(deal.tripCountry);
        tripCost.setText(String.valueOf(deal.tripCost));
        tripDescription.setText(deal.tripDescription);
        tripDiscount.setText(String.valueOf(deal.tripDiscount));

        if (deal.isFavourate()){
            fab.hide();
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!deal.isFavourate()) {
                    if (deal.getTid() != null) {
                        deal.setFavourate(true);
                        databaseReference.push().setValue(deal);
                        Toast.makeText(TripDetailsActivity.this, "Value :" + deal.isFavourate(), Toast.LENGTH_SHORT).show();
                    }

//                    Intent intent =
//                            new Intent(TripDetailsActivity.this, MainActivity.class);
//
//                    startActivity(intent);
                } else {
                    fab.hide();

                    Toast.makeText(TripDetailsActivity.this, "Already in your favourates!", Toast.LENGTH_SHORT).show();
                }

                fab.hide();
            }
        });

        if(FirebaseUtils.isAdmin){
            fab.hide();
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        if(deal.isFavourate()){
            fab.hide();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(deal.isFavourate()){
            fab.hide();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.trip_delete, menu);
        if (FirebaseUtils.isAdmin){
            menu.findItem(R.id.action_trip_delete).setVisible(true);
        } else {
            menu.findItem(R.id.action_trip_delete).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_trip_delete) {
            deleteDeal();
            backToList();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void deleteDeal(){
        if(deal == null){
            Toast.makeText(this, "Save the deal before deleating!",
                    Toast.LENGTH_LONG).show();
            return;
        }

        databaseReference.child(deal.getTid()).removeValue();
    }

    private void backToList(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}
