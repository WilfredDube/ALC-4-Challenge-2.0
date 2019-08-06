package com.unbusy.travelmantics;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;

public class InsertDealActivity extends AppCompatActivity {
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private ArrayList<String> mCountries;
    TravelDeal deal;

    Spinner tripCountry;
     EditText tripName;
     EditText tripCategory;
     EditText tripDescription;
     EditText tripCost;
     EditText tripDiscount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_deal);
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("traveldeals");

        tripName = findViewById(R.id.insert_trip_name);
        tripCategory = findViewById(R.id.insert_trip_category);
        tripDescription = findViewById(R.id.insert_trip_description);
        tripCost = findViewById(R.id.insert_trip_cost);
        tripDiscount = findViewById(R.id.insert_trip_discount);

        tripCountry = findViewById(R.id.insert_trip_country);
        mCountries = new ArrayList<>();
        mCountries.addAll(Arrays.asList(getResources().getStringArray(R.array.countries)));
        ArrayAdapter<String> countryArrayAdapter =
                new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, mCountries);
        tripCountry.setAdapter(countryArrayAdapter);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        TravelDeal deal = intent.getParcelableExtra(TravelDealAdapter.TRAVEL_DEAL);

        if (deal == null){
            toolbar.setTitle("New travel deal");
            deal = new TravelDeal();
        }

        this.deal = deal;
        tripName.setText(deal.tripName);
        tripCountry.setSelection(mCountries.indexOf(deal.tripCountry));
        tripCost.setText(String.valueOf(deal.tripCost));
        tripCategory.setText(deal.tripCategory);
        tripDescription.setText(deal.tripDescription);
        tripDiscount.setText(String.valueOf(deal.tripCost));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.trip_save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_trip_save) {
            saveTravelDeal();
            Toast.makeText(this, "Saved", Toast.LENGTH_LONG).show();
            clean();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void clean() {
        tripName.setText("");
        tripCategory.setText("");
        tripDescription.setText("");
        tripCost.setText("0");
        tripDiscount.setText("0");
    }

    private void saveTravelDeal() {
        deal.setTripName(tripName.getText().toString());
        deal.setTripCountry(tripCountry.getSelectedItem().toString());
        deal.setTripCategory(tripCategory.getText().toString());
        deal.setTripDescription(tripDescription.getText().toString());
        deal.setTripCost(Long.parseLong(tripCost.getText().toString()));
        deal.setTripDiscount(Long.parseLong(tripDiscount.getText().toString()));

        if (deal.getTid() == null){
            databaseReference.push().setValue(deal);
        } else {
//            deal.setTripDiscount(0);
//            deal.setTripCost(0);
            tripCost.setHint(0);
            tripDiscount.setHint(0);

            databaseReference.child(deal.getTid()).setValue(deal);
        }

    }



}
