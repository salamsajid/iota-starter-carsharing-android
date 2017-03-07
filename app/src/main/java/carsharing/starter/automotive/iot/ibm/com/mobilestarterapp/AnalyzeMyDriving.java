/**
 * Copyright 2016 IBM Corp. All Rights Reserved.
 * <p>
 * Licensed under the IBM License, a copy of which may be obtained at:
 * <p>
 * http://www14.software.ibm.com/cgi-bin/weblap/lap.pl?li_formnum=L-DDIN-AEGGZJ&popup=y&title=IBM%20IoT%20for%20Automotive%20Sample%20Starter%20Apps%20%28Android-Mobile%20and%20Server-all%29
 * <p>
 * You may not use this file except in compliance with the license.
 */
package carsharing.starter.automotive.iot.ibm.com.mobilestarterapp;

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
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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
import com.google.gson.JsonObject;
import com.ibm.iotf.client.device.DeviceClient;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;
import java.util.TimeZone;
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
    protected static final String[] reservationId = {new String()}; // Needs to be final to be able to access and change it inside the API.doRequest callback function

    protected static boolean userUnlocked = false;
    protected static boolean startedDriving = false;
    protected static boolean alreadyReserved = false;

    private int tripCount = 0;

    private DeviceClient deviceClient;

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

    private String speedMessage = "";
    private int transmissionCount = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.activity_analyze_my_driving, container, false);

        final SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        final AppCompatActivity activity = (AppCompatActivity) getActivity();
        locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        provider = locationManager.getBestProvider(new Criteria(), false);

        final ActionBar supportActionBar = activity.getSupportActionBar();
        supportActionBar.setDisplayHomeAsUpEnabled(true);
        supportActionBar.setTitle("Setting up...");

        startDriving = (ImageButton) view.findViewById(R.id.imageButton);
        startDriving.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.i("Button Clicked", "Start Driving");

                if (!startedDriving) {
                    supportActionBar.setTitle("Preparing for the trip...");
                    API.runInAsyncUIThread(new Runnable() {
                        @Override
                        public void run() {
                            if (startDrive(deviceID)) {
                                AnalyzeMyDriving.this.getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                                reserveCar();
                                startDriving.setImageResource(R.drawable.enddriving);
                                transmissionCount = 0;
                                supportActionBar.setTitle("Please start driving safely.");
                            } else {
                                Toast toast = Toast.makeText(activity.getApplicationContext(), "Failed to connect to IoT Platform.", Toast.LENGTH_SHORT);
                                toast.show();

                                supportActionBar.setTitle("Check server connection.");
                            }
                        }
                    }, activity);
                } else {
                    startedDriving = false;
                    supportActionBar.setTitle("Completing the trip...");
                    API.runInAsyncUIThread(new Runnable() {
                        @Override
                        public void run() {
                            completeReservation(reservationId[0], false);
                            startDriving.setImageResource(R.drawable.startdriving);
                            AnalyzeMyDriving.this.getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                        }
                    }, activity);
                }
            }
        });

        this.view = view;
        return view;
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
        if (this.view != null) {
            getLocation(this.view);
        }
    }

    public boolean startDrive(final String deviceId) {
        if (deviceClient == null) {
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

    public void stopDrive(final String deviceId) {
        if (reservationForMyDevice(deviceId)) {
            userUnlocked = false;
        }
    }

    public void completeDrive(final String deviceId) {
        if (reservationForMyDevice(deviceId)) {
            tripID = null;  // clear the tripID
        }
    }

    public static String getTripId(final String deviceId) {
        if (reservationForMyDevice(deviceId)) {
            return tripID;
        }
        return null;
    }

    public static boolean reservationForMyDevice(final String deviceId) {
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
        requestLocationUpdates(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        requestLocationUpdates(false);
    }

    private void requestLocationUpdates(final boolean request) {
        if (!request && locationManager != null) {
            locationManager.removeUpdates(this);
        }
        final FragmentActivity activity = getActivity();
        if (activity == null) {
            return;
        }
        if (ActivityCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (request && locationManager != null) {
            locationManager.requestLocationUpdates(provider, 2000, 1.0f, this);
        }
    }


    @Override
    public void onLocationChanged(Location location) {
//        Log.i("Location Data", "New Location - " + location.getLatitude() + ", " +  location.getLongitude());
        getAccurateLocation(mMap);
    }

    private void getAccurateLocation(final GoogleMap googleMap) {
        final FragmentActivity activity = getActivity();
        if (activity == null) {
            Log.e("getAccurateLocation", "do nothing as getActivity()==null");
            return;
        }
        final ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        final boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        final boolean networkConnected = networkInfo != null && networkInfo.isConnected();
        if (gpsEnabled && networkConnected) {
            if (!checkCurrentLocation()) {
                Log.e("Location Data", "Not Working!");
//                Toast.makeText(getActivity().getApplicationContext(), "Please activate your location settings and restart the application!", Toast.LENGTH_LONG).show();
                getAccurateLocation(mMap);
                return;
            }
            if (mMap != null) {
                float zoom = mMap.getCameraPosition().zoom;
                zoom = Math.max(14, zoom);
                zoom = Math.min(18, zoom);


                mMap.clear();
                final LatLng newLocation = new LatLng(location.getLatitude(), location.getLongitude());
//                if (!cameraSet) {
                mMap.moveCamera(CameraUpdateFactory.newLatLng(newLocation));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(zoom), 2000, null);
//                    cameraSet = true;
//                }
                mMap.addMarker(new MarkerOptions()
                        .position(newLocation).title("Your Location")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.models)));
            }
            final ActionBar supportActionBar = ((AppCompatActivity) activity).getSupportActionBar();
            if (startedDriving) {
                if (!behaviorDemo) {
                    // get credentials may be failed
                    startedDriving = false;
                    completeReservation(reservationId[0], false);
                    return;
                }
                speedMessage = "" + Math.round(location.getSpeed() * 60 * 60 / 16.0934) / 100.0 + " MPH";
                supportActionBar.setTitle(speedMessage + " - Data not sent");

                tripCount += 1;
                if (tripCount % 10 == 0) {
//                renderMapMatchedLocation()
                }
            } else if (deviceClient != null) {
                supportActionBar.setTitle("Press Start Driving when ready.");
            }
            if (behaviorDemo) {
                API.runInAsyncUIThread(new Runnable() {
                    @Override
                    public void run() {
                        if (deviceClient == null && needCredentials) {
                            createDeviceClient();
                            connectDeviceClient();
                        }
                        if (userUnlocked) {
                            sendLocation(location);
                        }
                    }
                }, activity);
            }
        } else {
            if (!gpsEnabled) {
                Toast.makeText(activity.getApplicationContext(), "Please turn on your GPS", Toast.LENGTH_LONG).show();

                final Intent gpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(gpsIntent, GPS_INTENT);

                if (!networkConnected) {
                    networkIntentNeeded = true;
                }
            } else if (!networkConnected) {
                Toast.makeText(activity.getApplicationContext(), "Please turn on Mobile Data or WIFI", Toast.LENGTH_LONG).show();

                final Intent settingsIntent = new Intent(Settings.ACTION_SETTINGS);
                startActivityForResult(settingsIntent, SETTINGS_INTENT);
            }
        }
    }

    private boolean checkCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        locationManager = (LocationManager) getActivity().getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        final List<String> providers = locationManager.getProviders(true);
        Location finalLocation = null;

        for (final String provider : providers) {
            final Location lastKnown = locationManager.getLastKnownLocation(provider);
            if (lastKnown == null) {
                continue;
            }
            if (finalLocation == null || (lastKnown.getAccuracy() < finalLocation.getAccuracy())) {
                finalLocation = lastKnown;
            }
        }
        location = finalLocation;
        return location != null;
    }

    private void createDeviceClient() {
        final String url = API.credentials + "/" + FirstPage.mobileAppDeviceId + "?owneronly=true";
        try {
            API.doRequest task = new API.doRequest(new API.doRequest.TaskListener() {
                @Override
                public void postExecute(JSONArray result) throws JSONException, MqttException {
                    result.remove(result.length() - 1);

                    final FragmentActivity activity = getActivity();
                    final ActionBar supportActionBar = ((AppCompatActivity) activity).getSupportActionBar();
                    if (deviceClient != null) {
                        // already got credentials
                        return;
                    }
                    if (result.length() == 0) {
                        Toast.makeText(activity.getApplicationContext(), "MQTT - Failed to get credentials. You may have exceeded the free plan limit.", Toast.LENGTH_LONG).show();
                        supportActionBar.setTitle("An error occurred.");
                        behaviorDemo = false;
                        return;
                    }
                    supportActionBar.setTitle("Press Start Driving when ready.");

                    final JSONObject deviceCredentials = result.getJSONObject(0);
                    try {
                        final Properties options = new Properties();
                        options.setProperty("org", deviceCredentials.getString("org"));
                        options.setProperty("type", deviceCredentials.getString("deviceType"));
                        options.setProperty("id", deviceCredentials.getString("deviceId"));
                        options.setProperty("auth-method", "token");
                        options.setProperty("auth-token", deviceCredentials.getString("token"));
                        deviceClient = new DeviceClient(options);
                    } catch (MqttException me) {
                        me.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
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

    // return true if connection exists
    private boolean connectDeviceClient() {
        if (deviceClient == null) {
            return false;
        }
        if (!deviceClient.isConnected()) {
            try {
                deviceClient.connect();
                Log.d("MQTT", "Connected");
                return true;
            } catch (MqttException e) {
                e.printStackTrace();
                Log.d("MQTT", "Failed to connect");
                return false;
            }
        } else {
            return true;
        }
    }

    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (requestCode == GPS_INTENT) {
            if (networkIntentNeeded) {
                Toast.makeText(getActivity().getApplicationContext(), "Please connect to a network", Toast.LENGTH_LONG).show();

                final Intent settingsIntent = new Intent(Settings.ACTION_SETTINGS);
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

    public void getLocation(final View view) {
        final FragmentActivity activity = getActivity();
        if (activity == null) {
            return;
        }
        if (ActivityCompat.checkSelfPermission(activity.getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity.getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        final Location location = locationManager.getLastKnownLocation(provider);
        onLocationChanged(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    public void sendLocation(final Location location) {
        if (connectDeviceClient()) {
            final GregorianCalendar cal = new GregorianCalendar();
            final TimeZone gmt = TimeZone.getTimeZone("GMT");
            cal.setTimeZone(gmt);
            final SimpleDateFormat formattedCal = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            formattedCal.setCalendar(cal);
            final String timestamp = formattedCal.format(cal.getTime());

            final double speed = Math.max(0.0, location.getSpeed() * 60 * 60 / 1000);
            final double longitude = location.getLongitude();
            final double latitude = location.getLatitude();
            final String mobileAppDeviceId = FirstPage.mobileAppDeviceId;
            final String status = tripID != null ? "Unlocked" : "Locked";

            if (tripID == null) {
                // this trip should be completed, so lock device now
                userUnlocked = false;
            }

            final JsonObject event = new JsonObject();
            final JsonObject data = new JsonObject();
            event.add("d", data);
            data.addProperty("trip_id", tripID);
            data.addProperty("speed", speed);
            data.addProperty("lng", longitude);
            data.addProperty("lat", latitude);
            data.addProperty("ts", timestamp);
            data.addProperty("id", mobileAppDeviceId);
            data.addProperty("status", status);

            final ActionBar supportActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (deviceClient.publishEvent("sensorData", event, 0)) {
                Log.d("MQTT", "publish event " + event.toString());
                supportActionBar.setTitle(speedMessage + " - Data sent (" + (++transmissionCount) + ")");
            } else {
                Log.d("MQTT", "ERROR in publishing event " + event.toString());
                supportActionBar.setTitle("Data Transmission Error.");
            }
        }
    }

    public String jsonToString(ArrayList<ArrayList<String>> data) {
        String temp = "{\"d\":{";
        int accum = 0;

        for (int i = 0; i < data.size(); i++) {
            if (accum == (data.size() - 1)) {
                temp += "\"" + data.get(i).get(0) + "\": \"" + data.get(i).get(1) + "\"}}";
            } else {
                temp += "\"" + data.get(i).get(0) + "\": \"" + data.get(i).get(1) + "\", ";
            }

            accum += 1;
        }

        return temp;
    }

    public void completeReservation(final String resId, final boolean alreadyTaken) {
        final String url = API.reservation + "/" + resId;

        try {
            final API.doRequest task = new API.doRequest(new API.doRequest.TaskListener() {
                @Override
                public void postExecute(JSONArray result) throws JSONException {
                    final JSONObject serverResponse = result.getJSONObject(result.length() - 1);
                    final int statusCode = serverResponse.getInt("statusCode");

                    result.remove(result.length() - 1);

                    String title = "";
                    String message = "";

                    switch (statusCode) {
                        case 200:
                            title = "Trip completed.";
                            message = "Please allow at least 30 minutes for the driver behavior data to be analyzed";
                            reservationId[0] = null;

                            break;
                        default:
                            title = "Something went wrong.";
                    }

                    final AppCompatActivity activity = (AppCompatActivity) getActivity();
                    activity.getSupportActionBar().setTitle(title);

                    if (!alreadyTaken) {
                        Toast toast = Toast.makeText(activity.getApplicationContext(), message, Toast.LENGTH_SHORT);
                        toast.show();
                    }

                    Log.i("Complete Reservation", result.toString());
                }
            });

            final String trip_id = getTripId(deviceID);

            final JSONObject bodyObject = new JSONObject();
            bodyObject.put("status", "close");

            if (trip_id != null) {
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
        final String url = API.reservation;
        final GregorianCalendar temp = new GregorianCalendar();
        final long pickupTime = temp.getTimeInMillis() / 1000;
        final long dropoffTime = (temp.getTimeInMillis() / 1000) + 3600;
        final Uri.Builder builder = new Uri.Builder()
                .appendQueryParameter("carId", deviceID)
                .appendQueryParameter("pickupTime", pickupTime + "")
                .appendQueryParameter("dropOffTime", dropoffTime + "");
        final String query = builder.build().getEncodedQuery();

        try {
            final API.doRequest task = new API.doRequest(new API.doRequest.TaskListener() {
                @Override
                public void postExecute(JSONArray result) throws JSONException {
                    final JSONObject serverResponse = result.getJSONObject(result.length() - 1);
                    final int statusCode = serverResponse.getInt("statusCode");

                    result.remove(result.length() - 1);
                    switch (statusCode) {
                        case 200:
                            // start driving
                            startedDriving = true;
                            reservationId[0] = result.getJSONObject(0).getString("reservationId");
                            Reservations.userReserved = true;
                            Log.i("Reservation", "Made=" + result.toString());

                            getAccurateLocation(mMap);

                            break;
                        case 409:
                            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Car already taken.");
                            Log.i("Reservation", "Already Exists=" + result.toString());
                            useExistingReservation();

                            break;
                        case 404:
                            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Car not available.");
                            Log.i("Reservation", "Not Made" + result.toString());

                            break;
                        default:
                            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Something went wrong.");
                            Log.i("Reservation", "Error" + result.toString());
                    }
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
        final String url = API.reservations;

        try {
            final API.doRequest task = new API.doRequest(new API.doRequest.TaskListener() {
                @Override
                public void postExecute(JSONArray result) throws JSONException {
                    result.remove(result.length() - 1);
                    for (int i = 0; i < result.length(); i++) {
                        final ReservationsData reservationData = new ReservationsData(result.getJSONObject(i));

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
