/**
 * Copyright 2016 IBM Corp. All Rights Reserved.
 *
 * Licensed under the IBM License, a copy of which may be obtained at:
 *
 * http://www14.software.ibm.com/cgi-bin/weblap/lap.pl?li_formnum=L-DDIN-AEGGZJ&popup=y&title=IBM%20IoT%20for%20Automotive%20Sample%20Starter%20Apps%20%28Android-Mobile%20and%20Server-all%29
 *
 * You may not use this file except in compliance with the license.
 */
package carsharing.starter.automotive.iot.ibm.com.mobilestarterapp.QRCodeReader;

import carsharing.starter.automotive.iot.ibm.com.mobilestarterapp.ConnectedDriverAPI.API;
import carsharing.starter.automotive.iot.ibm.com.mobilestarterapp.FirstPage;
import carsharing.starter.automotive.iot.ibm.com.mobilestarterapp.R;
import carsharing.starter.automotive.iot.ibm.com.mobilestarterapp.tabNavigation;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class SpecifyServer extends AppCompatActivity {
    public static SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specify_server);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("Specify Server");
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    public void openScanner(View view) {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.initiateScan();
    }

    public void useDefaultServer(View view) {
        Log.i("Button Clicked", "Default Server");

        Intent intent = new Intent(this, tabNavigation.class);
        startActivity(intent);
    }

    public void moreInfo(View view) {
        Log.i("Button Pressed", "More Info");

        Uri webpage = Uri.parse("http://www.ibm.com/internet-of-things/iot-industry/iot-automotive/");
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {
            if (scanResult.getContents() != null) {
                Log.d("RESULT", scanResult.toString());

                String[] fullString = scanResult.getContents().split(",");

                if (fullString[0].equals("1")) {
                    String appRoute;
                    String appGUID;
                    String customAuth;

                    switch (fullString.length) {
                        case 2:
                            appRoute = fullString[1];
                            setSharedpreferences("appRoute", appRoute);
                            removeSharedpreferences("appGUID");
                            removeSharedpreferences("customAuth");

                            break;

                        case 3:
                            appRoute = fullString[1];
                            appGUID = fullString[2];
                            setSharedpreferences("appRoute", appRoute);
                            setSharedpreferences("appGUID", appGUID);
                            removeSharedpreferences("customAuth");

                            break;

                        case 4:
                            appRoute = fullString[1];
                            appGUID = fullString[2];
                            customAuth = fullString[3];

                            break;

                        default:
                            break;
                    }
                }

                API.doInitialize();

                Toast toast = Toast.makeText(getApplicationContext(), "Changed were successfully applied!", Toast.LENGTH_SHORT);
                toast.show();

                Intent firstPage = new Intent(this, FirstPage.class);
                startActivity(firstPage);
            } else {
                Log.e("RESULT", "Failed");
            }
        }
    }

    public void setSharedpreferences(String key, String value) {
        sharedpreferences = getApplicationContext().getSharedPreferences("carsharing.starter.automotive.iot.ibm.com.mobilestarterapp.ConnectedDriverAPI", Context.MODE_PRIVATE);
        sharedpreferences.edit().putString(key, value).apply();

        String newValue = sharedpreferences.getString(key, "no" + key);

        Log.i(key + " changed to", newValue);
    }

    public void removeSharedpreferences(String key) {
        sharedpreferences = getApplicationContext().getSharedPreferences("carsharing.starter.automotive.iot.ibm.com.mobilestarterapp.ConnectedDriverAPI", Context.MODE_PRIVATE);
        sharedpreferences.edit().remove(key);

        if (sharedpreferences.getString(key, "no" + key).equals("no" + key)) {
            Log.i("Shared Preferences", key + " removed!");
        }

    }
}