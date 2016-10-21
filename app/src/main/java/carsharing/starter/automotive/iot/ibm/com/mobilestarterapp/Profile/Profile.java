/**
 * Copyright 2016 IBM Corp. All Rights Reserved.
 *
 * Licensed under the IBM License, a copy of which may be obtained at:
 *
 * http://www14.software.ibm.com/cgi-bin/weblap/lap.pl?li_formnum=L-DDIN-AEGGZJ&popup=y&title=IBM%20IoT%20for%20Automotive%20Sample%20Starter%20Apps%20%28Android-Mobile%20and%20Server-all%29
 *
 * You may not use this file except in compliance with the license.
 */
package carsharing.starter.automotive.iot.ibm.com.mobilestarterapp.Profile;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.ExecutionException;

import carsharing.starter.automotive.iot.ibm.com.mobilestarterapp.ConnectedDriverAPI.API;
import carsharing.starter.automotive.iot.ibm.com.mobilestarterapp.ConnectedDriverAPI.DriverStatistics;
import carsharing.starter.automotive.iot.ibm.com.mobilestarterapp.ConnectedDriverAPI.ScoringBehavior;
import carsharing.starter.automotive.iot.ibm.com.mobilestarterapp.R;

public class Profile extends Fragment {
    public DriverStatistics stat;

    public ArrayList<ScoringBehavior> behaviors = new ArrayList<ScoringBehavior>();

    public DriverStatistics.TimeRange timesOfDay;
    public DriverStatistics.SpeedPattern trafficConditions;
    public DriverStatistics.RoadType roadTypes;

    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_reservations, container, false);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Fetching Profile...");

        getDriverStats();

        return view;
    }

    private void getDriverStats() {
        String url = API.driverStats;

        try {
            API.doRequest task = new API.doRequest(new API.doRequest.TaskListener() {
                @Override
                public void postExecute(JSONArray result) throws JSONException {
                    result.remove(result.length() - 1);

                    ListView listView = (ListView) view.findViewById(R.id.listView);

                    ArrayList<DriverStatistics> stats = new ArrayList<DriverStatistics>();

                    for (int i=0; i < result.length(); i++) {
                        DriverStatistics tempDriverStatistics = new DriverStatistics(result.getJSONObject(i));
                        stats.add(tempDriverStatistics);
                    }

                    if (stats.size() > 0) {
                        stat = stats.get(0);
                        behaviors = stat.scoring.getScoringBehaviors();

                        Collections.sort(behaviors, new Comparator<ScoringBehavior>() {
                            @Override public int compare(ScoringBehavior b1, ScoringBehavior b2) {
                                return b2.count - b1.count;
                            }
                        });

                        timesOfDay = stat.timeRange;
                        timesOfDay.toDictionary();

                        roadTypes = stat.roadType;
                        roadTypes.toDictionary();

                        trafficConditions = stat.speedPattern;
                        trafficConditions.toDictionary();

                        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Your score is " + Math.round(stat.scoring.score) + "% for " + Math.round(stat.totalDistance/16.09344)/100 + " miles.");

                        ProfileDataAdapter adapter = new ProfileDataAdapter(getActivity().getApplicationContext(), stats, behaviors, timesOfDay, trafficConditions, roadTypes);
                        listView.setAdapter(adapter);
                    } else {
                        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("You have no trips.");
                    }

                    Log.i("Profile Data", result.toString());
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