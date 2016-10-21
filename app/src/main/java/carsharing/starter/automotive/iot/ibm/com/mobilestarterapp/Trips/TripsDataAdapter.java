/**
 * Copyright 2016 IBM Corp. All Rights Reserved.
 *
 * Licensed under the IBM License, a copy of which may be obtained at:
 *
 * http://www14.software.ibm.com/cgi-bin/weblap/lap.pl?li_formnum=L-DDIN-AEGGZJ&popup=y&title=IBM%20IoT%20for%20Automotive%20Sample%20Starter%20Apps%20%28Android-Mobile%20and%20Server-all%29
 *
 * You may not use this file except in compliance with the license.
 */
package carsharing.starter.automotive.iot.ibm.com.mobilestarterapp.Trips;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import carsharing.starter.automotive.iot.ibm.com.mobilestarterapp.ConnectedDriverAPI.TripData;
import carsharing.starter.automotive.iot.ibm.com.mobilestarterapp.R;

public class TripsDataAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater inflater;

    private ArrayList<TripData> data;
    static final GregorianCalendar[] tripCal = { new GregorianCalendar() };

    public TripsDataAdapter(Context context, ArrayList<TripData> tripData) {
        mContext = context;

        data = tripData;

        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() { return data.size(); }

    @Override
    public Object getItem(int index) {
        return data.get(index);
    }

    @Override
    public long getItemId(int index) {
        return index;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = inflater.inflate(R.layout.list_item_trips, parent, false);

        TripData tripData = data.get(position);

        tripCal[0].setTimeInMillis(tripData.start_time.longValue());

        TextView startLocation = (TextView) rowView.findViewById(R.id.startLocation);
        TextView endLocation = (TextView) rowView.findViewById(R.id.endLocation);

        TextView date = (TextView) rowView.findViewById(R.id.date);
        TextView duration = (TextView) rowView.findViewById(R.id.duration);

        date.setText(dateToString(tripCal[0]));

        String durationText = "";

        if (tripData.duration != null) {
            int durationValue = tripData.duration.intValue() / 1000 / 60;
            durationText += (durationValue + " Min");
        } else {
            durationText += ("Unknown Duration");
        }

        if (tripData.score != null) {
            durationText += (", Score " + Math.round(tripData.score.intValue()));
        } else {
            durationText += (", Unknown Score");
        }

        duration.setText(durationText);

        startLocation.setText(geoCoder(tripData.start_latitude, tripData.start_longitude));
        endLocation.setText(geoCoder(tripData.end_latitude, tripData.end_longitude));

        return rowView;
    }

    private String geoCoder(Double lat, Double lng) {
        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
        try {
            final List<Address> addressArray = geocoder.getFromLocation(lat, lng, 1);

            if(addressArray.size() > 0) {
                String locationAddress = addressArray.get(0).getAddressLine(0);

                return (locationAddress.length() > 18) ? locationAddress.substring(0, 18) + "..." : locationAddress;
            }
        } catch (IOException e) {
            e.printStackTrace();

            return "";
        }

        return "";
    }

    public static String dateToString(GregorianCalendar cal) {
        SimpleDateFormat formattedCal = new SimpleDateFormat("MMM dd, yyyy");
        formattedCal.setCalendar(cal);

        String dateFormatted = formattedCal.format(cal.getTime());

        return dateFormatted;
    }
}