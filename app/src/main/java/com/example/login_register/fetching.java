package com.example.login_register;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class fetching extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fetching);

        // Define the company name you want to search for
        String companyNameToSearch = "ABC Auto Repair";

        // Perform the database query
        performDatabaseQuery(companyNameToSearch);
    }

    private void performDatabaseQuery(String companyNameToSearch) {
        // Get a reference to the Firebase database
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("business_users/" + companyNameToSearch);

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
                } else {
                    // Handle case where no results are found
                    Log.d("FirebaseData", "No data found for company: " + companyNameToSearch);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
                Log.e("FirebaseData", "Failed to read data", databaseError.toException());
            }
        });
    }
}
