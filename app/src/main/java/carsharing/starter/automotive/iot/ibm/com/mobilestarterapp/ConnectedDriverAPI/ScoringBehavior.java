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

public class ScoringBehavior implements Serializable {
    public final String name;
    public final Double score;
    public final int totalTime, count;

    public ScoringBehavior(final JSONObject scoringBehaviorData, final String name) throws JSONException {
        score = scoringBehaviorData.getDouble("score");
        totalTime = scoringBehaviorData.getInt("totalTime");
        count = scoringBehaviorData.getInt("count");

        this.name = name;
    }
}