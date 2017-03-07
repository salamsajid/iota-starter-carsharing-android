/**
 * Copyright 2016 IBM Corp. All Rights Reserved.
 * <p>
 * Licensed under the IBM License, a copy of which may be obtained at:
 * <p>
 * http://www14.software.ibm.com/cgi-bin/weblap/lap.pl?li_formnum=L-DDIN-AEGGZJ&popup=y&title=IBM%20IoT%20for%20Automotive%20Sample%20Starter%20Apps%20%28Android-Mobile%20and%20Server-all%29
 * <p>
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
    final private Context mContext;
    final private LayoutInflater inflater;

    final private ArrayList<TripData> data;
    static final GregorianCalendar[] tripCal = {new GregorianCalendar()};

    public TripsDataAdapter(final Context context, final ArrayList<TripData> tripData) {
        mContext = context;
        data = tripData;
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int index) {
        return data.get(index);
    }

    @Override
    public long getItemId(int index) {
        return index;
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        final View rowView = inflater.inflate(R.layout.list_item_trips, parent, false);
        final TripData tripData = data.get(position);
        tripCal[0].setTimeInMillis(tripData.start_time);

        final TextView startLocation = (TextView) rowView.findViewById(R.id.startLocation);
        final TextView endLocation = (TextView) rowView.findViewById(R.id.endLocation);
        final TextView date = (TextView) rowView.findViewById(R.id.date);
        final TextView duration = (TextView) rowView.findViewById(R.id.duration);

        date.setText(dateToString(tripCal[0]));

        String durationText = "";
        if (tripData.start_time > 0) {
            final long durationValue = tripData.duration / 1000 / 60;
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

    private String geoCoder(final Double lat, final Double lng) {
        final Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
        try {
            final List<Address> addressArray = geocoder.getFromLocation(lat, lng, 1);

            if (addressArray.size() > 0) {
                final String locationAddress = addressArray.get(0).getAddressLine(0);
                return (locationAddress.length() > 18) ? locationAddress.substring(0, 18) + "..." : locationAddress;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
        return "";
    }

    public static String dateToString(final GregorianCalendar cal) {
        final SimpleDateFormat formattedCal = new SimpleDateFormat("MMM dd, yyyy");
        formattedCal.setCalendar(cal);

        final String dateFormatted = formattedCal.format(cal.getTime());
        return dateFormatted;
    }
}