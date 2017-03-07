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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class TripData implements Serializable {

    public final String trip_id, trip_uuid, mo_id;
    public final long start_time, end_time, duration;
    public final Double score, start_latitude, end_latitude, start_longitude, end_longitude;
    public final int start_altitude, end_altitude;

    public TripData(JSONObject tripData) throws JSONException {
        score = tripData.getDouble("score");
        trip_id = tripData.getString("trip_id");

        trip_uuid = tripData.has("trip_uuid") ? tripData.getString("trip_uuid") : "";
        mo_id = tripData.has("mo_id") ? tripData.getString("mo_id") : "";
        start_time = tripData.has("start_time") ? tripData.getLong("start_time") : 0;
        end_time = tripData.has("end_time") ? tripData.getLong("end_time") : 0;
        start_altitude = tripData.has("start_altitude") ? tripData.getInt("start_altitude") : 0;
        end_altitude = tripData.has("end_altitude") ? tripData.getInt("end_altitude") : 0;
        start_latitude = tripData.has("start_latitude") ? tripData.getDouble("start_latitude") : 0.0;
        end_latitude = tripData.has("end_latitude") ? tripData.getDouble("end_latitude") : 0.0;
        start_longitude = tripData.has("start_longitude") ? tripData.getDouble("start_longitude") : 0.0;
        end_longitude = tripData.has("end_longitude") ? tripData.getDouble("end_longitude") : 0.0;

        duration = end_time - start_time;
    }
}