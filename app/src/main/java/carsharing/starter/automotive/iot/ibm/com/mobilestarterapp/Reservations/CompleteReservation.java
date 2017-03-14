/**
 * Copyright 2016 IBM Corp. All Rights Reserved.
 * <p>
 * Licensed under the IBM License, a copy of which may be obtained at:
 * <p>
 * http://www14.software.ibm.com/cgi-bin/weblap/lap.pl?li_formnum=L-DDIN-AEGGZJ&popup=y&title=IBM%20IoT%20for%20Automotive%20Sample%20Starter%20Apps%20%28Android-Mobile%20and%20Server-all%29
 * <p>
 * You may not use this file except in compliance with the license.
 */
package carsharing.starter.automotive.iot.ibm.com.mobilestarterapp.Reservations;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.concurrent.ExecutionException;

import carsharing.starter.automotive.iot.ibm.com.mobilestarterapp.AnalyzeMyDriving;
import carsharing.starter.automotive.iot.ibm.com.mobilestarterapp.ConnectedDriverAPI.API;
import carsharing.starter.automotive.iot.ibm.com.mobilestarterapp.ConnectedDriverAPI.ReservationsData;
import carsharing.starter.automotive.iot.ibm.com.mobilestarterapp.R;
import carsharing.starter.automotive.iot.ibm.com.mobilestarterapp.tabNavigation;

public class CompleteReservation extends AppCompatActivity {

    private ReservationsData reservationData;
    private ReservationsData reservation;

    static final GregorianCalendar[] pickupCal = {new GregorianCalendar()};

    private Button unlockButton;
    private Button cancelButton;

    private boolean unlocked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_reservation);

        final ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setDisplayHomeAsUpEnabled(true);

        final Bundle extras = getIntent().getExtras();

        if (extras != null) {
            reservationData = (ReservationsData) getIntent().getSerializableExtra("reservationData");
            reservation = reservationData;

            pickupCal[0].setTimeInMillis(reservationData.pickupTime.longValue());
            supportActionBar.setTitle("Pickup at " + dateToString(pickupCal[0]));

            final ImageView carThumbnailImageView = (ImageView) findViewById(R.id.carThumbnail);
            final TextView carTitleTextView = (TextView) findViewById(R.id.behaviorTitle);
            final TextView carStarsTextView = (TextView) findViewById(R.id.carStars);

            Picasso.with(getApplicationContext()).load(reservationData.carDetails.thumbnailURL).placeholder(R.drawable.models).into(carThumbnailImageView);

            carTitleTextView.setText(reservationData.carDetails.title);
            final String stars = new String(new char[reservationData.carDetails.stars]).replace("\0", "\u2605");
            final String emptyStars = new String(new char[5 - reservationData.carDetails.stars]).replace("\0", "\u2606");
            carStarsTextView.setText(stars + emptyStars);

            final ListView listView = (ListView) findViewById(R.id.listView);

            final CompleteReservationAdapter adapter = new CompleteReservationAdapter(this, reservationData);
            listView.setAdapter(adapter);
        }

        unlockButton = (Button) findViewById(R.id.unlockButton);
        cancelButton = (Button) findViewById(R.id.cancelButton);

//        cancelButton.setEnabled(false);
        unlockButton.setEnabled(false);

        setLabelsAccordingToStatus();
    }

    @Override
    public void onBackPressed() {
        if (unlocked) {
            toggleLockUnlock();
        }
        super.onBackPressed();
    }

    @Override
    public void onStop() {
        if (unlocked) {
            toggleLockUnlock();
        }
        super.onStop();
    }


    @Override
    public boolean onSupportNavigateUp() {
        if (unlocked) {
            toggleLockUnlock();
        }
        finish();
        return true;
    }

    public static String dateToString(final GregorianCalendar cal) {
        final SimpleDateFormat formattedCal = new SimpleDateFormat("MM/dd/yy, hh:mm a");
        formattedCal.setCalendar(cal);
        return formattedCal.format(cal.getTime());
    }

    public void unlockCar(final View view) {
        toggleLockUnlock();
    }

    private void toggleLockUnlock() {
        final String url = API.carControl;

        final String reservationId = reservationData._id;
        final String[] command = {new String()};
        command[0] = "lock";

        if (reservation == null || ("Locked".equals(reservation.carDetails.status))) {
            command[0] = "unlock";
        }

        try {
            final JSONObject bodyObject = new JSONObject();
            bodyObject.put("reservationId", reservationId);
            bodyObject.put("command", command[0]);

            final API.doRequest task = new API.doRequest(new API.doRequest.TaskListener() {
                @Override
                public void postExecute(JSONArray result) throws JSONException {
                    final JSONObject serverResponse = result.getJSONObject(result.length() - 1);
                    final int statusCode = serverResponse.getInt("statusCode");

                    result.remove(result.length() - 1);

                    final ArrayList<ReservationsData> reservations = new ArrayList<ReservationsData>();
                    for (int i = 0; i < result.length(); i++) {
                        final ReservationsData tempReservationData = new ReservationsData(result.getJSONObject(i));
                        reservations.add(tempReservationData);
                    }

                    if (reservations.size() == 1) {
                        reservation = reservations.get(0);
                    }

                    final ActionBar supportActionBar = getSupportActionBar();
                    switch (statusCode) {
                        case 200:
                            if ("lock".equals(command[0])) {
                                unlockButton.setText("Unlock the car");
                                supportActionBar.setTitle("Drive completed.");
                                unlocked = false;
                                break;
                            } else {
                                unlockButton.setText("Lock the car");

                                cancelButton.setEnabled(true);

                                final Toast toast = Toast.makeText(getApplicationContext(), "Enjoy your ride and drive safe.", Toast.LENGTH_SHORT);
                                toast.show();

                                setLabelsAccordingToStatus();

                                supportActionBar.setTitle("Simulating your drive...");
                                API.runInAsyncUIThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        final Toast toast = Toast.makeText(getApplicationContext(), "Press LOCK THE CAR button to complete driving.", Toast.LENGTH_SHORT);
                                        toast.show();
                                    }
                                }, CompleteReservation.this, 1000);
                                unlocked = true;
                                break;
                            }
                        default:
                            supportActionBar.setTitle("Error in simulating drive...");
                            Log.e("Unlock Car", "unknown status code on unlock car action");

                            break;
                    }

                    Log.i("Unlock Car", result.toString());
                }
            });

            task.execute(url, "POST", null, bodyObject.toString()).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setLabelsAccordingToStatus() {
        if (reservation != null) {
            if ("active".equals(reservation.status)) {
                cancelButton.setText("Cancel Reservation");
            } else {
                cancelButton.setText("Complete Reservation");
            }

            if ("Locked".equals(reservation.carDetails.status)) {
                unlockButton.setText("Unlock the car");
            } else {
                unlockButton.setText("Lock the car");
            }
        } else {
            if ("active".equals(reservationData.status)) {
                cancelButton.setText("Cancel Reservation");
            } else {
                cancelButton.setText("Complete Reservation");
            }

            if ("Locked".equals(reservationData.carDetails.status)) {
                unlockButton.setText("Unlock the car");
            } else {
                unlockButton.setText("Lock the car");
            }
        }

        unlockButton.setEnabled(true);
    }

    public void cancelReservation(View view) {
        cancelButton.setEnabled(false);

        final String url = API.reservation + "/" + reservationData._id;

        final View v = view;

        try {
            if (reservation.status.equals(null) || "active".equals(reservation.status)) {
                final API.doRequest task = new API.doRequest(new API.doRequest.TaskListener() {
                    @Override
                    public void postExecute(JSONArray result) throws JSONException {

                        Log.i("Cancel Reservation", result.toString());

                        final JSONObject serverResponse = result.getJSONObject(result.length() - 1);
                        final int statusCode = serverResponse.getInt("statusCode");
                        if (statusCode == 200) {
                            final Toast toast = Toast.makeText(getApplicationContext(), "Reservation successfully cancelled!", Toast.LENGTH_SHORT);
                            toast.show();

                            Reservations.userReserved = true;

                            final Intent tabActivity = new Intent(v.getContext(), tabNavigation.class);
                            tabActivity.putExtra("next_activity", "reservations");

                            startActivity(tabActivity);
                        } else {
                            final Toast toast = Toast.makeText(getApplicationContext(), "Reservation cannot be canceled. You may unlock the car and complete it.", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                });

                task.execute(url, "DELETE").get();
            } else {
                final API.doRequest task = new API.doRequest(new API.doRequest.TaskListener() {
                    @Override
                    public void postExecute(JSONArray result) throws JSONException {

                        Log.i("Complete Reservation", result.toString());

                        final JSONObject serverResponse = result.getJSONObject(result.length() - 1);
                        final int statusCode = serverResponse.getInt("statusCode");
                        if (statusCode == 200) {
                            final Toast toast = Toast.makeText(getApplicationContext(), "Reservation successfully completed!", Toast.LENGTH_SHORT);
                            toast.show();

                            Reservations.userReserved = true;

                            final Intent tabActivity = new Intent(v.getContext(), tabNavigation.class);
                            tabActivity.putExtra("next_activity", "trips");

                            startActivity(tabActivity);

                        } else {
                            final Toast toast = Toast.makeText(getApplicationContext(), "Complete reservation failed.", Toast.LENGTH_SHORT);
                            toast.show();

                            final Intent tabActivity = new Intent(v.getContext(), tabNavigation.class);
                            tabActivity.putExtra("next_activity", "reservations");

                            startActivity(tabActivity);
                        }
                    }
                });

                String trip_id = AnalyzeMyDriving.getTripId(reservation.carDetails.deviceID);

                //TODO Compare with the Swift Version, needs additions
                final JSONObject bodyObject = new JSONObject();
                bodyObject.put("status", "close");
                bodyObject.put("trip_id", trip_id);

                task.execute(url, "PUT", null, bodyObject.toString()).get();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}