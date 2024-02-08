package com.example.login_register;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.ArrayList;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ConfirmLoca extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener,  GoogleMap.OnInfoWindowClickListener {
    private static final int REQUEST_CODE_LOCATION = 1;
    private GoogleMap mMap;
    double currentLat, currentLong;
    Button btFind;
    private FusedLocationProviderClient fusedLocationClient;
    private boolean locationPermissionGranted = false;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_loca);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        btFind = findViewById(R.id.bt_find);
        SearchView searchView = findViewById(R.id.searchView);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        databaseReference = FirebaseDatabase.getInstance().getReference("business_users");

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        getLocationPermission();

        Button btnConfirm = findViewById(R.id.btnConfirm);
        Button btnCancel = findViewById(R.id.btnCancel);

        btnConfirm.setOnClickListener(view -> {
            if (locationPermissionGranted) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                    if (location != null) {

                        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                        Log.d(String.valueOf(currentLatLng), "onCreate: ");
                        mMap.clear();
                        mMap.addMarker(new MarkerOptions().position(currentLatLng).title("Current Location"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
                    } else {
                        requestLocationUpdates();
                        Toast.makeText(this, "Location not available. Requesting updates...", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        btnCancel.setOnClickListener(view -> {
            Intent i = new Intent(ConfirmLoca.this, HomePage.class);
            startActivity(i);
            Log.d("button clicked: ", "onCreate: Cancel button  Clicked");
        });

        hideLocationDetailsFragment();

        btFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (locationPermissionGranted) {
                    if (ActivityCompat.checkSelfPermission(ConfirmLoca.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ConfirmLoca.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    fusedLocationClient.getLastLocation().addOnSuccessListener(ConfirmLoca.this, location -> {
                        if (location != null) {
                            currentLat = location.getLatitude();
                            currentLong = location.getLongitude();

                            String searchText = ((SearchView) findViewById(R.id.searchView)).getQuery().toString();
                            String formattedSearchText = searchText.replaceAll(" ", "+");

                            String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json" +
                                    "?location=" + currentLat + "," + currentLong +
                                    "&radius=5000" +
                                    "&keyword=" + formattedSearchText +
                                    "&sensor=true" +
                                    "&key=" + "YOUR_API_KEY";

                            // Execute PlaceTask with the URL as a parameter
                            Log.d(url, "onClick: ");
                            new PlaceTask().execute(url);
                        } else {
                            requestLocationUpdates();
                            Toast.makeText(ConfirmLoca.this, "Location not available. Requesting updates...", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

    }

    private void fetchData(Marker marker) {
        // Assuming you have a company name, replace "companyName" with actual company name
        String companyName = marker.getTitle();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("business_users/" + companyName);

        // Add a listener for a single value event
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Check if the query returns any results
                if (dataSnapshot.exists()) {
                    // Extract data from the snapshot
                    String address = dataSnapshot.child("address").getValue(String.class);
                    String ownerContactNumber = dataSnapshot.child("ownerContactNumber").getValue(String.class);
                    String ownerName = dataSnapshot.child("ownerName").getValue(String.class);

                    // Use the extracted data as needed
                    Log.d("FirebaseData", "Address: " + address);
                    Log.d("FirebaseData", "Owner Contact Number: " + ownerContactNumber);
                    Log.d("FirebaseData", "Owner Name: " + ownerName);

                    // Create a bundle to pass data to the fragment
                    Bundle bundle = new Bundle();
                    bundle.putString("name", companyName);
                    bundle.putString("address", address);
                    bundle.putString("ownerContactNumber", ownerContactNumber);
                    bundle.putString("ownerName", ownerName);

                    // Create and show the fragment
                    LocationDetailsFragment locationDetailsFragment = new LocationDetailsFragment();
                    locationDetailsFragment.setArguments(bundle);

                    // Show the fragment using FragmentTransaction
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, locationDetailsFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();

                    findViewById(R.id.fragment_container).setVisibility(View.VISIBLE);
                } else {
                    // Handle case where no results are found
                    Log.d("FirebaseData", "No data found for company: " + companyName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
                Log.e("FirebaseData", "Failed to read data", databaseError.toException());
            }
        });

    }


    private LatLng currentUserLocation;  // Variable to store the current user location

    // Updated method to handle the response from the API
    private void handleApiResponse(String response) {
        if (response != null) {
            Log.d("ApiResponse", "Response: " + response);
            try {
                JSONObject jsonObject = new JSONObject(response);
                List<HashMap<String, String>> placesList = new JsonParser().parseResult(jsonObject);

                Log.d(mMap.toString(), "handleApiResponse: ");
                // Clear existing markers on the map
                mMap.clear();

                Log.d("places list", "handleApiResponse: "+ placesList);

                if (!placesList.isEmpty()) {
                    // List to store all markers
                    List<Marker> markerList = new ArrayList<>();

                    for (HashMap<String, String> place : placesList) {
                        double lat = Double.parseDouble(place.get("lat"));
                        double lng = Double.parseDouble(place.get("lng"));
                        String name = place.get("name");
                        String address = place.get("address");

                        LatLng latLng = new LatLng(lat, lng);

                        MarkerOptions options = new MarkerOptions();
                        options.position(latLng);
                        options.title(name);
                        options.snippet(address);

                        Marker marker = mMap.addMarker(options);
                        markerList.add(marker);
                    }

// Set an OnMarkerClickListener for all markers
                    mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker clickedMarker) {
                            // Check if the clicked marker is in the markerList
                            if (markerList.contains(clickedMarker)) {
                                // Call the fetchData method with the clicked marker
                                fetchData(clickedMarker);
                                return true; // Return true if you've handled the click
                            } else {
                                return false; // Return false if the clicked marker is not in the markerList
                            }
                        }
                    });

                    // Move the camera to the first marker
                    LatLng firstMarker = new LatLng(Double.parseDouble(placesList.get(0).get("lat")), Double.parseDouble(placesList.get(0).get("lng")));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstMarker, 15));

                    BitmapDescriptor customIcon = BitmapDescriptorFactory.fromResource(R.drawable.locicon);
                    // Add marker for the current user location
                    if (currentUserLocation != null) {
                        MarkerOptions userMarkerOptions = new MarkerOptions()
                                .position(currentUserLocation)
                                .title("Your Location")
                                .icon(customIcon);
                        mMap.addMarker(userMarkerOptions);
                    }
                } else {
                    // Handle the case where placesList is empty
                    Toast.makeText(ConfirmLoca.this, "No places found", Toast.LENGTH_SHORT).show();

                    // Optionally, you may want to handle this case by showing a message or taking appropriate action.
                }
            } catch (JSONException e) {
                e.printStackTrace();
                // Handle JSON parsing error
                Toast.makeText(ConfirmLoca.this, "Failed to parse JSON response", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Handle the case where the response is null
            Toast.makeText(ConfirmLoca.this, "Empty response from server", Toast.LENGTH_SHORT).show();
        }
    }


    public void handleBackPressed() {
        // Check if the fragment_container is currently visible
        if (findViewById(R.id.fragment_container).getVisibility() == View.VISIBLE) {
            // If visible, hide it
            findViewById(R.id.fragment_container).setVisibility(View.GONE);
        } else {
            // If not visible, proceed with the default back button behavior
            super.onBackPressed();
        }
    }


    // Method to update the current user location
    private void updateCurrentUserLocation(Location location) {
        if (location != null) {
            currentUserLocation = new LatLng(location.getLatitude(), location.getLongitude());
        }
    }

    private void openLocationDetailsFragment(LocationDetails locationDetails) {
        // Show the fragment using FragmentTransaction
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Replace R.id.locationDetailsFragment with the actual ID of your FrameLayout
        LocationDetailsFragment locationDetailsFragment = LocationDetailsFragment.newInstance(locationDetails);
        fragmentTransaction.replace(R.id.fragment_container, locationDetailsFragment);
        fragmentTransaction.addToBackStack(null); // Optional: add to back stack for fragment navigation
        fragmentTransaction.commit();
    }

    private LocationDetails getLocationDetails(Marker marker) {
        // Replace this with your logic to retrieve location details based on the marker
        // Example:
        String markerTitle = marker.getTitle();

        // Use the title or any other marker property to fetch details from your data source
        // For example, if you have a database or API to get details based on the title, use it here
        // Replace the following line with your actual data retrieval logic
        LocationDetails locationDetails = fetchDataFromDataSource(markerTitle);

        return locationDetails;
    }

    private void hideLocationDetailsFragment() {
        Fragment locationDetailsFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (locationDetailsFragment != null) {
            ((Fragment) locationDetailsFragment).requireView().setVisibility(View.GONE);
        }
    }

    private LocationDetails fetchDataFromDataSource(String markerTitle) {
        // Replace this with your actual data retrieval logic from your data source
        // For example, if you have a database or API, make a query or request to get the details
        // For now, creating a dummy LocationDetails object
        return new LocationDetails(markerTitle, "Owner Name", "Owner Contact", "123 Main St", "Landmark");
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        return false;
    }


    public class PlaceTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... strings) {
            try {
                String url = strings[0];
                return downloadUrl(url);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            if (s != null) {
                handleApiResponse(s);  // Call handleApiResponse with the response
            } else {
                // Handle the case where downloadUrl failed
                Toast.makeText(ConfirmLoca.this, "Failed to fetch data from server", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String downloadUrl(String string) throws IOException {
        URL url = new URL(string);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.connect();
        InputStream stream = connection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder builder = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }
        String data = builder.toString();
        reader.close();
        return data;
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (locationPermissionGranted) {
            getDeviceLocation();
            mMap.setOnMarkerClickListener(this); // Set the marker click listener
            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    // Handle the info window click event here
                    String placeName = marker.getTitle();
                    Log.d("InfoWindowClick", "Info window clicked for place: " + placeName);

                    // Add your logic to display more details or perform actions related to the clicked marker
                }
            });

        }
    }

    public void onInfoWindowClick(Marker marker) {
        // Handle the info window click event here
        // You can customize the behavior, e.g., open a new activity, show a dialog, etc.
        String placeName = marker.getTitle();
        Log.d("InfoWindowClick", "Clicked on marker: " + placeName);
        // Add your logic here to display detailed information about the place
    }

    private boolean onMarkerClickCustom(Marker marker) {
        // Do your custom handling
        return true; // Return true if you've handled the click
    }


    private void getDeviceLocation() {
        try {
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) {
                    currentLat = location.getLatitude();
                    currentLong = location.getLongitude();
                    LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(currentLatLng).title("Current Location"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 10));
                } else {
                    requestLocationUpdates();
                }
            });

        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void requestLocationUpdates() {
        try {
            LocationRequest locationRequest = LocationRequest.create()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(10000); // Update every 10 seconds (adjust as needed)

            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            if (locationResult.getLastLocation() != null) {
                LatLng currentLatLng = new LatLng(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude());
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(currentLatLng).title("Current Location"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
                fusedLocationClient.removeLocationUpdates(this); // Stop updates after getting the location
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_LOCATION && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
            getDeviceLocation();
        }
    }

    public class ParserTask extends AsyncTask<String, Integer, List<HashMap<String, String>>> {

        @Override
        protected List<HashMap<String, String>> doInBackground(String... string) {
            JsonParser jsonParser = new JsonParser();
            List<HashMap<String, String>> mapList;
            Log.d("string: ",string[0]);
            mapList = jsonParser.parseResult(string[0]);
            return mapList;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> hashMaps) {
            if (hashMaps != null) {
                if (mMap != null) {
                    mMap.clear();

                    for (int i = 0; i < hashMaps.size(); i++) {
                        HashMap<String, String> hashMapList = hashMaps.get(i);

                        double lat = Double.parseDouble(Objects.requireNonNull(hashMapList.get("lat")));
                        double lng = Double.parseDouble(Objects.requireNonNull(hashMapList.get("lng")));
                        String name = hashMapList.get("name");

                        LatLng latLng = new LatLng(lat, lng);

                        MarkerOptions options = new MarkerOptions();
                        options.position(latLng);
                        options.title(name);
                        mMap.addMarker(options);
                    }

                    mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker) {
                            // Handle marker click here
                            // You can use marker.getTitle() to get the marker's title
                            return true; // Return true if you've handled the click
                        }
                    });

                    // Move the camera to the first marker
                    if (!hashMaps.isEmpty()) {
                        LatLng firstMarker = new LatLng(Double.parseDouble(Objects.requireNonNull(hashMaps.get(0).get("lat"))), Double.parseDouble(Objects.requireNonNull(hashMaps.get(0).get("lng"))));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstMarker, 15));
                    }
                } else {
                    // Handle the case where map is null (e.g., show a log or a toast message)
                }
            } else {
                // Handle the case where hashMaps is null (e.g., show a log or a toast message)
            }
        }
    }

}
