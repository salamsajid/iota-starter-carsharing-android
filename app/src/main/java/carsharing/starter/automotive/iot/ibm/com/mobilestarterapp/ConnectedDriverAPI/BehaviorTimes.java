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

public class BehaviorTimes implements Serializable {
    public final BehaviorDuration[] freqStop, harshBrake, overSpeed, freqAcceleration, anxiousAcceleration, freqBrake, tiredDriving, accBefTurn, brakeOutTurn, sharpTurn;

    public BehaviorTimes(final JSONObject behaviorTimeData) throws JSONException {

        freqStop = behaviorTimeData.has("FreqStop") ? BehaviorDuration.fromJSONArray(behaviorTimeData.getJSONArray("FreqStop")) : null;
        harshBrake = behaviorTimeData.has("HarshBrake") ? BehaviorDuration.fromJSONArray(behaviorTimeData.getJSONArray("HarshBrake")) : null;
        overSpeed = behaviorTimeData.has("OverSpeed") ? BehaviorDuration.fromJSONArray(behaviorTimeData.getJSONArray("OverSpeed")) : null;
        freqAcceleration = behaviorTimeData.has("FreqAcceleration") ? BehaviorDuration.fromJSONArray(behaviorTimeData.getJSONArray("FreqAcceleration")) : null;
        anxiousAcceleration = behaviorTimeData.has("AnxiousAcceleration") ? BehaviorDuration.fromJSONArray(behaviorTimeData.getJSONArray("AnxiousAcceleration")) : null;
        freqBrake = behaviorTimeData.has("FreqBrake") ? BehaviorDuration.fromJSONArray(behaviorTimeData.getJSONArray("FreqBrake")) : null;
        tiredDriving = behaviorTimeData.has("TiredDriving") ? BehaviorDuration.fromJSONArray(behaviorTimeData.getJSONArray("TiredDriving")) : null;
        accBefTurn = behaviorTimeData.has("AccBefTurn") ? BehaviorDuration.fromJSONArray(behaviorTimeData.getJSONArray("AccBefTurn")) : null;
        brakeOutTurn = behaviorTimeData.has("BrakeOutTurn") ? BehaviorDuration.fromJSONArray(behaviorTimeData.getJSONArray("BrakeOutTurn")) : null;
        sharpTurn = behaviorTimeData.has("SharpTurn") ? BehaviorDuration.fromJSONArray(behaviorTimeData.getJSONArray("SharpTurn")) : null;
    }
}