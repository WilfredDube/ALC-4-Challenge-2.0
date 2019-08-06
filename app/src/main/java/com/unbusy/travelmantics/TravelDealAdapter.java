package com.unbusy.travelmantics;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class TravelDealAdapter extends RecyclerView.Adapter<TravelDealAdapter.TravelDealHolder>{
    public static final String TRAVEL_DEAL = "com.unbusy.travelmantics.TRAVEL_DEAL";
    ArrayList<TravelDeal> travelDeals;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ChildEventListener childEventListener;

    public TravelDealAdapter(){
        FirebaseUtils.openFbReference("traveldeals");
        firebaseDatabase = FirebaseUtils.firebaseDatabase;
        databaseReference = FirebaseUtils.databaseReference;
        travelDeals = FirebaseUtils.travelDeals;
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                TravelDeal td = dataSnapshot.getValue(TravelDeal.class);
                td.setTid(dataSnapshot.getKey().toString());
                travelDeals.add(td);
                notifyItemInserted(travelDeals.size() - 1);
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
        databaseReference.addChildEventListener(childEventListener);
    }

    @NonNull
    @Override
    public TravelDealHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.trip_list_item, viewGroup, false);

        return new TravelDealHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TravelDealHolder travelDealHolder, int i) {
        TravelDeal travelDeal = travelDeals.get(i);
        travelDealHolder.bind(travelDeal);
    }

    @Override
    public int getItemCount() {
        return travelDeals.size();
    }


    public class TravelDealHolder extends RecyclerView.ViewHolder{


        TextView tripName;
        TextView tripCountry;
        TextView tripCost;

        public TravelDealHolder(@NonNull View itemView) {
            super(itemView);
            tripName = itemView.findViewById(R.id.list_trip_name);
            tripCountry = itemView.findViewById(R.id.list_trip_country);
            tripCost = itemView.findViewById(R.id.list_trip_cost);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    TravelDeal travelDeal = travelDeals.get(pos);

                    Intent intent = new Intent(v.getContext(), TripDetailsActivity.class);
                    intent.putExtra(TRAVEL_DEAL, travelDeal);
                    v.getContext().startActivity(intent);
                }
            });
        }

        public void bind(TravelDeal travelDeal){
            tripName.setText(travelDeal.tripName);
            tripCountry.setText(travelDeal.tripCountry);
            tripCost.setText(String.valueOf(travelDeal.tripCost));
        }
    }
}
