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

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import carsharing.starter.automotive.iot.ibm.com.mobilestarterapp.ConnectedDriverAPI.API;
import carsharing.starter.automotive.iot.ibm.com.mobilestarterapp.ConnectedDriverAPI.Path;
import carsharing.starter.automotive.iot.ibm.com.mobilestarterapp.ConnectedDriverAPI.Trip;
import carsharing.starter.automotive.iot.ibm.com.mobilestarterapp.ConnectedDriverAPI.TripBehavior;
import carsharing.starter.automotive.iot.ibm.com.mobilestarterapp.ConnectedDriverAPI.TripData;
import carsharing.starter.automotive.iot.ibm.com.mobilestarterapp.ConnectedDriverAPI.TripLocation;
import carsharing.starter.automotive.iot.ibm.com.mobilestarterapp.R;

public class TripDetails extends AppCompatActivity implements OnMapReadyCallback {
    final ArrayList<Trip> stats = new ArrayList<Trip>();
    Trip trip;

    LatLng startLoc;
    LatLng endLoc;

    final Map behaviors = new HashMap();

    ArrayList<Polyline> behaviorPolylines = new ArrayList<>();

    ListView listView;
    TextView notAnalyzedLabel;

    private GoogleMap mMap;

    private TripData tripData;
    static final GregorianCalendar[] tripCal = {new GregorianCalendar()};

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_details2);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Trip Details");

        listView = (ListView) findViewById(R.id.listView);
        notAnalyzedLabel = (TextView) findViewById(R.id.notAnalyzedLabel);

        final Bundle extras = getIntent().getExtras();

        if (extras != null) {
            tripData = (TripData) getIntent().getSerializableExtra("tripData");

            tripCal[0].setTimeInMillis(tripData.start_time);

            String durationText = "";
            if (tripData.start_time > 0) {
                final long durationValue = tripData.duration / 1000 / 60;
                durationText += (durationValue + " Min");
            } else {
                durationText += ("Unknown Duration");
            }
            getSupportActionBar().setTitle(dateToString(tripCal[0]) + " (" + durationText + ")");

            getDriverBehavior();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();

        return true;
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;

        final LatLng startLocation = new LatLng(tripData.start_latitude, tripData.start_longitude);
        mMap.addMarker(new MarkerOptions().position(startLocation).title("Start of Trip"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(startLocation));

        final LatLng endLocation = new LatLng(tripData.end_latitude, tripData.end_longitude);
        mMap.addMarker(new MarkerOptions().position(endLocation).title("End of Trip"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(endLocation));

        final String url = API.tripRoutes + "/" + tripData.trip_id;

        try {
            final API.doRequest task = new API.doRequest(new API.doRequest.TaskListener() {
                @Override
                public void postExecute(JSONArray result) throws JSONException {
                    result.remove(result.length() - 1);

                    final ArrayList<Path> stats = Path.fromDictionary(result);

                    if (stats.size() > 0) {
                        final Path stat = stats.get(0);
                        final Polyline line = mMap.addPolyline(new PolylineOptions()
                                .add(stat.coordinates)
                                .width(5)
                                .color(Color.BLUE));

                        final LatLngBounds.Builder builder = new LatLngBounds.Builder();
                        for (int i = 0; i < stat.coordinates.length; i++) {
                            builder.include(stat.coordinates[i]);
                        }

                        final LatLngBounds latLngBounds = builder.build();
                        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                            @Override
                            public void onMapLoaded() {
                                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 100));
                            }
                        });

                    }

                    Log.i("Trip Routes", result.toString());
                }
            });

            task.execute(url, "GET").get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void getDriverBehavior() {
        final String trip_uuid = tripData.trip_uuid;

        if (trip_uuid == null) {
            final String trip_id = tripData.trip_id;
            final String url = API.tripAnalysisStatus + "/" + trip_id;

            try {
                final API.doRequest task = new API.doRequest(new API.doRequest.TaskListener() {
                    @Override
                    public void postExecute(JSONArray result) throws JSONException {
                        result.remove(result.length() - 1);

                        if (result.length() > 0) {
                            final JSONObject status = result.getJSONObject(0);

                            notAnalyzedLabel.setText(status.getString("message"));

                            listView.setVisibility(View.GONE);
                        }

                        Log.i("Trip Analysis", result.toString());
                    }
                });

                task.execute(url, "GET").get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            return;
        }

        final String url = API.tripBehavior + "/" + trip_uuid;
        try {
            final API.doRequest task = new API.doRequest(new API.doRequest.TaskListener() {
                @Override
                public void postExecute(JSONArray result) throws JSONException {
                    result.remove(result.length() - 1);

                    for (int i = 0; i < result.length(); i++) {
                        final Trip tempTripData = new Trip(result.getJSONObject(i));
                        stats.add(tempTripData);
                    }

                    if (stats.size() > 0) {
                        notAnalyzedLabel.setVisibility(View.GONE);

                        final Trip stat = stats.get(0);
                        trip = stat;

                        startLoc = new LatLng(stat.start_latitude, stat.start_longitude);
                        endLoc = new LatLng(stat.end_latitude, stat.end_longitude);

                        buildBehaviorData();

                        final TripDetailsDataAdapter adapter = new TripDetailsDataAdapter(getApplicationContext(), behaviors);
                        listView.setAdapter(adapter);

                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapter, View view, int position, long arg) {
                                if (behaviorPolylines.size() > 0) {
                                    for (int i = 0; i < behaviorPolylines.size(); i++) {
                                        behaviorPolylines.get(i).remove();
                                    }

                                    behaviorPolylines = new ArrayList<Polyline>();
                                }

                                String behaviorName = (String) behaviors.keySet().toArray()[position];
                                ArrayList<ArrayList<LatLng>> occurences = (ArrayList<ArrayList<LatLng>>) behaviors.get(behaviorName);

                                for (int i = 0; i < occurences.size(); i++) {
                                    ArrayList<LatLng> temp = occurences.get(i);
                                    LatLng[] tempLatLngs = temp.toArray(new LatLng[temp.size()]);

                                    Polyline polyline = mMap.addPolyline(new PolylineOptions()
                                            .add(tempLatLngs)
                                            .width(20)
                                            .color(Color.RED));

                                    behaviorPolylines.add(polyline);
                                }

                            }
                        });
                    }

                    Log.i("Trip Analysis", result.toString());
                }
            });

            task.execute(url, "GET").get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void buildBehaviorData() {
        TripLocation[] locations = trip.locations;

        if (locations != null && locations.length > 0) {
            for (int i = 0; i < locations.length; i++) {
                TripLocation location = locations[i];

                TripBehavior[] tripBehaviors = location.behaviors;

                if (tripBehaviors != null) {
                    for (int j = 0; j < tripBehaviors.length; j++) {
                        TripBehavior behavior = tripBehaviors[j];

                        ArrayList<ArrayList<LatLng>> array;
                        // TODO

                        if (behaviors.get(behavior.behavior_name) == null) {
                            array = new ArrayList<>();
                            behaviors.put(behavior.behavior_name, array);
                        } else {
                            array = (ArrayList<ArrayList<LatLng>>) behaviors.get(behavior.behavior_name);
                        }

                        final ArrayList<LatLng> coordinateArray = new ArrayList<>();
                        coordinateArray.add(new LatLng(behavior.start_latitude, behavior.start_longitude));
                        coordinateArray.add(new LatLng(behavior.end_latitude, behavior.end_longitude));

                        array.add(coordinateArray);
                        behaviors.put(behavior.behavior_name, array);
                    }
                }
            }
        } else {
            listView.setVisibility(View.GONE);
            notAnalyzedLabel.setVisibility(View.VISIBLE);
        }
    }

    public static String dateToString(GregorianCalendar cal) {
        final SimpleDateFormat formattedCal = new SimpleDateFormat("MM/dd/yy, hh:mm a");
        formattedCal.setCalendar(cal);

        final String dateFormatted = formattedCal.format(cal.getTime());

        return dateFormatted;
    }
}
