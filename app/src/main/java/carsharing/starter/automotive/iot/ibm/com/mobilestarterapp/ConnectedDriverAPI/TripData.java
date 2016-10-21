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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class TripData implements Serializable {
    public String trip_id = "", trip_uuid = "", mo_id = "";
    public Double score = 0.0, start_time= 0.0, end_time = 0.0, start_latitude = 0.0, end_latitude = 0.0, start_longitude = 0.0, end_longitude = 0.0, duration = 0.0;
    public int start_altitude, end_altitude;

    public TripData(JSONObject tripData) throws JSONException {
        score = tripData.getDouble("score");

        trip_id = tripData.getString("trip_id");

        if (tripData.has("trip_uuid")) {
            trip_uuid = tripData.getString("trip_uuid");
        }

        if (tripData.has("mo_id")) {
            mo_id = tripData.getString("mo_id");
        }

        if (tripData.has("start_time")) {
            start_time = tripData.getDouble("start_time");
        }

        if (tripData.has("end_time")) {
            end_time = tripData.getDouble("end_time");
        }

        if (tripData.has("start_altitude")) {
            start_altitude = tripData.getInt("start_altitude");
        }

        if (tripData.has("end_altitude")) {
            end_altitude = tripData.getInt("end_altitude");
        }

        if (tripData.has("start_latitude")) {
            start_latitude = tripData.getDouble("start_latitude");
        }

        if (tripData.has("end_latitude")) {
            end_latitude = tripData.getDouble("end_latitude");
        }

        if (tripData.has("start_longitude")) {
            start_longitude = tripData.getDouble("start_longitude");
        }

        if (tripData.has("end_longitude")) {
            end_longitude = tripData.getDouble("end_longitude");
        }

        duration = end_time - start_time;
    }
}