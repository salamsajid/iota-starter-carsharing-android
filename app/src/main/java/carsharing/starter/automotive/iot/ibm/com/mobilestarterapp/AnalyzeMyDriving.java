/**
 * Copyright 2016 IBM Corp. All Rights Reserved.
 *
 * Licensed under the IBM License, a copy of which may be obtained at:
 *
 * http://www14.software.ibm.com/cgi-bin/weblap/lap.pl?li_formnum=L-DDIN-AEGGZJ&popup=y&title=IBM%20IoT%20for%20Automotive%20Sample%20Starter%20Apps%20%28Android-Mobile%20and%20Server-all%29
 *
 * You may not use this file except in compliance with the license.
 */
package carsharing.starter.automotive.iot.ibm.com.mobilestarterapp;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import carsharing.starter.automotive.iot.ibm.com.mobilestarterapp.ConnectedDriverAPI.API;
import carsharing.starter.automotive.iot.ibm.com.mobilestarterapp.ConnectedDriverAPI.ReservationsData;
import carsharing.starter.automotive.iot.ibm.com.mobilestarterapp.Reservations.Reservations;

public class AnalyzeMyDriving extends Fragment implements OnMapReadyCallback, LocationListener {
    protected static boolean behaviorDemo = false;
    protected static boolean needCredentials = false;

    protected static String tripID = null;
    protected static String deviceID = FirstPage.mobileAppDeviceId;
    protected static final String[] reservationId = { new String() }; // Needs to be final to be able to access and change it inside the API.doRequest callback function

    protected static boolean userUnlocked = false;
    protected static boolean startedDriving = false;
    protected static boolean alreadyReserved = false;

    private int tripCount = 0;

    protected static MqttAsyncClient mqtt;
    private static MqttConnectOptions options = new MqttConnectOptions();
    private static MemoryPersistence persistence = new MemoryPersistence();

    private GoogleMap mMap;
    private GoogleApiClient client;

    private boolean cameraSet = false;

    private ImageButton startDriving;

    LocationManager locationManager;
    Location location;
    String provider;

    private View view = null;

    private final int GPS_INTENT = 000;
    private final int SETTINGS_INTENT = 001;

    private boolean networkIntentNeeded = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_analyze_my_driving, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        provider = locationManager.getBestProvider(new Criteria(), false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Setting Up...");

        startDriving = (ImageButton) view.findViewById(R.id.imageButton);

        startDriving.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.i("Button Clicked", "Start Driving");

                if (!startedDriving) {
                    if(startDrive(deviceID)){
                        reserveCar();

                        startDriving.setImageResource(R.drawable.enddriving);
                    }else{
                        Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Failed to connect to IoT Platform", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                } else {
                    startedDriving = false;
                    completeReservation(reservationId[0], false);

                    startDriving.setImageResource(R.drawable.startdriving);
                }
            }
        });

        this.view = view;

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (this.view != null) {
            getLocation(this.view);
        }
    }

    public boolean startDrive(String deviceId) {
        if (mqtt == null) {
            return false;
        }

        if (reservationForMyDevice(deviceId)) {
            userUnlocked = true;

            if (tripID == null) {
                tripID = UUID.randomUUID().toString();
            }
        }

        return true;
    }

    public void stopDrive(String deviceId) {
        if (reservationForMyDevice(deviceId)) {
            userUnlocked = false;
        }
    }

    public void completeDrive(String deviceId) {
        if (reservationForMyDevice(deviceId)) {
            tripID = null;  // clear the tripID
        }
    }

    public static String getTripId(String deviceId) {
        if (reservationForMyDevice(deviceId)) {
            return tripID;
        }

        return null;
    }

    public static boolean reservationForMyDevice(String deviceId) {
        return behaviorDemo && deviceId == FirstPage.mobileAppDeviceId;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (client != null) {
            client.connect();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        locationManager.requestLocationUpdates(provider, 500, 1, this);
    }

    @Override
    public void onPause() {
        super.onPause();


        if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
//        Log.i("Location Data", "New Location - " + location.getLatitude() + ", " +  location.getLongitude());

        getAccurateLocation(mMap);
    }

    private void getAccurateLocation(GoogleMap googleMap) {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && (networkInfo != null && networkInfo.isConnected())) {
            if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            locationManager = (LocationManager) getActivity().getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

            List<String> providers = locationManager.getProviders(true);
            Location finalLocation = null;

            for (String provider : providers) {
                if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }

                Location lastKnown = locationManager.getLastKnownLocation(provider);

                if (lastKnown == null) {
                    continue;
                }

                if (finalLocation == null || (lastKnown.getAccuracy() < finalLocation.getAccuracy())) {
                    finalLocation = lastKnown;
                }
            }

            location = finalLocation;

            if (location != null) {
                mMap.clear();

                LatLng newLocation = new LatLng(location.getLatitude(), location.getLongitude());

//                if (!cameraSet) {
                mMap.moveCamera(CameraUpdateFactory.newLatLng(newLocation));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);

//                    cameraSet = true;
//                }

                mMap.addMarker(new MarkerOptions()
                        .position(newLocation).title("Your Location")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.models)));

                if (startedDriving) {
                    if(!behaviorDemo){
                        // get credentials may be failed
                        startedDriving = false;
                        completeReservation(reservationId[0], false);

                        return;
                    }

                    ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Speed " + Math.round(location.getSpeed() * 60 * 60 / 16.0934)/100.0 + " MPH" );

                    tripCount += 1;
                    if(tripCount % 10 == 0) {
//                renderMapMatchedLocation()
                    }
                }


                if (behaviorDemo) {
                    if (mqtt == null && needCredentials) {
                        String url = API.credentials + "/" + FirstPage.mobileAppDeviceId + "?owneronly=true";

                        try {
                            API.doRequest task = new API.doRequest(new API.doRequest.TaskListener() {
                                @Override
                                public void postExecute(JSONArray result) throws JSONException, MqttException {
                                    result.remove(result.length() - 1);

                                    if (mqtt != null) {
                                        // already got credentials
                                        return;
                                    }
                                    if (result.length() == 0) {
                                        Toast.makeText(getActivity().getApplicationContext(), "MQTT - Failed to get credentials. You may have exceeded the free plan limit.", Toast.LENGTH_LONG).show();

                                        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("An Error Occured");

                                        behaviorDemo = false;

                                        return;
                                    }

                                    ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Press Start Driving when ready");

                                    JSONObject deviceCredentials = result.getJSONObject(0);

                                    Log.i("MQTT", "calling mqttsettings");

                                    final String clientIdPid = "d:" + deviceCredentials.getString("org") + ":" + deviceCredentials.getString("deviceType") + ":" + deviceCredentials.getString("deviceId");
                                    final String broker       = "wss://" + deviceCredentials.getString("org") + ".messaging.internetofthings.ibmcloud.com:443";
                                    MemoryPersistence persistence = new MemoryPersistence();

                                    try {
                                        mqtt = new MqttAsyncClient(broker, clientIdPid, persistence);

                                        options.setCleanSession(true);
                                        options.setUserName("use-token-auth");
                                        options.setPassword(deviceCredentials.getString("token").toCharArray());
                                        options.setKeepAliveInterval(90);

                                        Log.i("MQTT", "Connecting to broker: " + broker);
                                        mqtt.connect(options);

                                        Log.i("MQTT", "Connected");
                                    } catch(MqttException me) {
                                        Log.e("Reason", me.getReasonCode() + "");
                                        Log.e("Message", me.getMessage());
                                        Log.e("Localized Message", me.getLocalizedMessage());
                                        Log.e("Cause", me.getCause() + "");
                                        Log.e("Exception", me + "");

                                        me.printStackTrace();
                                    }

                                    Log.i("Credentials Data", result.toString());

                                    needCredentials = false;
                                }
                            });

                            task.execute(url, "GET").get();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                    }

                    if (mqtt != null && userUnlocked) {
                        if (!mqtt.isConnected()) {
                            try {
                                mqtt.connect(options);
                            } catch (MqttException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Log.d("MQTT", "Connected - Publishing NOW");

                            try {
                                sendLocation(location);
                            } catch (MqttException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            } else {
                Log.e("Location Data", "Not Working!");

//                Toast.makeText(getActivity().getApplicationContext(), "Please activate your location settings and restart the application!", Toast.LENGTH_LONG).show();
                getAccurateLocation(mMap);
            }
        } else {
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Toast.makeText(getActivity().getApplicationContext(), "Please turn on your GPS", Toast.LENGTH_LONG).show();

                Intent gpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(gpsIntent, GPS_INTENT);

                if (networkInfo == null) {
                    networkIntentNeeded = true;
                }
            } else {
                if (networkInfo == null) {
                    Toast.makeText(getActivity().getApplicationContext(), "Please turn on Mobile Data or WIFI", Toast.LENGTH_LONG).show();

                    Intent settingsIntent = new Intent(Settings.ACTION_SETTINGS);
                    startActivityForResult(settingsIntent, SETTINGS_INTENT);
                }
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GPS_INTENT) {
            if (networkIntentNeeded) {
                Toast.makeText(getActivity().getApplicationContext(), "Please connect to a network", Toast.LENGTH_LONG).show();

                Intent settingsIntent = new Intent(Settings.ACTION_SETTINGS);
                startActivityForResult(settingsIntent, SETTINGS_INTENT);
            } else {
                getAccurateLocation(mMap);
            }
        } else if (requestCode == SETTINGS_INTENT) {
            networkIntentNeeded = false;

            getAccurateLocation(mMap);
        }

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public void getLocation(View view) {
        if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Location location = locationManager.getLastKnownLocation(provider);

        onLocationChanged(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    public void sendLocation(Location location) throws MqttException {
        if(mqtt == null || !mqtt.isConnected()){
            return;
        }

        GregorianCalendar cal = new GregorianCalendar();
        SimpleDateFormat formattedCal = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        formattedCal.setCalendar(cal);

        String dateFormatted = formattedCal.format(cal.getTime());

        ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();
        data.add(new ArrayList<String>(Arrays.asList("speed", Math.max(0.0, location.getSpeed() * 60 * 60 / 1000) + "")));
        data.add(new ArrayList<String>(Arrays.asList("lng", location.getLongitude() + "")));
        data.add(new ArrayList<String>(Arrays.asList("lat", location.getLatitude() + "")));
        data.add(new ArrayList<String>(Arrays.asList("ts", dateFormatted)));
        data.add(new ArrayList<String>(Arrays.asList("id", FirstPage.mobileAppDeviceId)));
        data.add(new ArrayList<String>(Arrays.asList("status", tripID != null ? "Unlocked" : "Locked")));

        if(tripID != null){
            data.add(new ArrayList<String>(Arrays.asList("trip_id", tripID)));
        }else{
            // this trip should be completed, so lock device now
            userUnlocked = false;
        }

        String stringData = jsonToString(data);
        MqttMessage message = new MqttMessage(stringData.getBytes());

        mqtt.publish("iot-2/evt/sensorData/fmt/json", message);
    }

    public String jsonToString(ArrayList<ArrayList<String>> data) {
        String temp = "{\"d\":{";
        int accum = 0;

        for (int i=0; i < data.size(); i++) {
            if (accum == (data.size() - 1)) {
                temp += "\"" + data.get(i).get(0) + "\": \"" + data.get(i).get(1) + "\"}}";
            } else {
                temp += "\"" + data.get(i).get(0) + "\": \"" + data.get(i).get(1) + "\", ";
            }

            accum += 1;
        }

        return temp;
    }

    public void completeReservation(String resId, final boolean alreadyTaken) {
        String url = API.reservation + "/" + resId;

        try {
            API.doRequest task = new API.doRequest(new API.doRequest.TaskListener() {
                @Override
                public void postExecute(JSONArray result) throws JSONException {
                    JSONObject serverResponse = result.getJSONObject(result.length() - 1);
                    int statusCode = serverResponse.getInt("statusCode");

                    result.remove(result.length() - 1);

                    String title = "";
                    String message = "";

                    switch (statusCode) {
                        case 200:
                            title = "Drive completed";
                            message = "Please allow at least 30 minutes for the driver behavior data to be analyzed";
                            reservationId[0] = null;

                            break;
                        default:
                            title = "Something went wrong.";
                    }

                    ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title);

                    if (!alreadyTaken) {
                        Toast toast = Toast.makeText(((AppCompatActivity) getActivity()).getApplicationContext(), message, Toast.LENGTH_SHORT);
                        toast.show();
                    }

                    Log.i("Complete Reservation", result.toString());
                }
            });

            String trip_id = getTripId(deviceID);

            JSONObject bodyObject = new JSONObject();
            bodyObject.put("status", "close");

            if(trip_id != null){
                // bind this trip to this reservation
                bodyObject.put("trip_id", trip_id);
            }

            completeDrive(deviceID);

            task.execute(url, "PUT", null, bodyObject.toString()).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void cancelReservation(String resId) {
        String url = API.reservation + "/" + resId;

        try {
            API.doRequest task = new API.doRequest(new API.doRequest.TaskListener() {
                @Override
                public void postExecute(JSONArray result) throws JSONException {
                    Log.i("Cancel Reservation", result.toString());
                }
            });

            task.execute(url, "DELETE").get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void reserveCar() {
        // reserve my device as a car
        String url = API.reservation;

        GregorianCalendar temp = new GregorianCalendar();

        long pickupTime = temp.getTimeInMillis() / 1000;
        long dropoffTime = (temp.getTimeInMillis() / 1000) + 3600;

        Uri.Builder builder = new Uri.Builder()
                .appendQueryParameter("carId", deviceID)
                .appendQueryParameter("pickupTime", pickupTime + "")
                .appendQueryParameter("dropOffTime", dropoffTime + "");

        String query = builder.build().getEncodedQuery();

        try {
            API.doRequest task = new API.doRequest(new API.doRequest.TaskListener() {
                @Override
                public void postExecute(JSONArray result) throws JSONException {
                    JSONObject serverResponse = result.getJSONObject(result.length() - 1);
                    int statusCode = serverResponse.getInt("statusCode");

                    result.remove(result.length() - 1);

                    switch (statusCode) {
                        case 200:
                            // start driving
                            startedDriving = true;
                            reservationId[0] = result.getJSONObject(0).getString("reservationId");
                            Reservations.userReserved = true;
                            try {
                                sendLocation(locationManager.getLastKnownLocation(provider));
                            } catch (MqttException e) {
                                e.printStackTrace();
                            } catch (SecurityException e) {
                                e.printStackTrace();
                            }
                            break;
                        case 409:
                            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Car already taken");
                            useExistingReservation();

                            break;
                        case 404:
                            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Car is not available");

                            break;
                        default:
                            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Something Went Wrong.");
                    }

                    Log.i("Reserve Data", result.toString());
                }
            });

            alreadyReserved = true;
            task.execute(url, "POST", query).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void useExistingReservation() {
        String url = API.reservations;

        try {
            API.doRequest task = new API.doRequest(new API.doRequest.TaskListener() {
                @Override
                public void postExecute(JSONArray result) throws JSONException {
                    result.remove(result.length() - 1);

                    ArrayList<ReservationsData> reservationsArray = new ArrayList<ReservationsData>();

                    for (int i=0; i < result.length(); i++) {
                        ReservationsData reservationData = new ReservationsData(result.getJSONObject(i));

                        if (reservationData.carId.equals(FirstPage.mobileAppDeviceId)) {
                            if (reservationData.status.equals("driving")) {
                                completeReservation(reservationData._id, true);
                                reserveCar();
                            } else {
                                startedDriving = true;
                                reservationId[0] = reservationData._id;
                            }
                        }
                    }

                    Log.i("Existing Reservation", result.toString());
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
