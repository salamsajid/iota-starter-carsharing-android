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
import android.widget.TextView;

import java.io.IOException;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import carsharing.starter.automotive.iot.ibm.com.mobilestarterapp.ConnectedDriverAPI.ReservationsData;
import carsharing.starter.automotive.iot.ibm.com.mobilestarterapp.R;

public class CompleteReservationAdapter extends BaseAdapter {
    final private Context mContext;
    final private LayoutInflater inflater;
    final private ReservationsData reservationData;
    static final GregorianCalendar[] pickupCal = { new GregorianCalendar() };

    public CompleteReservationAdapter(final Context context, final ReservationsData data) {
        mContext = context;
        reservationData = data;
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public Object getItem(int index) {
        return reservationData.carDetails.title;
    }

    @Override
    public long getItemId(int index) {
        return index;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View rowView = inflater.inflate(R.layout.list_item_completereservation, parent, false);

        final TextView pickupValueTextView = (TextView) rowView.findViewById(R.id.pickupValue);
        final TextView fromValueTextView = (TextView) rowView.findViewById(R.id.fromValue);
        final TextView plateValueTextView = (TextView) rowView.findViewById(R.id.plateValue);
        final TextView billValueTextView = (TextView) rowView.findViewById(R.id.billValue);

//        dateValueTextView.setText(carData.availability);
//        priceValueTextView.setText("$" + carData.hourlyRate + "/hr, $" + carData.dailyRate + "/day");

        pickupCal[0].setTimeInMillis(reservationData.pickupTime.longValue());
        pickupValueTextView.setText(CompleteReservation.dateToString(pickupCal[0]));

        final Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
        try {
            final List<Address> addressArray = geocoder.getFromLocation(reservationData.carDetails.lat, reservationData.carDetails.lng, 1);

            final String locationAddress = addressArray.get(0).getAddressLine(0);

            fromValueTextView.setText((locationAddress.length() > 18) ? locationAddress.substring(0, 18) + "..." : locationAddress);


            fromValueTextView.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);

            fromValueTextView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        final Intent geoIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:" + reservationData.carDetails.lat + "," + reservationData.carDetails.lng + "?q=" + addressArray.get(0).getAddressLine(0)));
                        geoIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        mContext.startActivity(geoIntent);
                    }

                    return false;
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rowView;
    }
}