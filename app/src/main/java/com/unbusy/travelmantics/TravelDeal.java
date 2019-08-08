package com.unbusy.travelmantics;

import android.os.Parcel;
import android.os.Parcelable;

public class TravelDeal implements Parcelable {

    public boolean isFavourite;
    public String tid;
    public String tripName;
    public String tripCountry;
    public String tripCategory;
    public String tripDescription;
    public long tripCost;
    public long tripDiscount;
    public String tripImageUrl;



    public int tripRating;

    public TravelDeal() {
    }

    public TravelDeal(String tripName, String tripCountry, String tripCategory, String tripDescription, Long tripCost, Long tripDiscount, int tripRating) {
        this.tripName = tripName;
        this.tripCountry = tripCountry;
        this.tripCategory = tripCategory;
        this.tripDescription = tripDescription;
        this.tripCost = tripCost;
        this.tripDiscount = tripDiscount;
        this.tripRating = tripRating;
        this.isFavourite = false;
        this.tripImageUrl = null;
    }

    public TravelDeal(Parcel source) {
        this.tid = source.readString();
        this.tripName = source.readString();
        this.tripCountry = source.readString();
        this.tripCategory = source.readString();
        this.tripDescription = source.readString();
        this.tripCost = source.readLong();
        this.tripDiscount = source.readLong();
        this.tripImageUrl = source.readString();
        this.isFavourite = source.readByte() == 1;
    }

    public String getTripImageUrl() {
        return tripImageUrl;
    }

    public void setTripImageUrl(String tripImageUrl) {
        this.tripImageUrl = tripImageUrl;
    }

    public boolean isFavourate() {
        return isFavourite;
    }

    public void setFavourate(boolean favourate) {
        isFavourite = favourate;
    }
    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }
    public int getTripRating() {
        return tripRating;
    }

    public void setTripRating(int tripRating) {
        this.tripRating = tripRating;
    }


    public String getTripName() {
        return tripName;
    }

    public void setTripName(String tripName) {
        this.tripName = tripName;
    }

    public String getTripCountry() {
        return tripCountry;
    }

    public void setTripCountry(String tripCountry) {
        this.tripCountry = tripCountry;
    }

    public String getTripCategory() {
        return tripCategory;
    }

    public void setTripCategory(String tripCategory) {
        this.tripCategory = tripCategory;
    }

    public String getTripDescription() {
        return tripDescription;
    }

    public void setTripDescription(String tripDescription) {
        this.tripDescription = tripDescription;
    }

    public long getTripCost() {
        return tripCost;
    }

    public void setTripCost(long tripCost) {
        this.tripCost = tripCost;
    }

    public long getTripDiscount() {
        return tripDiscount;
    }

    public void setTripDiscount(long tripDiscount) {
        this.tripDiscount = tripDiscount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(tid);
        dest.writeString(tripName);
        dest.writeString(tripCountry);
        dest.writeString(tripCategory);
        dest.writeString(tripDescription);
        dest.writeLong(tripCost);
        dest.writeLong(tripDiscount);
        dest.writeString(tripImageUrl);
        dest.writeByte(isFavourite ? (byte) 1 : (byte) 0);
    }

    public static final Parcelable.Creator<TravelDeal> CREATOR =
            new Creator<TravelDeal>() {
                @Override
                public TravelDeal createFromParcel(Parcel source) {
                    return new TravelDeal(source);
                }

                @Override
                public TravelDeal[] newArray(int size) {
                    return new TravelDeal[size];
                }
            };

}
