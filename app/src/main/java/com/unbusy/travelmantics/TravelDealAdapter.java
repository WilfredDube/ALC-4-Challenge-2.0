package com.unbusy.travelmantics;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
//import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class TravelDealAdapter extends RecyclerView.Adapter<TravelDealAdapter.TravelDealHolder>
        implements Filterable {
    public static final String TRAVEL_DEAL = "com.unbusy.travelmantics.TRAVEL_DEAL";
    public static final String EDIT_DEAL = "com.unbusy.travelmantics.EDIT_DEAL";
    ArrayList<TravelDeal> travelDeals;
    ArrayList<TravelDeal> travelDealsCopy;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ChildEventListener childEventListener;

    public TravelDealAdapter(){
        FirebaseUtils.openFbReference("traveldeals");
        firebaseDatabase = FirebaseUtils.firebaseDatabase;
        databaseReference = FirebaseUtils.databaseReference;
        travelDeals = FirebaseUtils.travelDeals;

        travelDealsCopy = new ArrayList<>(travelDeals);
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                TravelDeal td = dataSnapshot.getValue(TravelDeal.class);
                td.setTid(dataSnapshot.getKey());
                travelDeals.add(td);
                notifyItemInserted(travelDeals.size() - 1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                String key = dataSnapshot.getKey();
                for (int i = 0; i < travelDeals.size(); i++) {
                    // Find the item to remove and then remove it by index
                    if (travelDeals.get(i).getTid().equals(key)) {
                        travelDeals.remove(i);
                        break;
                    }
                }

                notifyDataSetChanged();
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
        ImageView tripImage;
        TextView tripName;
        TextView tripCountry;
        TextView tripCost;

        public TravelDealHolder(@NonNull View itemView) {
            super(itemView);
            tripName = itemView.findViewById(R.id.list_trip_name);
            tripCountry = itemView.findViewById(R.id.list_trip_country);
            tripCost = itemView.findViewById(R.id.list_trip_cost);
            tripImage = itemView.findViewById(R.id.list_trip_image);
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
            showImage(travelDeal.getTripImageUrl());

        }

        private void showImage(String url) {

            if (url != null && !url.isEmpty()) {
                Log.d("IMAGE ADAPTER", " " + url);
                Picasso.get()
                        .load(url)
                        .resize(150, 150)
                        .centerCrop()
                        .into(tripImage);
            }

        }
    }

    @Override
    public Filter getFilter() {
        return travelDealsFilter;
    }

    private Filter travelDealsFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<TravelDeal> filterList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filterList.addAll(travelDeals);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (TravelDeal travelDeal : travelDeals) {
                    if (travelDeal.getTripName().toLowerCase().contains(filterPattern) ||
                            travelDeal.getTripCountry().toLowerCase().contains(filterPattern) ||
                            travelDeal.getTripCategory().toLowerCase().contains(filterPattern) ||
                            travelDeal.getTripDescription().toLowerCase().contains(filterPattern) ||
                            String.valueOf(travelDeal.getTripCost()).toLowerCase()
                                    .contains(filterPattern)) {
                        filterList.add(travelDeal);

                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filterList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            travelDeals.clear();
            travelDeals.addAll((ArrayList) results.values);
            notifyDataSetChanged();
        }
    };
}
