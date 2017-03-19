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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import carsharing.starter.automotive.iot.ibm.com.mobilestarterapp.ConnectedDriverAPI.API;
import carsharing.starter.automotive.iot.ibm.com.mobilestarterapp.ConnectedDriverAPI.ReservationsData;
import carsharing.starter.automotive.iot.ibm.com.mobilestarterapp.R;

public class Reservations extends Fragment {
    public static ArrayList<ReservationsData> reservations = new ArrayList<ReservationsData>();

    public static boolean userReserved = true;

    private View view;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_reservations, container, false);

        final FragmentActivity activity = getActivity();
        ((AppCompatActivity) activity).getSupportActionBar().setTitle("Fetching reservations...");
        API.runInAsyncUIThread(new Runnable() {
            @Override
            public void run() {
                if (userReserved) {
                    userReserved = false;
                    getReservations();
                } else {
                    if (reservations.size() > 0) {
                        setContent(reservations);
                    } else {
                        getReservations();
                    }
                }
            }
        }, activity);

        return view;
    }

    public void getReservations() {
        final String url = API.reservations;

        try {
            final API.doRequest task = new API.doRequest(new API.doRequest.TaskListener() {
                @Override
                public void postExecute(JSONArray result) throws JSONException {
                    result.remove(result.length() - 1);

                    final ArrayList<ReservationsData> reservationsArray = new ArrayList<ReservationsData>();

                    for (int i = 0; i < result.length(); i++) {
                        final ReservationsData tempReservationData = new ReservationsData(result.getJSONObject(i));
                        reservationsArray.add(tempReservationData);
                    }

                    reservations = reservationsArray;

                    setContent(reservations);

                    Log.i("Reservation Data", result.toString());
                }
            });

            task.execute(url, "GET").get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void setContent(final ArrayList<ReservationsData> reservationsArray) {
        final FragmentActivity activity = getActivity();
        if (activity == null) {
            return;
        }
        final ListView listView = (ListView) view.findViewById(R.id.listView);

        final ReservationsDataAdapter adapter = new ReservationsDataAdapter(activity.getApplicationContext(), reservationsArray);
        listView.setAdapter(adapter);

        final ArrayList<ReservationsData> finalReservationsArray = reservationsArray;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long arg) {
                final Intent completeReservation = new Intent(view.getContext(), CompleteReservation.class);

                final Bundle bundle = new Bundle();
                bundle.putSerializable("reservationData", finalReservationsArray.get(position));
                completeReservation.putExtras(bundle);

                startActivity(completeReservation);
            }
        });

        final ActionBar supportActionBar = ((AppCompatActivity) activity).getSupportActionBar();
        switch (reservations.size()) {
            case 0:
                supportActionBar.setTitle("You have no reservations.");
            case 1:
                supportActionBar.setTitle("You have one reservation.");
            default:
                supportActionBar.setTitle("You have " + reservations.size() + " reservations.");
        }
    }
}