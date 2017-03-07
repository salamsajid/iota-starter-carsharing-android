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
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import carsharing.starter.automotive.iot.ibm.com.mobilestarterapp.ConnectedDriverAPI.API;
import carsharing.starter.automotive.iot.ibm.com.mobilestarterapp.QRCodeReader.SpecifyServer;

public class FirstPage extends AppCompatActivity {
    protected static String mobileAppDeviceId;

    private final int INITIAL_PERMISSIONS = 000;

    final Map<String, String> permissions = new HashMap<>();
    final ArrayList<String> permissionNeeded = new ArrayList<>();

    private boolean permissionsGranted = false;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_page);

        new API(getApplicationContext());
        mobileAppDeviceId = "d" + API.getUUID().substring(0, 30);

        getSupportActionBar().hide();

        API.doInitialize();

        checkPermissions();
    }

    public void checkPermissions() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED)
                permissions.put("internet", Manifest.permission.INTERNET);

            if (checkSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED)
                permissions.put("networkState", Manifest.permission.ACCESS_NETWORK_STATE);

            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                permissions.put("coarseLocation", Manifest.permission.ACCESS_COARSE_LOCATION);

            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                permissions.put("fineLocation", Manifest.permission.ACCESS_FINE_LOCATION);

            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                permissions.put("externalStorage", Manifest.permission.WRITE_EXTERNAL_STORAGE);

            for (Map.Entry<String, String> entry : permissions.entrySet()) {
                Log.e(entry.getKey(), entry.getValue() + "");

                permissionNeeded.add(entry.getValue());
            }

            if (permissionNeeded.size() > 0) {
                final Object[] tempObjectArray = permissionNeeded.toArray();
                final String[] permissionsArray = Arrays.copyOf(tempObjectArray, tempObjectArray.length, String[].class);

                requestPermissions(permissionsArray, INITIAL_PERMISSIONS);
            } else {
                permissionsGranted = true;
            }
        } else {
            if (!API.warningShown()) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder
                        .setTitle("Warning")
                        .setMessage("This app requires permissions to your Locations and Storage settings.\n\n" +
                                "If you are running the application to your phone from Android Studio, you will not be able to allow these permissions.\n\n" +
                                "If that is the case, please install the app through the provided APK file."
                        )
                        .setPositiveButton("Ok", null)
                        .show();
            }

            permissionsGranted();
        }
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, final String[] permissions, final int[] results) {
        switch (requestCode) {
            case INITIAL_PERMISSIONS:
                if (results[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionsGranted();
                } else {
                    Toast.makeText(getApplicationContext(), "Permissions Denied", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, results);
        }
    }

    public void permissionsGranted() {
        permissionsGranted = true;
    }

    public void needPermissions() {
        Toast.makeText(getApplicationContext(), "App needs permissions granted to let you use it!", Toast.LENGTH_SHORT).show();

        checkPermissions();
    }

    public void specifyServer(View view) {
        Log.i("Button Clicked", "Specify Server");

        if (permissionsGranted) {
            final Intent intent = new Intent(this, SpecifyServer.class);
            startActivity(intent);
        } else {
            needPermissions();
        }

    }

    public void simulateCarsharing(View view) {
        Log.i("Button Clicked", "Simulate Carsharing");

        if (permissionsGranted) {
            Intent intent = new Intent(this, tabNavigation.class);
            startActivity(intent);
        } else {
            needPermissions();
        }
    }

    public void analyzeMyDriving(final View view) throws IOException {
        Log.i("Button Clicked", "Analyze Driving");

        if (permissionsGranted) {
            if (!API.disclaimerShown(false)) {
                final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int choice) {
                        switch (choice) {
                            case DialogInterface.BUTTON_POSITIVE:
                                API.disclaimerShown(true);

                                final Intent intent = new Intent(view.getContext(), AnalyzeTabNavigation.class);
                                startActivity(intent);

                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                final Toast toast = Toast.makeText(view.getContext(), "Cannot use \"Analyze My Driving\" without agreeing to the disclaimer", Toast.LENGTH_SHORT);
                                toast.show();

                                break;
                        }
                    }
                };

                final InputStream is = getResources().getAssets().open("LICENSE");
                String line;
                final StringBuffer message = new StringBuffer();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                try {
                    br = new BufferedReader(new InputStreamReader(is));
                    while ((line = br.readLine()) != null) {
                        message.append(line + "\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (br != null) br.close();
                }

                final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder
                        .setTitle("Disclaimer")
                        .setMessage(message)
                        .setNegativeButton("Disagree", dialogClickListener)
                        .setPositiveButton("Agree", dialogClickListener)
                        .show();
            } else {
                final Intent intent = new Intent(view.getContext(), AnalyzeTabNavigation.class);
                startActivity(intent);
            }
        } else {
            needPermissions();
        }
    }
}