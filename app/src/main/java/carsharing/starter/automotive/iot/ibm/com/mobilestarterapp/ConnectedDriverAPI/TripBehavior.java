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

public class TripBehavior implements Serializable {
    public Double start_latitude, start_longitude, end_latitude, end_longitude;
    public String behavior_name;

    public TripBehavior(JSONObject tripBehaviorData) throws JSONException {
        start_latitude = tripBehaviorData.getDouble("start_latitude");
        start_longitude = tripBehaviorData.getDouble("start_longitude");
        end_latitude = tripBehaviorData.getDouble("end_latitude");
        end_longitude = tripBehaviorData.getDouble("end_longitude");
        behavior_name = tripBehaviorData.getString("behavior_name");
    }
}