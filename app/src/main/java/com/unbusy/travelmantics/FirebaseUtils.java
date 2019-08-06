package com.unbusy.travelmantics;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class FirebaseUtils {
    public static FirebaseDatabase firebaseDatabase;
    public static DatabaseReference databaseReference;
    public static FirebaseAuth firebaseAuth;
    public static FirebaseAuth.AuthStateListener authStateListener;
    public static ArrayList<TravelDeal> travelDeals;
    public static FirebaseUtils firebaseUtils;
    private static boolean isAdmin;

    private FirebaseUtils(){}

    public static void openFbReference(String ref){
        if(firebaseUtils == null){
            firebaseUtils = new FirebaseUtils();
            firebaseDatabase = FirebaseDatabase.getInstance();
            firebaseAuth = FirebaseAuth.getInstance();
            authStateListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    if (firebaseAuth.getCurrentUser() == null){
                        FirebaseAuth.getInstance().signOut();
                    }else{
                        String userId = firebaseAuth.getUid();
                        checkAdmin(userId);
                    }
                }
            };
        }

        travelDeals = new ArrayList<TravelDeal>();
        databaseReference = firebaseDatabase.getReference().child(ref);
    }

    private static void checkAdmin(String userId) {
        FirebaseUtils.isAdmin = false;
        DatabaseReference ref = firebaseDatabase.getReference().child("administrator")
                .child(userId);
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                FirebaseUtils.isAdmin = true;
                Log.d("Admin", "You are the administrator.");
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        ref.addChildEventListener(childEventListener);
    }

    private static void goToLogin(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);

    }

    public static void attachListener(){
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    public static void detachListener(){
        firebaseAuth.removeAuthStateListener(authStateListener);
    }
}
