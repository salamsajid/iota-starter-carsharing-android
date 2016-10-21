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

public class BehaviorTimes implements Serializable {
    public BehaviorDuration[] freqStop, harshBrake, overSpeed, freqAcceleration, anxiousAcceleration, freqBrake, tiredDriving, accBefTurn, brakeOutTurn, sharpTurn;

    public BehaviorTimes(JSONObject behaviorTimeData) throws JSONException {
        if (behaviorTimeData.has("FreqStop")) {
            freqStop = BehaviorDuration.fromJSONArray(behaviorTimeData.getJSONArray("FreqStop"));
        }

        if (behaviorTimeData.has("HarshBrake")) {
            harshBrake = BehaviorDuration.fromJSONArray(behaviorTimeData.getJSONArray("HarshBrake"));
        }

        if (behaviorTimeData.has("OverSpeed")) {
            overSpeed = BehaviorDuration.fromJSONArray(behaviorTimeData.getJSONArray("OverSpeed"));
        }

        if (behaviorTimeData.has("FreqAcceleration")) {
            freqAcceleration = BehaviorDuration.fromJSONArray(behaviorTimeData.getJSONArray("FreqAcceleration"));
        }

        if (behaviorTimeData.has("AnxiousAcceleration")) {
            anxiousAcceleration = BehaviorDuration.fromJSONArray(behaviorTimeData.getJSONArray("AnxiousAcceleration"));
        }

        if (behaviorTimeData.has("FreqBrake")) {
            freqBrake = BehaviorDuration.fromJSONArray(behaviorTimeData.getJSONArray("FreqBrake"));
        }

        if (behaviorTimeData.has("TiredDriving")) {
            tiredDriving = BehaviorDuration.fromJSONArray(behaviorTimeData.getJSONArray("TiredDriving"));
        }

        if (behaviorTimeData.has("AccBefTurn")) {
            accBefTurn = BehaviorDuration.fromJSONArray(behaviorTimeData.getJSONArray("AccBefTurn"));
        }

        if (behaviorTimeData.has("BrakeOutTurn")) {
            brakeOutTurn = BehaviorDuration.fromJSONArray(behaviorTimeData.getJSONArray("BrakeOutTurn"));
        }

        if (behaviorTimeData.has("SharpTurn")) {
            sharpTurn = BehaviorDuration.fromJSONArray(behaviorTimeData.getJSONArray("SharpTurn"));
        }
    }
}