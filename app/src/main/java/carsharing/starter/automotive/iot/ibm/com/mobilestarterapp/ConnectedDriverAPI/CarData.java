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

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class CarData implements Serializable {
    public String deviceID, deviceType, name, status, title, makeModel, availability, thumbnailURL, type, drive, license, rateCauseCategory, rateCauseShort, rateCauseLong;
    public Double lat, lng, rate;
    public int distance, lastUpdateTime, year, mileage, stars, hourlyRate, dailyRate;

    public static LatLng coordinate;

    public CarData(final JSONObject carData) throws JSONException {
        deviceID = carData.getString("deviceID");
        deviceType = carData.has("deviceType") ? carData.getString("deviceType") : null;
        lastUpdateTime = carData.has("lastUpdateTime") ? carData.getInt("lastUpdateTime") : 0;

        if (carData.has("lat") && carData.has("lng") && carData.get("lat") != null && carData.get("lng") != null) {
            lat = carData.getDouble("lat");
            lng = carData.getDouble("lng");

            coordinate = new LatLng(lat, lng);
        }
        name = carData.getString("name");
        status = carData.has("status") ? carData.getString("status") : null;
        distance = carData.has("distance") ? carData.getInt("distance") : 0;
        license = carData.has("license") ? carData.getString("license") : null;
        title = name;

        if (carData.has("model")) {
            final JSONObject model = carData.getJSONObject("model");

            makeModel = model.getString("makeModel");
            year = model.getInt("year");
            mileage = model.getInt("mileage");
            stars = model.getInt("stars");
            hourlyRate = model.getInt("hourlyRate");
            dailyRate = model.getInt("dailyRate");
            thumbnailURL = model.getString("thumbnailURL");
            type = model.has("type") ? model.getString("type") : null;
            drive = model.has("drive") ? model.getString("drive") : null;
        }

//        if let recommendation = dictionary["recommendation"] {
//            rate = recommendation["rate"] as? Double
//
//            if let causes = recommendation["causes"] {
//                if causes?.count > 0 {
//                    rateCauseCategory = causes![0]["category"] as? String
//                    rateCauseShort = causes![0]["shortText"] as? String
//                    rateCauseLong = causes![0]["longText"] as? String
//                }
//            }
//        }
    }
}