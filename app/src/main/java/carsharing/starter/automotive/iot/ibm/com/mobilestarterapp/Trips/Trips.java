/**
 * Copyright 2016 IBM Corp. All Rights Reserved.
 *
 * Licensed under the IBM License, a copy of which may be obtained at:
 *
 * http://www14.software.ibm.com/cgi-bin/weblap/lap.pl?li_formnum=L-DDIN-AEGGZJ&popup=y&title=IBM%20IoT%20for%20Automotive%20Sample%20Starter%20Apps%20%28Android-Mobile%20and%20Server-all%29
 *
 * You may not use this file except in compliance with the license.
 */
package carsharing.starter.automotive.iot.ibm.com.mobilestarterapp.Trips;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_trips, container, false);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Fetching Trips...");

        getTrips();

        return view;
    }

    private void getTrips() {
        String url = API.tripBehavior + "?all=true";

        try {
            API.doRequest task = new API.doRequest(new API.doRequest.TaskListener() {
                @Override
                public void postExecute(JSONArray result) throws JSONException {
                    result.remove(result.length() - 1);

                    ListView listView = (ListView) view.findViewById(R.id.listView);

                    ArrayList<TripData> tripsArray = new ArrayList<TripData>();

                    for (int i=0; i < result.length(); i++) {
                        TripData tempTripData = new TripData(result.getJSONObject(i));
                        tripsArray.add(tempTripData);
                    }

                    Collections.sort(tripsArray, new Comparator<TripData>() {
                        @Override public int compare(TripData b1, TripData b2) {
                            return b2.start_time.intValue() - b1.start_time.intValue();
                        }
                    });

                    TripsDataAdapter adapter = new TripsDataAdapter(getActivity().getApplicationContext(), tripsArray);
                    listView.setAdapter(adapter);

                    final ArrayList<TripData> finalTripArray = tripsArray;

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapter, View view, int position, long arg) {
                            Intent tripDetails = new Intent(view.getContext(), TripDetails.class);

                            Bundle bundle = new Bundle();
                            bundle.putSerializable("tripData", finalTripArray.get(position));
                            tripDetails.putExtras(bundle);

                            startActivity(tripDetails);
                        }
                    });

                    switch (tripsArray.size()) {
                        case 0: ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("You have no trips.");
                        case 1: ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("You have 1 trip.");
                        default: ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("You have " + tripsArray.size() + " trips.");
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