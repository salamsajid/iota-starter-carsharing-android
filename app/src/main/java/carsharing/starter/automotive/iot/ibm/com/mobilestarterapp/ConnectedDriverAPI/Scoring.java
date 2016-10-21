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
import java.util.ArrayList;

public class Scoring implements Serializable {
    public String freqStop_key = "Frequent stops";
    public String harshBrake_key = "Harsh braking";
    public String overSpeed_key = "Speeding";
    public String freqAcceleration_key = "Frequent acceleration";
    public String anxiousAcceleration_key = "Harsh acceleration";
    public String freqBrake_key = "Frequent braking";
    public String tiredDriving_key = "Fatigued driving";
    public String accBefTurn_key = "Acceleration before turn";
    public String brakeOutTurn_key = "Over-braking before exiting turn";
    public String sharpTurn_key = "Sharp turn";


    public int totalTime;
    public Double score;

    public ScoringBehavior freqStop;
    public ScoringBehavior harshBrake;
    public ScoringBehavior overSpeed;
    public ScoringBehavior freqAcceleration;
    public ScoringBehavior anxiousAcceleration;
    public ScoringBehavior freqBrake;
    public ScoringBehavior tiredDriving;
    public ScoringBehavior accBefTurn;
    public ScoringBehavior brakeOutTurn;
    public ScoringBehavior sharpTurn;

    public Scoring(JSONObject scoringData) throws JSONException {
        totalTime = scoringData.getInt("totalTime");
        score = scoringData.getDouble("score");

        if (scoringData.has(freqStop_key)) {
            freqStop = new ScoringBehavior(scoringData.getJSONObject(freqStop_key), freqStop_key);
        }

        if (scoringData.has(harshBrake_key)) {
            harshBrake = new ScoringBehavior(scoringData.getJSONObject(harshBrake_key), harshBrake_key);
        }

        if (scoringData.has(overSpeed_key)) {
            overSpeed = new ScoringBehavior(scoringData.getJSONObject(overSpeed_key), overSpeed_key);
        }

        if (scoringData.has(freqAcceleration_key)) {
            freqAcceleration = new ScoringBehavior(scoringData.getJSONObject(freqAcceleration_key), freqAcceleration_key);
        }

        if (scoringData.has(anxiousAcceleration_key)) {
            anxiousAcceleration = new ScoringBehavior(scoringData.getJSONObject(anxiousAcceleration_key), anxiousAcceleration_key);
        }

        if (scoringData.has(freqBrake_key)) {
            freqBrake = new ScoringBehavior(scoringData.getJSONObject(freqBrake_key), freqBrake_key);
        }

        if (scoringData.has(tiredDriving_key)) {
            tiredDriving = new ScoringBehavior(scoringData.getJSONObject(tiredDriving_key), tiredDriving_key);
        }

        if (scoringData.has(accBefTurn_key)) {
            accBefTurn = new ScoringBehavior(scoringData.getJSONObject(accBefTurn_key), accBefTurn_key);
        }

        if (scoringData.has(brakeOutTurn_key)) {
            brakeOutTurn = new ScoringBehavior(scoringData.getJSONObject(brakeOutTurn_key), brakeOutTurn_key);
        }

        if (scoringData.has(sharpTurn_key)) {
            sharpTurn = new ScoringBehavior(scoringData.getJSONObject(sharpTurn_key), sharpTurn_key);
        }
    }

    public ArrayList<ScoringBehavior> getScoringBehaviors() {
        ArrayList<ScoringBehavior> returnArray = new ArrayList<ScoringBehavior>();

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