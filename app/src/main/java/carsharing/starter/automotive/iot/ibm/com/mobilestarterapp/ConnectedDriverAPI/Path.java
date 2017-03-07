/**
 * Copyright 2016 IBM Corp. All Rights Reserved.
 * <p>
 * Licensed under the IBM License, a copy of which may be obtained at:
 * <p>
 * http://www14.software.ibm.com/cgi-bin/weblap/lap.pl?li_formnum=L-DDIN-AEGGZJ&popup=y&title=IBM%20IoT%20for%20Automotive%20Sample%20Starter%20Apps%20%28Android-Mobile%20and%20Server-all%29
 * <p>
 * You may not use this file except in compliance with the license.
 */
package carsharing.starter.automotive.iot.ibm.com.mobilestarterapp.ConnectedDriverAPI;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class Path implements Serializable {
    public final LatLng[] coordinates;

    public Path(final JSONObject geometryData) throws JSONException {
        final JSONArray coordinatesArray = geometryData.getJSONArray("coordinates");

        coordinates = new LatLng[coordinatesArray.length()];

        for (int i = 0; i < coordinatesArray.length(); i++) {
            final LatLng temp = new LatLng(coordinatesArray.getJSONArray(i).getDouble(1), coordinatesArray.getJSONArray(i).getDouble(0));

            coordinates[i] = temp;
        }
    }

    public static ArrayList<Path> fromDictionary(final JSONArray dictionary) throws JSONException {
        final ArrayList<Path> returnArray = new ArrayList<Path>();

        for (int i = 0; i < dictionary.length(); i++) {
            final JSONObject item = dictionary.getJSONObject(i);
            final JSONArray features = item.getJSONArray("features");
            final JSONObject geometry = features.getJSONObject(0).getJSONObject("geometry");

            returnArray.add(new Path(geometry));
        }

        return returnArray;
    }
}