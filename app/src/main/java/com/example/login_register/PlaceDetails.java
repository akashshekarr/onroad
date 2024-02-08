package com.example.login_register;

import java.util.List;

public class PlaceDetails {
    private String name;
    private double latitude;
    private double longitude;
    private String address;
    private String businessStatus;
    private String icon;
    private String iconBackgroundColor;
    private String iconMaskBaseUri;
    private boolean isOpenNow;
    private List<String> photoReferences;
    private String placeId;
    private String plusCodeCompoundCode;
    private String plusCodeGlobalCode;
    private double rating;
    private String reference;
    private String scope;
    private List<String> types;
    private int userRatingsTotal;
    private String vicinity;

    // Constructors
    public PlaceDetails() {
        // Default constructor
    }

    public PlaceDetails(String name, double latitude, double longitude, String address, String businessStatus,
                        String icon, String iconBackgroundColor, String iconMaskBaseUri, boolean isOpenNow,
                        List<String> photoReferences, String placeId, String plusCodeCompoundCode,
                        String plusCodeGlobalCode, double rating, String reference, String scope,
                        List<String> types, int userRatingsTotal, String vicinity) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.businessStatus = businessStatus;
        this.icon = icon;
        this.iconBackgroundColor = iconBackgroundColor;
        this.iconMaskBaseUri = iconMaskBaseUri;
        this.isOpenNow = isOpenNow;
        this.photoReferences = photoReferences;
        this.placeId = placeId;
        this.plusCodeCompoundCode = plusCodeCompoundCode;
        this.plusCodeGlobalCode = plusCodeGlobalCode;
        this.rating = rating;
        this.reference = reference;
        this.scope = scope;
        this.types = types;
        this.userRatingsTotal = userRatingsTotal;
        this.vicinity = vicinity;
    }

    // Getters and setters (generated using your IDE or manually)
    // ...

    // Example getters for the basic properties
    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getAddress() {
        return address;
    }

    // Add similar getters and setters for other properties
    // ...
}

