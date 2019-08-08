package com.unbusy.travelmantics;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AlertDialog;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
//import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class TripDetailsActivity extends AppCompatActivity {
    public static final String EDIT_DEAL = "com.unbusy.travelmantics.EDIT_DEAL";
    ImageView tripImage;
    TextView tripCountry;
    TextView tripName;
    TextView tripDescription;
    TextView tripCost;
    TextView tripDiscount;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ArrayList<TravelDeal> travelDeals;
    private TravelDeal deal;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FirebaseUtils.openFbReference("traveldeals");
        firebaseDatabase = FirebaseUtils.firebaseDatabase;
        databaseReference = FirebaseUtils.databaseReference
                .child(FirebaseUtils.userAccount.getUserId());

        tripCountry = findViewById(R.id.detail_trip_country);
        tripDescription = findViewById(R.id.detail_trip_description);
        tripName = findViewById(R.id.detail_trip_name);
        tripCost = findViewById(R.id.detail_trip_cost);
        tripDiscount = findViewById(R.id.detail_trip_discount);
        tripImage = findViewById(R.id.detail_trip_image);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        deal = intent.getParcelableExtra(TravelDealAdapter.TRAVEL_DEAL);

        tripName.setText(deal.tripName);
        getSupportActionBar().setTitle(deal.tripName);
        tripCountry.setText(deal.tripCountry);

        String cost = "$" + deal.tripCost;
        tripCost.setText(cost);

        tripDescription.setText(deal.tripDescription);
        if (deal.tripDiscount != 0) {
            String discountConcat = deal.tripDiscount + " off";
            tripDiscount.setText(discountConcat);
        }


        showImage(deal.getTripImageUrl());
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void showImage(String url) {

        Log.d("IMAGE DETAILS", " " + url);
        if (url != null && !url.isEmpty()) {
            int width = Resources.getSystem().getDisplayMetrics().widthPixels;
            Picasso.get()
                    .load(url)
                    .resize(width, width / 2)
                    .centerCrop()
                    .into(tripImage);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu optionMenu) {
        this.menu = optionMenu;
        getMenuInflater().inflate(R.menu.trip_delete, menu);

        if (FirebaseUtils.isAdmin){
            menu.findItem(R.id.action_trip_delete).setVisible(true);
            menu.findItem(R.id.action_favourate).setVisible(false);
        } else {
            menu.findItem(R.id.action_trip_delete).setVisible(false);
            menu.findItem(R.id.action_favourate).setVisible(true);
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
            return true;
        }

        if (id == R.id.action_favourate) {
            // TODO : add code to set trip as favourite

            if (!deal.isFavourate()) {
                if (deal.getTid() != null) {
                    showAddToFavourite();
                }
            } else {
                showRemoveFromFavourite();
            }

            return true;
        }

        if (id == R.id.action_trip_edit) {
            Intent intent =
                    new Intent(this, InsertDealActivity.class);
            if (deal != null) {
                Log.d("DEALL", "ITS NULL" + deal.tripName);
            }
            intent.putExtra(EDIT_DEAL, deal);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showRemoveFromFavourite() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(deal.tripName);
        alertDialog.setMessage("Remove travel deal from your wish list?");

        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                deal.setFavourate(true);
                databaseReference.push().setValue(deal);
                Toast.makeText(TripDetailsActivity.this, "Removed from wishlist!",
                        Toast.LENGTH_SHORT).show();
                menu.findItem(R.id.action_favourate)
                        .setIcon(R.drawable.ic_favorite_border_black_24dp);
            }
        });

        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        alertDialog.show();
    }

    private void showAddToFavourite() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(deal.tripName);
        alertDialog.setMessage("Add travel deal to your wish list?");

        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                deal.setFavourate(true);
                databaseReference.push().setValue(deal);
                Toast.makeText(TripDetailsActivity.this,
                        "Successfully added to wishlist.", Toast.LENGTH_SHORT).show();
                menu.findItem(R.id.action_favourate).setIcon(R.drawable.ic_favorite_orange);
            }
        });

        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        alertDialog.show();
    }

    private void deleteDeal(){
//        Toast.makeText(this, "ID : " + deal.getTid(),
//                    Toast.LENGTH_LONG).show();

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Delete " + deal.tripName);
        alertDialog.setMessage("Do you want to delete this travel deal?");

        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                DatabaseReference ref = FirebaseDatabase.getInstance()
                        .getReference().child("traveldeals");
                ref.child(deal.getTid()).removeValue();
                backToList();
                Log.d("DELETE", "SCAAXAXA " + deal.getTid());
            }
        });

        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
//                backToList();
            }
        });

        alertDialog.show();


    }

    private void backToList(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}
