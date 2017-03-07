/**
 * Copyright 2016 IBM Corp. All Rights Reserved.
 * <p>
 * Licensed under the IBM License, a copy of which may be obtained at:
 * <p>
 * http://www14.software.ibm.com/cgi-bin/weblap/lap.pl?li_formnum=L-DDIN-AEGGZJ&popup=y&title=IBM%20IoT%20for%20Automotive%20Sample%20Starter%20Apps%20%28Android-Mobile%20and%20Server-all%29
 * <p>
 * You may not use this file except in compliance with the license.
 */
package carsharing.starter.automotive.iot.ibm.com.mobilestarterapp.Trips;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.ExecutionException;

import carsharing.starter.automotive.iot.ibm.com.mobilestarterapp.ConnectedDriverAPI.API;
import carsharing.starter.automotive.iot.ibm.com.mobilestarterapp.ConnectedDriverAPI.TripData;
import carsharing.starter.automotive.iot.ibm.com.mobilestarterapp.R;

public class Trips extends Fragment {
    public TripData[] trips;
    public ArrayList<ArrayList<String>> locationCache;

    private View view;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_trips, container, false);
        final AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setTitle("Fetching trips...");
        API.runInAsyncUIThread(new Runnable(){
            @Override
            public void run() {
                getTrips();
            }
        },activity);
        return view;
    }

    private void getTrips() {
        final String url = API.tripBehavior + "?all=true";
        try {
            final API.doRequest task = new API.doRequest(new API.doRequest.TaskListener() {
                @Override
                public void postExecute(JSONArray result) throws JSONException {
                    result.remove(result.length() - 1);

                    final ListView listView = (ListView) view.findViewById(R.id.listView);
                    final ArrayList<TripData> tripsArray = new ArrayList<TripData>();
                    for (int i = 0; i < result.length(); i++) {
                        final JSONObject data = result.getJSONObject(i);
                        if (data.has("start_time")) {
                            TripData tempTripData = new TripData(data);
                            tripsArray.add(tempTripData);
                        }
                    }

                    Collections.sort(tripsArray, new Comparator<TripData>() {
                        @Override
                        public int compare(TripData b1, TripData b2) {
                            final long delta = b2.start_time - b1.start_time;
                            if (delta > 0) {
                                return 1;
                            } else if (delta < 0) {
                                return -1;
                            } else {
                                return 0;
                            }
                        }
                    });

                    final TripsDataAdapter adapter = new TripsDataAdapter(getActivity().getApplicationContext(), tripsArray);
                    listView.setAdapter(adapter);

                    final ArrayList<TripData> finalTripArray = tripsArray;
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapter, View view, int position, long arg) {
                            final Intent tripDetails = new Intent(view.getContext(), TripDetails.class);

                            final Bundle bundle = new Bundle();
                            bundle.putSerializable("tripData", finalTripArray.get(position));
                            tripDetails.putExtras(bundle);

                            startActivity(tripDetails);
                        }
                    });

                    final ActionBar supportActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
                    switch (tripsArray.size()) {
                        case 0:
                            supportActionBar.setTitle("You have no trips.");
                        case 1:
                            supportActionBar.setTitle("You have 1 trip.");
                        default:
                            supportActionBar.setTitle("You have " + tripsArray.size() + " trips.");
                    }

                    Log.i("Trip Data", result.toString());
                }
            });

            task.execute(url, "GET").get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}