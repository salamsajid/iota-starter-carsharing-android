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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class TripLocation implements Serializable {
    public Double start_latitude, start_longitude, end_latitude, end_longitude;
    public TripBehavior[] behaviors;

    public TripLocation(JSONObject tripLocData) throws JSONException {
        start_latitude = tripLocData.getDouble("start_latitude");
        start_longitude = tripLocData.getDouble("start_longitude");
        end_latitude = tripLocData.getDouble("end_latitude");
        end_longitude = tripLocData.getDouble("end_longitude");

        if (tripLocData.has("behaviors")) {
            JSONArray behaviorsArray = tripLocData.getJSONArray("behaviors");
            behaviors = new TripBehavior[behaviorsArray.length()];

            for (int i=0; i < behaviorsArray.length(); i++) {
                TripBehavior tempTripBehavior = new TripBehavior(behaviorsArray.getJSONObject(i));
                behaviors[i] = tempTripBehavior;
            }
        }
    }
}