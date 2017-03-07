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
import java.util.ArrayList;

public class Scoring implements Serializable {
    public final String freqStop_key = "Frequent stops";
    public final String harshBrake_key = "Harsh braking";
    public final String overSpeed_key = "Speeding";
    public final String freqAcceleration_key = "Frequent acceleration";
    public final String anxiousAcceleration_key = "Harsh acceleration";
    public final String freqBrake_key = "Frequent braking";
    public final String tiredDriving_key = "Fatigued driving";
    public final String accBefTurn_key = "Acceleration before turn";
    public final String brakeOutTurn_key = "Over-braking before exiting turn";
    public final String sharpTurn_key = "Sharp turn";


    public final int totalTime;
    public final Double score;

    public final ScoringBehavior freqStop;
    public final ScoringBehavior harshBrake;
    public final ScoringBehavior overSpeed;
    public final ScoringBehavior freqAcceleration;
    public final ScoringBehavior anxiousAcceleration;
    public final ScoringBehavior freqBrake;
    public final ScoringBehavior tiredDriving;
    public final ScoringBehavior accBefTurn;
    public final ScoringBehavior brakeOutTurn;
    public final ScoringBehavior sharpTurn;

    public Scoring(final JSONObject scoringData) throws JSONException {
        totalTime = scoringData.getInt("totalTime");
        score = scoringData.getDouble("score");
        freqStop = scoringData.has(freqStop_key) ? new ScoringBehavior(scoringData.getJSONObject(freqStop_key), freqStop_key) : null;
        harshBrake = scoringData.has(harshBrake_key) ? new ScoringBehavior(scoringData.getJSONObject(harshBrake_key), harshBrake_key) : null;
        overSpeed = scoringData.has(overSpeed_key) ? new ScoringBehavior(scoringData.getJSONObject(overSpeed_key), overSpeed_key) : null;
        freqAcceleration = scoringData.has(freqAcceleration_key) ? new ScoringBehavior(scoringData.getJSONObject(freqAcceleration_key), freqAcceleration_key) : null;
        anxiousAcceleration = scoringData.has(anxiousAcceleration_key) ? new ScoringBehavior(scoringData.getJSONObject(anxiousAcceleration_key), anxiousAcceleration_key) : null;
        freqBrake = scoringData.has(freqBrake_key) ? new ScoringBehavior(scoringData.getJSONObject(freqBrake_key), freqBrake_key) : null;
        tiredDriving = scoringData.has(tiredDriving_key) ? new ScoringBehavior(scoringData.getJSONObject(tiredDriving_key), tiredDriving_key) : null;
        accBefTurn = scoringData.has(accBefTurn_key) ? new ScoringBehavior(scoringData.getJSONObject(accBefTurn_key), accBefTurn_key) : null;
        brakeOutTurn = scoringData.has(brakeOutTurn_key) ? new ScoringBehavior(scoringData.getJSONObject(brakeOutTurn_key), brakeOutTurn_key) : null;
        sharpTurn = scoringData.has(sharpTurn_key) ? new ScoringBehavior(scoringData.getJSONObject(sharpTurn_key), sharpTurn_key) : null;
    }

    public ArrayList<ScoringBehavior> getScoringBehaviors() {
        final ArrayList<ScoringBehavior> returnArray = new ArrayList<ScoringBehavior>();

        if (accBefTurn != null) {
            returnArray.add(accBefTurn);
        }
        if (anxiousAcceleration != null) {
            returnArray.add(anxiousAcceleration);
        }
        if (brakeOutTurn != null) {
            returnArray.add(brakeOutTurn);
        }
        if (freqAcceleration != null) {
            returnArray.add(freqAcceleration);
        }
        if (freqBrake != null) {
            returnArray.add(freqBrake);
        }
        if (freqStop != null) {
            returnArray.add(freqStop);
        }
        if (harshBrake != null) {
            returnArray.add(harshBrake);
        }
        if (overSpeed != null) {
            returnArray.add(overSpeed);
        }
        if (sharpTurn != null) {
            returnArray.add(sharpTurn);
        }
        if (tiredDriving != null) {
            returnArray.add(tiredDriving);
        }

        return returnArray;
    }
}