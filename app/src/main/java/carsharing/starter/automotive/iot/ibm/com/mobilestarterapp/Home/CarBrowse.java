/**
 * Copyright 2016 IBM Corp. All Rights Reserved.
 * <p>
 * Licensed under the IBM License, a copy of which may be obtained at:
 * <p>
 * http://www14.software.ibm.com/cgi-bin/weblap/lap.pl?li_formnum=L-DDIN-AEGGZJ&popup=y&title=IBM%20IoT%20for%20Automotive%20Sample%20Starter%20Apps%20%28Android-Mobile%20and%20Server-all%29
 * <p>
 * You may not use this file except in compliance with the license.
 */
package carsharing.starter.automotive.iot.ibm.com.mobilestarterapp.Home;

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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import carsharing.starter.automotive.iot.ibm.com.mobilestarterapp.ConnectedDriverAPI.API;
import carsharing.starter.automotive.iot.ibm.com.mobilestarterapp.ConnectedDriverAPI.CarData;
import carsharing.starter.automotive.iot.ibm.com.mobilestarterapp.R;

public class CarBrowse extends Fragment implements OnMapReadyCallback, LocationListener {
    private GoogleMap mMap;
    private GoogleApiClient client;

    final String[] pickerData = {
            "Current Location",
            "Tokyo, Japan",
            "MGM Grand, Las Vegas",
            "Mandalay Bay, Las Vegas",
            "Hellabrunn Zoo, Munich, Germany",
            "Nymphenburg Palace, Munich, Germany"
    };

    // Latitude - Longitude
    final Double locationData[][] = {
            {0.0, 0.0},
            {35.709026, 139.731992},
            {36.102118, -115.165571},
            {36.090754, -115.176670},
            {48.0993, 11.55848},
            {48.176656, 11.553583}
    };

    private LocationManager locationManager;
    private Location location;
    private String provider;

    private final int GPS_INTENT = 000;
    private final int SETTINGS_INTENT = 001;

    private boolean networkIntentNeeded = false;

    static private Location simulatedLocation = null;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.activity_car_browse, container, false);

        final SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        provider = locationManager.getBestProvider(new Criteria(), false);

        return view;
    }

    public void getCars(final Location location) {
        final String url = API.carsNearby + "/" + location.getLatitude() + "/" + location.getLongitude();

        try {
            final API.doRequest task = new API.doRequest(new API.doRequest.TaskListener() {
                @Override
                public void postExecute(JSONArray result) throws JSONException {
                    JSONObject serverResponse = result.getJSONObject(result.length() - 1);
                    int statusCode = serverResponse.getInt("statusCode");

                    result.remove(result.length() - 1);

                    final FragmentActivity activity = getActivity();
                    final ActionBar supportActionBar = ((AppCompatActivity) activity).getSupportActionBar();
                    if (statusCode == 200) {
                        final ListView listView = (ListView) getView().findViewById(R.id.listView);

                        final ArrayList<CarData> carsArray = new ArrayList<CarData>();

                        for (int i = 0; i < result.length(); i++) {
                            final CarData tempCarData = new CarData(result.getJSONObject(i));
                            carsArray.add(tempCarData);

                            final LatLng carLocation = new LatLng(tempCarData.lat, tempCarData.lng);
                            mMap.addMarker(new MarkerOptions()
                                    .position(carLocation)
                                    .title(tempCarData.title)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.models)));
                        }

                        Collections.sort(carsArray, new Comparator<CarData>() {
                            @Override
                            public int compare(CarData lhs, CarData rhs) {
                                return lhs.distance - rhs.distance;
                            }
                        });

                        final CarDataAdapter adapter = new CarDataAdapter(activity.getApplicationContext(), carsArray);
                        listView.setAdapter(adapter);

                        final ArrayList<CarData> finalCarsArray = carsArray;

                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapter, View view, int position, long arg) {
                                final Intent carDetails = new Intent(view.getContext(), CarDetails.class);

                                final Bundle bundle = new Bundle();
                                bundle.putSerializable("carData", finalCarsArray.get(position));
                                carDetails.putExtras(bundle);

                                startActivity(carDetails);
                            }
                        });

                        if (carsArray.size() == 0) {
                            final Toast toast = Toast.makeText(activity.getApplicationContext(), "No car available in this area. Maybe Free Plan limitation and you may ask administrator to delete registered cars.", Toast.LENGTH_LONG);
                            toast.show();
                            supportActionBar.setTitle("No car available.");
                        } else {
                            final String title = (carsArray.size() == 1) ? "1 car found." : carsArray.size() + " cars found.";
                            supportActionBar.setTitle(title);
                        }

                        Log.i("Car Data", result.toString());
                    } else if (statusCode == 500) {
                        final Toast toast = Toast.makeText(activity.getApplicationContext(), "A server internal error received. Ask your administrator.", Toast.LENGTH_LONG);
                        toast.show();
                        supportActionBar.setTitle("Error: Server internal error.");
                    } else {
                        final Toast toast = Toast.makeText(activity.getApplicationContext(), "Error: Unable to connect to server.", Toast.LENGTH_LONG);
                        toast.show();
                        supportActionBar.setTitle("Error: Not connected to server.");
                    }
                }
            });

            task.execute(url, "GET").get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(final LatLng latLng) {
                //System.out.println("MAP LONG CLICK " + latLng);

                final Location location = new Location("NoProvider");
                location.setLatitude(latLng.latitude);
                location.setLongitude(latLng.longitude);
                location.setTime(new Date().getTime());

                String title = "";
                if (simulatedLocation == null) {
                    simulatedLocation = location;
                    title = "This location is used next.";
                } else {
                    simulatedLocation = null;
                    title = "Real location is used next.";
                }
                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title);

                final Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Long tap toggles location setting. Go back main menu and come again for the new location!", Toast.LENGTH_LONG);
                toast.show();
            }
        });

        getAccurateLocation(mMap);
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
        if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        locationManager.requestLocationUpdates(provider, 2000, 1.0f, this);
    }

    @Override
    public void onPause() {
        super.onPause();

        locationManager.removeUpdates(this);
        if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
    }

    @Override
    public void onLocationChanged(final Location location) {
        Log.i("Location Data", "New Location - " + location.getLatitude() + ", " + location.getLongitude());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public void getLocation(final View view) {
        if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        final Location location = locationManager.getLastKnownLocation(provider);

        onLocationChanged(location);
    }

    private void getAccurateLocation(final GoogleMap googleMap) {
        final FragmentActivity activity = getActivity();
        ((AppCompatActivity) activity).getSupportActionBar().setTitle("Searching cars...");
        API.runInAsyncUIThread(new Runnable() {
            @Override
            public void run() {
                getAccurateLocation2(googleMap);
            }
        }, activity);

    }

    private void getAccurateLocation2(final GoogleMap googleMap) {
        final ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && (networkInfo != null && networkInfo.isConnected())) {
            if (simulatedLocation == null) {
                if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }

                locationManager = (LocationManager) getActivity().getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

                final List<String> providers = locationManager.getProviders(true);
                Location finalLocation = null;

                for (String provider : providers) {
                    if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }

                    final Location lastKnown = locationManager.getLastKnownLocation(provider);

                    if (lastKnown == null) {
                        continue;
                    }

                    if (finalLocation == null || (lastKnown.getAccuracy() < finalLocation.getAccuracy())) {
                        finalLocation = lastKnown;
                    }
                }

                location = finalLocation;
            } else {
                location = simulatedLocation;
            }
            if (location != null) {
                final LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());

                mMap.addMarker(new MarkerOptions().position(userLocation).title("Your Location"));

                mMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(14), 2000, null);

                getCars(location);
            } else {
                Log.e("Location Data", "Not Working!");

//                Toast.makeText(getActivity().getApplicationContext(), "Please activate your location settings and restart the application!", Toast.LENGTH_LONG).show();
                getAccurateLocation(mMap);
            }
        } else {
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Toast.makeText(getActivity().getApplicationContext(), "Please turn on your GPS", Toast.LENGTH_LONG).show();

                final Intent gpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(gpsIntent, GPS_INTENT);

                if (networkInfo == null) {
                    networkIntentNeeded = true;
                }
            } else {
                if (networkInfo == null) {
                    Toast.makeText(getActivity().getApplicationContext(), "Please turn on Mobile Data or WIFI", Toast.LENGTH_LONG).show();

                    final Intent settingsIntent = new Intent(Settings.ACTION_SETTINGS);
                    startActivityForResult(settingsIntent, SETTINGS_INTENT);
                }
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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
}