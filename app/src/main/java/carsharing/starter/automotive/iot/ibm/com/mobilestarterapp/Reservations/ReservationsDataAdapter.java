/**
 * Copyright 2016 IBM Corp. All Rights Reserved.
 *
 * Licensed under the IBM License, a copy of which may be obtained at:
 *
 * http://www14.software.ibm.com/cgi-bin/weblap/lap.pl?li_formnum=L-DDIN-AEGGZJ&popup=y&title=IBM%20IoT%20for%20Automotive%20Sample%20Starter%20Apps%20%28Android-Mobile%20and%20Server-all%29
 *
 * You may not use this file except in compliance with the license.
 */
package carsharing.starter.automotive.iot.ibm.com.mobilestarterapp.Reservations;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import carsharing.starter.automotive.iot.ibm.com.mobilestarterapp.ConnectedDriverAPI.CarData;
import carsharing.starter.automotive.iot.ibm.com.mobilestarterapp.ConnectedDriverAPI.ReservationsData;
import carsharing.starter.automotive.iot.ibm.com.mobilestarterapp.R;

public class ReservationsDataAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater inflater;
    private ArrayList<ReservationsData> data;

    static final GregorianCalendar[] pickupCal = { new GregorianCalendar() };

    public ReservationsDataAdapter(Context context, ArrayList<ReservationsData> carsArray) {
        mContext = context;
        data = carsArray;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = inflater.inflate(R.layout.list_item_reservation, parent, false);

        TextView carTitleTextView = (TextView) rowView.findViewById(R.id.behaviorTitle);
        TextView dateAndTime = (TextView) rowView.findViewById(R.id.dateAndTime);
        TextView carAddress = (TextView) rowView.findViewById(R.id.carAddress);
        ImageView carThumbnailImageView = (ImageView) rowView.findViewById(R.id.carThumbnail);

        final CarData carData = data.get(position).carDetails;

        carTitleTextView.setText(carData.title);
        Picasso.with(mContext).load(carData.thumbnailURL).placeholder(R.drawable.models).into(carThumbnailImageView);

        if (carData.lng != null && carData.lat != null) {
            Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
            String locationAddress = "";

            try {
                final List<Address> addressArray = geocoder.getFromLocation(carData.lat, carData.lng, 1);

                if (addressArray.size() > 0) {
                     locationAddress = addressArray.get(0).getAddressLine(0);
                }
            } catch (IOException e) {
//                e.printStackTrace();
            }

            carAddress.setText((locationAddress.length() > 18) ? locationAddress.substring(0, 18) + "..." : locationAddress);
            carAddress.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        }

        pickupCal[0].setTimeInMillis(data.get(position).pickupTime.longValue());
        dateAndTime.setText(CompleteReservation.dateToString(pickupCal[0]));

        return rowView;
    }
}