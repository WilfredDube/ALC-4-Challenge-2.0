package com.unbusy.travelmantics;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
//import com.squareup.picasso.Picasso;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.Arrays;

public class InsertDealActivity extends AppCompatActivity {
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private boolean isNewDeal;

    private ArrayList<String> mCountries, mDiscounts;
    TravelDeal deal;

    Spinner tripCountry;
    EditText tripName;
    EditText tripCategory;
    EditText tripDescription;
    EditText tripCost;
    Spinner tripDiscount;
    TextView insertimageMessage;
    ImageView tripImage;

    Uri resultUri;


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
        tripImage = findViewById(R.id.insert_trip_image);
        insertimageMessage = findViewById(R.id.insert_image_message);

        tripImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(InsertDealActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(InsertDealActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    } else {
                        CropImage.activity()
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .setAspectRatio(1, 1)
                                .start(InsertDealActivity.this);
                    }
                }
            }
        });

        tripCountry = findViewById(R.id.insert_trip_country);
        mCountries = new ArrayList<>();
        mCountries.addAll(Arrays.asList(getResources().getStringArray(R.array.countries)));
        ArrayAdapter<String> countryArrayAdapter =
                new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, mCountries);
        tripCountry.setAdapter(countryArrayAdapter);

//        tripDiscount = findViewById(R.id.insert_trip_discount);
        mDiscounts = new ArrayList<>();
        mDiscounts.addAll(Arrays.asList(getResources().getStringArray(R.array.discounts)));
        ArrayAdapter<String> discountArrayAdapter =
                new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item,
                        mDiscounts);
        tripDiscount.setAdapter(discountArrayAdapter);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initializeDisplayStates();


//        if (deal == null){
//            getSupportActionBar().setTitle("New travel deal");
//            deal = new TravelDeal();
//        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent =
                new Intent(this, TripDetailsActivity.class);
        if (deal != null) {
            Log.d("DEALL", "ITS NULL" + deal.tripName);
        }
        intent.putExtra(TravelDealAdapter.TRAVEL_DEAL, deal);
        startActivity(intent);
    }

    private void initializeDisplayStates() {
        Intent intent = getIntent();
        TravelDeal dealIntent = intent.getParcelableExtra(TravelDealAdapter.EDIT_DEAL);

        if (dealIntent != null) {
            displayTravelDeal(dealIntent);
        } else {
            getSupportActionBar().setTitle("New travel deal");
            dealIntent = new TravelDeal();
            deal = dealIntent;
        }
    }

    private void displayTravelDeal(TravelDeal dealIntent) {
        deal = dealIntent;
        tripName.setText(deal.tripName);
        tripCountry.setSelection(mCountries.indexOf(deal.tripCountry));
        tripCost.setText(String.valueOf(deal.tripCost));
        tripCategory.setText(deal.tripCategory);
        tripDescription.setText(deal.tripDescription);
        tripDiscount.setSelection(mDiscounts.indexOf(deal.tripDiscount));

        getSupportActionBar().setTitle(deal.tripName);

        showImage(deal.getTripImageUrl());
    }

    private void showImage(String url) {

        if (url != null && !url.isEmpty()) {
            Log.d("IMAGE INSERT", " " + url);
            Picasso.get()
                    .load(url)
                    .resize(150, 150)
                    .centerCrop()
                    .into(tripImage);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                try {
                    resultUri = result.getUri();
                    tripImage.setImageURI(resultUri);
                    final StorageReference storageReference =
                            FirebaseUtils.storageReference
                                    .child(resultUri.getLastPathSegment());
                    storageReference.putFile(resultUri).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    deal.setTripImageUrl(uri.toString());
                                    insertimageMessage.setText("");
                                    Log.d("TAG", "picture OK : " + uri.toString());
                                }
                            });
//                            String url = taskSnapshot.getUploadSessionUri().toString();

                        }
                    });


                } catch (Exception ex) {
                    Log.d("TAG", "AccountActy : " + ex.getMessage());
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
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
        tripCost.setText("");
//        tripDiscount.setText("");
    }

    private void saveTravelDeal() {
        deal.setTripName(tripName.getText().toString());
        deal.setTripCountry(tripCountry.getSelectedItem().toString());
        deal.setTripCategory(tripCategory.getText().toString());
        deal.setTripDescription(tripDescription.getText().toString());
        deal.setTripCost(Long.parseLong(tripCost.getText().toString()));
        deal.setTripDiscount(Long.parseLong(tripDiscount.getSelectedItem().toString()));

        if (deal.getTid() == null) {
            databaseReference.push().setValue(deal);
        } else {
            databaseReference.child(deal.getTid()).setValue(deal);
        }

    }


}
