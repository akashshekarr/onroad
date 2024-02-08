package com.example.login_register;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UpdateBusiInf extends AppCompatActivity {

    EditText editTextCompanyName, editTextOwnerName, editTextCompanyLocation, editTextOwnerContact;
    Button save, cancel;

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_busi_inf);

        editTextCompanyName = findViewById(R.id.editTextCompanyName);
        editTextOwnerName = findViewById(R.id.editTextOwnerName);
        editTextCompanyLocation = findViewById(R.id.editTextCompanyLocation);
        editTextOwnerContact = findViewById(R.id.editTextOwnerContact);
        save = findViewById(R.id.btnSave);
        cancel = findViewById(R.id.btnCancel);

        // Get reference to "business_users" node in the database
        databaseReference = FirebaseDatabase.getInstance().getReference("business_users");

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go back to InfoBusi activity
                startActivity(new Intent(UpdateBusiInf.this, InfoBusi.class));
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDataToFirebase();
            }
        });
    }

    private void saveDataToFirebase() {
        String companyName = editTextCompanyName.getText().toString().trim();
        String ownerName = editTextOwnerName.getText().toString().trim();
        String companyLocation = editTextCompanyLocation.getText().toString().trim();
        String ownerContact = editTextOwnerContact.getText().toString().trim();

        // Check if all fields are filled
        if (!companyName.isEmpty() && !ownerName.isEmpty() && !companyLocation.isEmpty() && !ownerContact.isEmpty()) {
            // Get the current user's ID
            String userId = firebaseAuth.getCurrentUser().getUid();

            // Create a BusinessUser object with updated data
            BusinessUser businessUser = new BusinessUser(companyName, ownerName, companyLocation, ownerContact);

            // Update data under the "business_users" node for the current user
            databaseReference.child(userId).setValue(businessUser)
                    .addOnSuccessListener(aVoid -> {
                        // Data saved successfully
                        Toast.makeText(UpdateBusiInf.this, "Data saved successfully", Toast.LENGTH_SHORT).show();
                        // Redirect to InfoBusi activity
                        startActivity(new Intent(UpdateBusiInf.this, InfoBusi.class));
                        finish(); // Finish current activity
                    })
                    .addOnFailureListener(e -> {
                        // Error occurred while saving data
                        Toast.makeText(UpdateBusiInf.this, "Failed to save data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            // Fields are empty
            Toast.makeText(UpdateBusiInf.this, "All fields are required", Toast.LENGTH_SHORT).show();
        }
    }
}
