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

public class BehaviorDuration implements Serializable {
    public final int start_time, end_time;

    public BehaviorDuration(final JSONObject behaviorDurationData) throws JSONException {
        start_time = behaviorDurationData.getInt("start_time");
        end_time = behaviorDurationData.getInt("end_time");
    }

    public static BehaviorDuration[] fromJSONArray(final JSONArray behaviorDurations) throws JSONException {
        final BehaviorDuration[] returnArray = new BehaviorDuration[behaviorDurations.length()];

        for (int i = 0; i < behaviorDurations.length(); i++) {
            final BehaviorDuration tempBehaviorDuration = new BehaviorDuration(behaviorDurations.getJSONObject(i));
            returnArray[i] = tempBehaviorDuration;
        }

        return returnArray;
    }
}