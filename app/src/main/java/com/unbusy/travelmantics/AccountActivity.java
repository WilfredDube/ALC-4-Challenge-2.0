package com.unbusy.travelmantics;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

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
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountActivity extends AppCompatActivity {
    private ArrayList<String> mCountries;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    private static final int REQUEST_CODE = 22;

    private CircleImageView profileImageView;
    EditText userFirstName;
    EditText userLastname;
    EditText userCity;
    EditText userPhoneNumber;
    EditText userEmailAddress;
    Spinner userCountry;

    User user;

    private Uri resultUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Account Settings");

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("users");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userFirstName = findViewById(R.id.account_firstname);
        userLastname = findViewById(R.id.account_lastname);
        userCountry = findViewById(R.id.account_country);
        userCity = findViewById(R.id.account_city);
        userPhoneNumber = findViewById(R.id.account_phone_number);
        userEmailAddress = findViewById(R.id.account_email);

        userEmailAddress.setText(FirebaseUtils.userAccount.getUserEmailAddress());

        profileImageView = findViewById(R.id.account_profile_image);

        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(AccountActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(AccountActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    } else {
                        CropImage.activity()
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .setAspectRatio(1, 1)
                                .start(AccountActivity.this);
                    }
                }
            }
        });

        mCountries = new ArrayList<>();
        mCountries.addAll(Arrays.asList(getResources().getStringArray(R.array.countries)));
        ArrayAdapter<String> countryArrayAdapter =
                new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, mCountries);
        userCountry.setAdapter(countryArrayAdapter);

        if (FirebaseUtils.userAccount.getUserFirstName() != null) {
            initialiseUI();
        }

    }

    private void initialiseUI() {
        userFirstName.setText(user.getUserFirstName());
        userLastname.setText(user.getUserLastName());
        userCountry.setSelection(mCountries.indexOf(user.getUserCountry()));
        userCity.setText(user.getUserCity());
        userPhoneNumber.setText(user.getUserPhoneNumber());
        userEmailAddress.setText(user.getUserEmailAddress());
        showImage(FirebaseUtils.userAccount.getUserProfileImage().toString());
    }

    private void showImage(String url) {

        Log.d("IMAGE PROFILE", " " + url);
        if (url != null && !url.isEmpty()) {
            int width = Resources.getSystem().getDisplayMetrics().widthPixels;
            Picasso.get()
                    .load(url)
                    .resize(width, width / 2)
                    .centerCrop()
                    .into(profileImageView);
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
//                    profileImageView.setImageURI(resultUri);
                    final StorageReference storageReference =
                            FirebaseUtils.storageReference.child("profile_pictures")
                                    .child(FirebaseAuth.getInstance().getUid())
                                    .child(resultUri.getLastPathSegment());
                    storageReference.putFile(resultUri).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    user.setUserProfileImage(uri);
                                    showImage(uri.toString());
                                    Log.d("TAG", "profile pic OK : " + uri.toString());
                                }
                            });
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
        getMenuInflater().inflate(R.menu.account_save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_account_save) {
            saveAccount();
            Toast.makeText(this, "Saved", Toast.LENGTH_LONG).show();
            clean();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void clean() {
    }

    private void saveAccount() {
//        Log.d("TAG", "picture OK" + FirebaseUtils.userAccount
//                .getUserProfileImage().toString());
//        FirebaseUtils.userAccount.setUserFirstName(userFirstName.getText().toString());
//        FirebaseUtils.userAccount.setUserLastName(userLastname.getText().toString());
//        FirebaseUtils.userAccount.setUserCountry(userCountry.getSelectedItem().toString());
//        FirebaseUtils.userAccount.setUserCity(userCity.getText().toString());
//        FirebaseUtils.userAccount.setUserPhoneNumber(userPhoneNumber.getText().toString());
//        FirebaseUtils.userAccount.setUserEmailAddress(userEmailAddress.getText().toString());


        user.setUserFirstName(userFirstName.getText().toString());
        user.setUserLastName(userLastname.getText().toString());
        user.setUserCountry(userCountry.getSelectedItem().toString());
        user.setUserCity(userCity.getText().toString());
        user.setUserPhoneNumber(userPhoneNumber.getText().toString());
        user.setUserEmailAddress(userEmailAddress.getText().toString());

        if (user.getUserId() == null) {
            databaseReference.push().setValue(user);
            FirebaseUtils.setUserAccount(user);
        } else {
            databaseReference.child(user.getUserId()).setValue(user);
        }
//        databaseReference.child(FirebaseUtils.userAccount.getUserId()).push()
//                .setValue(FirebaseUtils.userAccount);
    }
}