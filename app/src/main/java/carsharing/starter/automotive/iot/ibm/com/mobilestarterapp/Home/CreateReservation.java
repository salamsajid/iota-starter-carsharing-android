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
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.concurrent.ExecutionException;

import carsharing.starter.automotive.iot.ibm.com.mobilestarterapp.ConnectedDriverAPI.API;
import carsharing.starter.automotive.iot.ibm.com.mobilestarterapp.ConnectedDriverAPI.CarData;
import carsharing.starter.automotive.iot.ibm.com.mobilestarterapp.R;
import carsharing.starter.automotive.iot.ibm.com.mobilestarterapp.Reservations.Reservations;
import carsharing.starter.automotive.iot.ibm.com.mobilestarterapp.tabNavigation;

public class CreateReservation extends AppCompatActivity {
    private CarData carData;
    private String formattedAddress = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_reservation);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("Confirm Reservation");

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            carData = (CarData) getIntent().getSerializableExtra("carData");
            formattedAddress = getIntent().getStringExtra("formattedAddress");

            final ImageView carThumbnailImageView = (ImageView) findViewById(R.id.carThumbnail);
            final TextView carTitleTextView = (TextView) findViewById(R.id.behaviorTitle);
            final TextView carStarsTextView = (TextView) findViewById(R.id.carStars);

            Picasso.with(getApplicationContext()).load(carData.thumbnailURL).placeholder(R.drawable.models).into(carThumbnailImageView);

            carTitleTextView.setText(carData.title);
            final String stars = new String(new char[carData.stars]).replace("\0", "\u2605");
            final String emptyStars = new String(new char[5 - carData.stars]).replace("\0", "\u2606");
            carStarsTextView.setText(stars + emptyStars);

            final ListView listView = (ListView) findViewById(R.id.listView);

            final CreateReservationAdapter adapter = new CreateReservationAdapter(this, carData, formattedAddress);
            listView.setAdapter(adapter);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();

        return true;
    }

    public void reserveCarAction(View view) {
        final View v = view;

        final String url = API.reservation;

        final Uri.Builder builder = new Uri.Builder()
                .appendQueryParameter("carId", carData.deviceID)
                .appendQueryParameter("pickupTime", CreateReservationAdapter.pickupCal[0].getTimeInMillis() + "")
                .appendQueryParameter("dropOffTime", CreateReservationAdapter.dropoffCal[0].getTimeInMillis() + "");

        final String query = builder.build().getEncodedQuery();

        final Toast toast = Toast.makeText(getApplicationContext(), "Making reservation...", Toast.LENGTH_SHORT);
        toast.show();

        try {
            final API.doRequest task = new API.doRequest(new API.doRequest.TaskListener() {
                @Override
                public void postExecute(JSONArray result) throws JSONException {
                    result.remove(result.length() - 1);

                    if (result.getJSONObject(0).has("reservationId")) {
                        final Toast toast = Toast.makeText(getApplicationContext(), "Car Successfully Reserved!", Toast.LENGTH_SHORT);
                        toast.show();

                        Reservations.userReserved = true;

                        final Intent tabActivity = new Intent(v.getContext(), tabNavigation.class);
                        tabActivity.putExtra("next_activity", "reservations");

                        startActivity(tabActivity);
                    }

                    Log.i("Car Data", result.toString());
                }
            });

            task.execute(url, "POST", query).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}