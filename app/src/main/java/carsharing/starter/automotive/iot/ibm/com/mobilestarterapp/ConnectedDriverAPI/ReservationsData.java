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

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class ReservationsData implements Serializable {
    public String _id, _rev, type, status, carId, userId;
    public Double pickupTime, dropOffTime;
    public CarData carDetails;

    public ReservationsData(JSONObject reservationData) throws JSONException {
        _id = reservationData.getString("_id");
        _rev = reservationData.getString("_rev");
        type = reservationData.getString("type");
        carId = reservationData.getString("carId");

        pickupTime = reservationData.getDouble("pickupTime");
        dropOffTime = reservationData.getDouble("dropOffTime");

        userId = reservationData.getString("userId");

        status = reservationData.getString("status");

        if (reservationData.has("carDetails")) {
            carDetails = new CarData(reservationData.getJSONObject("carDetails"));
        }
    }
}