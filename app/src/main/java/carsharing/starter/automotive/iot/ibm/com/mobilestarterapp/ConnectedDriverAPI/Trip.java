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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Trip implements Serializable {
    public final Double start_time, end_time, start_latitude, start_longitude, end_latitude, end_longitude;
    public final BehaviorTimes behaviorTimes;
    public final Scoring scoring;
    public TripLocation[] locations;

    public Trip(final JSONObject tripData) throws JSONException {
        start_time = tripData.getDouble("start_time");
        end_time = tripData.getDouble("end_time");
        start_latitude = tripData.getDouble("start_latitude");
        start_longitude = tripData.getDouble("start_longitude");
        end_latitude = tripData.getDouble("end_latitude");
        end_longitude = tripData.getDouble("end_longitude");
        behaviorTimes = tripData.has("behaviors") ? new BehaviorTimes(tripData.getJSONObject("behaviors")) : null;
        scoring = tripData.has("scoring") ? new Scoring(tripData.getJSONObject("scoring")) : null;

        if (tripData.has("locations")) {
            JSONArray locationsArray = tripData.getJSONArray("locations");
            locations = new TripLocation[locationsArray.length()];

            for (int i = 0; i < locationsArray.length(); i++) {
                TripLocation tempTripLocation = new TripLocation(locationsArray.getJSONObject(i));
                locations[i] = tempTripLocation;
            }

            //  TODO
        }
    }
}