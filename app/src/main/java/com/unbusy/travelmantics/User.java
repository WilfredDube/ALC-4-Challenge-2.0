package com.unbusy.travelmantics;

import android.net.Uri;
import android.util.Log;

public class User {
    private String userId;
    private String userFirstName;
    private String userLastName;
    private String userCountry;
    private String userCity;
    private String userPhoneNumber;
    private String userEmailAddress;
    private Uri userProfileImage;

    protected User() {
        this.userId = null;
        this.userFirstName = null;
        this.userLastName = null;
        this.userCountry = null;
        this.userCity = null;
        this.userPhoneNumber = null;
        this.userEmailAddress = null;
        this.userProfileImage = null;
    }

    public User(String userId, String userFirstName, String userLastName, String userCountry, String userCity, String userPhoneNumber, String userEmailAddress, Uri userProfileImage) {
        this.userId = userId;
        this.userFirstName = userFirstName;
        this.userLastName = userLastName;
        this.userCountry = userCountry;
        this.userCity = userCity;
        this.userPhoneNumber = userPhoneNumber;
        this.userEmailAddress = userEmailAddress;
        this.userProfileImage = userProfileImage;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserFirstName() {
        return userFirstName;
    }

    public void setUserFirstName(String userFirstName) {
        this.userFirstName = userFirstName;
    }

    public String getUserLastName() {
        return userLastName;
    }

    public void setUserLastName(String userLastName) {
        this.userLastName = userLastName;
    }

    public String getUserCountry() {
        return userCountry;
    }

    public void setUserCountry(String userCountry) {
        this.userCountry = userCountry;
    }

    public String getUserCity() {
        return userCity;
    }

    public void setUserCity(String userCity) {
        this.userCity = userCity;
    }

    public String getUserPhoneNumber() {
        return userPhoneNumber;
    }

    public void setUserPhoneNumber(String userPhoneNumber) {
        this.userPhoneNumber = userPhoneNumber;
    }

    public String getUserEmailAddress() {
        return userEmailAddress;
    }

    public void setUserEmailAddress(String userEmailAddress) {
        this.userEmailAddress = userEmailAddress;
    }

    public Uri getUserProfileImage() {
        return userProfileImage;
    }

    public void setUserProfileImage(Uri userProfileImage) {
        this.userProfileImage = userProfileImage;
        Log.d("TAG", "AccountActy : " + userProfileImage);
    }
}
