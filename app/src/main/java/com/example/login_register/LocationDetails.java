package com.example.login_register;

import android.os.Parcel;
import android.os.Parcelable;

public class LocationDetails implements Parcelable {
    private String name;
    private String title;
    private String ownerName;
    private String ownerContact;
    private String address;
    private String category;
    // Add more fields as needed

    // Constructor and getters/setters
    // ...
    // Add more fields as needed

    // Default constructor
    // Default constructor
    public LocationDetails(String title, String ownerName, String ownerContact, String address, String category) {
        this.title = title;
        this.ownerName = ownerName;
        this.ownerContact = ownerContact;
        this.address = address;
        this.category = category;
        // Initialize additional fields as needed
    }

    // Constructor with fields
    public LocationDetails(String name) {
        this.name = name;
        // Initialize other fields as needed
    }

    // Add a getDescription() method
    public String getDescription() {
        // Create a description using the available fields
        // Customize this based on your actual fields
        return "Owner: " + ownerName + "\nContact: " + ownerContact + "\nAddress: " + address;
    }

    // Getter and Setter methods for each field

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Implement Parcelable methods
    protected LocationDetails(Parcel in) {
        name = in.readString();
        // Read other fields if any
    }

    public static final Creator<LocationDetails> CREATOR = new Creator<LocationDetails>() {
        @Override
        public LocationDetails createFromParcel(Parcel in) {
            return new LocationDetails(in);
        }

        @Override
        public LocationDetails[] newArray(int size) {
            return new LocationDetails[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        // Write other fields if any
    }


}
