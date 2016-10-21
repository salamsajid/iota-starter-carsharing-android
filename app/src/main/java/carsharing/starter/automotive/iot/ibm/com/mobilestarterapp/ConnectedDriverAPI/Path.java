/**
 * Copyright 2016 IBM Corp. All Rights Reserved.
 *
 * Licensed under the IBM License, a copy of which may be obtained at:
 *
 * http://www14.software.ibm.com/cgi-bin/weblap/lap.pl?li_formnum=L-DDIN-AEGGZJ&popup=y&title=IBM%20IoT%20for%20Automotive%20Sample%20Starter%20Apps%20%28Android-Mobile%20and%20Server-all%29
 *
 * You may not use this file except in compliance with the license.
 */
package carsharing.starter.automotive.iot.ibm.com.mobilestarterapp.ConnectedDriverAPI;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class Path implements Serializable {
    public LatLng[] coordinates;

    public Path(JSONObject geometryData) throws JSONException {
        JSONArray coordinatesArray = geometryData.getJSONArray("coordinates");

        coordinates = new LatLng[coordinatesArray.length()];

        for (int i = 0; i < coordinatesArray.length(); i++) {
            LatLng temp = new LatLng(coordinatesArray.getJSONArray(i).getDouble(1), coordinatesArray.getJSONArray(i).getDouble(0));

            coordinates[i] = temp;
        }
    }

    public static ArrayList<Path> fromDictionary(JSONArray dictionary) throws JSONException {
        ArrayList<Path> returnArray = new ArrayList<Path>();

        for (int i = 0; i < dictionary.length(); i++) {
            JSONObject item = dictionary.getJSONObject(i);
            JSONArray features = item.getJSONArray("features");
            JSONObject geometry = features.getJSONObject(0).getJSONObject("geometry");

            returnArray.add(new Path(geometry));
        }

        return returnArray;
    }
}