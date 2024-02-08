package com.example.login_register;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class LocationDetailsFragment extends Fragment {

    private static final String ARG_LOCATION_DETAILS = "locationDetails";

    public LocationDetailsFragment() {
        // Required empty public constructor
    }

    public static LocationDetailsFragment newInstance(LocationDetails locationDetails) {
        LocationDetailsFragment fragment = new LocationDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_LOCATION_DETAILS, locationDetails);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_location_details, container, false);

        Button btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call the handleBackPressed method of the hosting activity
                if (getActivity() instanceof ConfirmLoca) {
                    ((ConfirmLoca) getActivity()).handleBackPressed();
                }
            }
        });

        // Retrieve location details from arguments
        Bundle args = getArguments();

        Log.d("bundle", "onCreateView: "+ args);

        if (args != null) {
            // Extract the "name" from the bundle
            String name = args.getString("name");
            String ownerName = args.getString("ownerName");
            String OwnerContact = args.getString("ownerContactNumber");
            String Address = args.getString("address");

            // Update UI with location details
            TextView nameView = view.findViewById(R.id.textViewCompanyName);
            TextView owner = view.findViewById(R.id.textViewOwnerName);
            TextView contact = view.findViewById(R.id.textViewOwnerContact);
            TextView address = view.findViewById(R.id.textViewAddress);

            if (nameView != null) {
                nameView.setText(name);
            }
            if (owner != null) {
                owner.setText(ownerName);
            }
            if (contact != null) {
                contact.setText(OwnerContact);
            }
            if (address != null) {
                address.setText(Address);
            }

            // Add more TextViews or UI elements to display other details
        }

        return view;
    }
}
