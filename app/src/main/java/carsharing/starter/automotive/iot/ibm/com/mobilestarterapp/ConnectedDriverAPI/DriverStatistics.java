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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DriverStatistics implements Serializable {
    public Double totalDistance;
    public Scoring scoring;
    public SpeedPattern speedPattern;
    public RoadType roadType;
    public TimeRange timeRange;

    public DriverStatistics(JSONObject profileData) throws JSONException {
        totalDistance = profileData.getDouble("totalDistance");

        if (profileData.has("scoring")) {
            scoring = new Scoring(profileData.getJSONObject("scoring"));
        }

        if (profileData.has("speedPattern")) {
            speedPattern = new SpeedPattern(profileData.getJSONObject("speedPattern"));
        }

        if (profileData.has("roadType")) {
            roadType = new RoadType(profileData.getJSONObject("roadType"));
        }

        if (profileData.has("timeRange")) {
            timeRange = new TimeRange(profileData.getJSONObject("timeRange"));
        }
    }

    public class RoadType {
        public String others_key = "Others/Urban path or alley";
        public String totalDistance_key = "totalDistance";
        public String urban_key = "Urban-road";
        public String secondary_key = "Secondary Extra-urban road/urban primary";
        public String highway_key = "Highway/motor way";
        public String main_key = "Main extra-urban road/urban-highway";
        public String unknown_key = "unknown";

        public Double others, totalDistance, urban, secondary, highway, main, unknown;

        public Map<String, Double> roadTypes = new HashMap<String, Double>();

        public ArrayList<String> roadTypesSortedKeys = new ArrayList<String>();

        public RoadType(JSONObject roadTypeData) throws JSONException {
            if (roadTypeData.has(others_key)) {
                others = roadTypeData.getDouble(others_key);

                roadTypes.put(others_key, others);
            }

            if (roadTypeData.has(totalDistance_key)) {
                totalDistance = roadTypeData.getDouble(totalDistance_key);

                roadTypes.put(totalDistance_key, totalDistance);
            }

            if (roadTypeData.has(urban_key)) {
                urban = roadTypeData.getDouble(urban_key);

                roadTypes.put(urban_key, urban);
            }
            if (roadTypeData.has(secondary_key)) {
                secondary = roadTypeData.getDouble(secondary_key);

                roadTypes.put(secondary_key, secondary);
            }
            if (roadTypeData.has(highway_key)) {
                highway = roadTypeData.getDouble(highway_key);

                roadTypes.put(highway_key, highway);
            }
            if (roadTypeData.has(main_key)) {
                main = roadTypeData.getDouble(main_key);

                roadTypes.put(main_key, main);
            }
            if (roadTypeData.has(unknown_key)) {
                unknown = roadTypeData.getDouble(unknown_key);

                roadTypes.put(unknown_key, unknown);
            }
        }

        public void setTotalDistance(Double totalDistance) {
            this.totalDistance = totalDistance;
        }

        public void toDictionary() {
            // SORT
            List<Map.Entry<String, Double>> list = new LinkedList<>(roadTypes.entrySet());

            Collections.sort(list, new Comparator<Map.Entry<String,Double>>()
            {
                @Override
                public int compare(Map.Entry<String,Double> obj1, Map.Entry<String,Double> obj2)
                {
                    return (obj2.getValue()).compareTo(obj1.getValue());
                }
            } );

            for (int i = 0; i < list.size(); i++) {
                roadTypesSortedKeys.add(list.get(i).getKey());
            }
        }
    }

    public class SpeedPattern {
        public String mixedSpeed_key = "mixedConditions";
        public String totalDistance_key = "totalDistance";
        public String steadyFlow_key = "steadyFlow";
        public String freeFlow_key = "freeFlow";
        public String congestion_key = "congestion";
        public String severeCongestion_key = "severeCongestion";
        public String unknown_key = "unknown";

        public Double mixedSpeed, totalDistance, steadyFlow, freeFlow, congestion, severeCongestion, unknown;

        public Map<String, Double> trafficConditions = new HashMap<String, Double>();

        public ArrayList<String> trafficConditionsSortedKeys = new ArrayList<String>();

        public SpeedPattern(JSONObject speedPatternData) throws JSONException {
            if (speedPatternData.has(mixedSpeed_key)) {
                mixedSpeed = speedPatternData.getDouble(mixedSpeed_key);

                trafficConditions.put(mixedSpeed_key, mixedSpeed);
            }

            if (speedPatternData.has(totalDistance_key)) {
                totalDistance = speedPatternData.getDouble(totalDistance_key);

                trafficConditions.put(totalDistance_key, totalDistance);
            }

            if (speedPatternData.has(steadyFlow_key)) {
                steadyFlow = speedPatternData.getDouble(steadyFlow_key);

                trafficConditions.put(steadyFlow_key, steadyFlow);
            }

            if (speedPatternData.has(freeFlow_key)) {
                freeFlow = speedPatternData.getDouble(freeFlow_key);

                trafficConditions.put(freeFlow_key, freeFlow);
            }

            if (speedPatternData.has(congestion_key)) {
                congestion = speedPatternData.getDouble(congestion_key);

                trafficConditions.put(congestion_key, congestion);
            }

            if (speedPatternData.has(severeCongestion_key)) {
                severeCongestion = speedPatternData.getDouble(severeCongestion_key);

                trafficConditions.put(severeCongestion_key, severeCongestion);
            }

            if (speedPatternData.has(unknown_key)) {
                unknown = speedPatternData.getDouble(unknown_key);

                trafficConditions.put(unknown_key, unknown);
            }
        }

        public void setTotalDistance(Double totalDistance) {
            this.totalDistance = totalDistance;
        }

        public void toDictionary() {
            // SORT
            List<Map.Entry<String, Double>> list = new LinkedList<>(trafficConditions.entrySet());

            Collections.sort(list, new Comparator<Map.Entry<String,Double>>()
            {
                @Override
                public int compare(Map.Entry<String,Double> obj1, Map.Entry<String,Double> obj2)
                {
                    return (obj2.getValue()).compareTo(obj1.getValue());
                }
            } );

            for (int i = 0; i < list.size(); i++) {
                trafficConditionsSortedKeys.add(list.get(i).getKey());
            }
        }
    }

    public class TimeRange {
        public String morningPeak_key = "morningPeakHours";
        public String totalDistance_key = "totalDistance";
        public String nightDriving_key = "night";
        public String dayDriving_key = "day";
        public String eveningPeak_key = "eveningPeakHours";

        public Double morningPeak, totalDistance, nightDriving, dayDriving, eveningPeak;

        public Map<String, Double> timesOfDay = new HashMap<String, Double>();

        public ArrayList<String> timesOfDaySortedKeys = new ArrayList<String>();

        public TimeRange(JSONObject timeRangeData) throws JSONException {
            if (timeRangeData.has(morningPeak_key)){
                morningPeak = timeRangeData.getDouble(morningPeak_key);

                timesOfDay.put(morningPeak_key, morningPeak);
            }

            if (timeRangeData.has(totalDistance_key)) {
                totalDistance = timeRangeData.getDouble(totalDistance_key);

                timesOfDay.put(totalDistance_key, totalDistance);
            }

            if (timeRangeData.has(nightDriving_key)) {
                nightDriving = timeRangeData.getDouble(nightDriving_key);

                timesOfDay.put(nightDriving_key, nightDriving);
            }

            if (timeRangeData.has(dayDriving_key)) {
                dayDriving = timeRangeData.getDouble(dayDriving_key);

                timesOfDay.put(dayDriving_key, dayDriving);
            }

            if (timeRangeData.has(eveningPeak_key)) {
                eveningPeak = timeRangeData.getDouble(eveningPeak_key);

                timesOfDay.put(eveningPeak_key, eveningPeak);
            }
        }

        public void setTotalDistance(Double totalDistance) {
            this.totalDistance = totalDistance;
        }

        public void toDictionary() {
            // SORT
            List<Map.Entry<String, Double>> list = new LinkedList<>(timesOfDay.entrySet());

            Collections.sort(list, new Comparator<Map.Entry<String,Double>>()
            {
                @Override
                public int compare(Map.Entry<String,Double> obj1, Map.Entry<String,Double> obj2)
                {
                    return (obj2.getValue()).compareTo(obj1.getValue());
                }
            } );

            for (int i = 0; i < list.size(); i++) {
                timesOfDaySortedKeys.add(list.get(i).getKey());
            }
        }
    }
}