/**
 * Copyright 2016 IBM Corp. All Rights Reserved.
 *
 * Licensed under the IBM License, a copy of which may be obtained at:
 *
 * http://www14.software.ibm.com/cgi-bin/weblap/lap.pl?li_formnum=L-DDIN-AEGGZJ&popup=y&title=IBM%20IoT%20for%20Automotive%20Sample%20Starter%20Apps%20%28Android-Mobile%20and%20Server-all%29
 *
 * You may not use this file except in compliance with the license.
 */
package carsharing.starter.automotive.iot.ibm.com.mobilestarterapp.Home;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import carsharing.starter.automotive.iot.ibm.com.mobilestarterapp.ConnectedDriverAPI.CarData;
import carsharing.starter.automotive.iot.ibm.com.mobilestarterapp.R;

public class CarDetails extends AppCompatActivity {
    private CarData carData;
    private String formattedAddress = "";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_details);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("Car Details");

        final Bundle extras = getIntent().getExtras();

        if (extras != null) {
            carData = (CarData) getIntent().getSerializableExtra("carData");

            final ImageView carThumbnailImageView = (ImageView) findViewById(R.id.carThumbnail);
            final TextView carTitleTextView = (TextView) findViewById(R.id.behaviorTitle);
            final TextView carStarsTextView = (TextView) findViewById(R.id.carStars);

            Picasso.with(getApplicationContext()).load(carData.thumbnailURL).placeholder(R.drawable.models).into(carThumbnailImageView);

            carTitleTextView.setText(carData.title);
            final String stars = new String(new char[carData.stars]).replace("\0", "\u2605");
            final String emptyStars = new String(new char[5 - carData.stars]).replace("\0", "\u2606");
            carStarsTextView.setText(stars + emptyStars);

            final ListView listView = (ListView) findViewById(R.id.listView);

            final Geocoder geocoder = new Geocoder(this, Locale.getDefault());

            try {
                final List<Address> addressArray = geocoder.getFromLocation(carData.lat, carData.lng, 1);

                formattedAddress = addressArray.get(0).getAddressLine(0);
            } catch (IOException e) {
                e.printStackTrace();
            }

            final CarDetailsAdapter adapter = new CarDetailsAdapter(this, carData, formattedAddress);
            listView.setAdapter(adapter);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();

        return true;
    }

    public void createReservation(final View view) throws IOException {
        Log.i("Button Clicked", "Create Reservation");

        final Intent createReservation = new Intent(view.getContext(), CreateReservation.class);

        final Bundle bundle = new Bundle();
        bundle.putSerializable("carData", carData);
        bundle.putString("formattedAddress", formattedAddress);
        createReservation.putExtras(bundle);

        startActivity(createReservation);
    }
}
