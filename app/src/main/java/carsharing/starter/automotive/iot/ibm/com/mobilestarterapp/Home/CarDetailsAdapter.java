/**
 * Copyright 2016 IBM Corp. All Rights Reserved.
 *
 * Licensed under the IBM License, a copy of which may be obtained at:
 *
 * http://www14.software.ibm.com/cgi-bin/weblap/lap.pl?li_formnum=L-DDIN-AEGGZJ&popup=y&title=IBM%20IoT%20for%20Automotive%20Sample%20Starter%20Apps%20%28Android-Mobile%20and%20Server-all%29
 *
 * You may not use this file except in compliance with the license.
 */
package carsharing.starter.automotive.iot.ibm.com.mobilestarterapp.Home;

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
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import carsharing.starter.automotive.iot.ibm.com.mobilestarterapp.R;

import carsharing.starter.automotive.iot.ibm.com.mobilestarterapp.ConnectedDriverAPI.CarData;

public class CarDetailsAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater inflater;
    private CarData carData;
    private String formattedAddr;

    public CarDetailsAdapter(Context context, CarData data, String formattedAddress) {
        mContext = context;
        carData = data;
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        formattedAddr = formattedAddress;
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public Object getItem(int index) {
        return carData.title;
    }

    @Override
    public long getItemId(int index) {
        return index;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = inflater.inflate(R.layout.list_item_cardetails, parent, false);

        TextView dateValueTextView = (TextView) rowView.findViewById(R.id.dateValue);
        TextView locationAddress = (TextView) rowView.findViewById(R.id.locationAddress);
        TextView priceValueTextView = (TextView) rowView.findViewById(R.id.priceValue);

        TextView makeModelValueTextView = (TextView) rowView.findViewById(R.id.makeModelValue);
        TextView yearValueTextView = (TextView) rowView.findViewById(R.id.yearValue);
        TextView mileageValueTextView = (TextView) rowView.findViewById(R.id.mileageValue);

        dateValueTextView.setText(carData.availability);
        priceValueTextView.setText("$" + carData.hourlyRate + "/hr, $" + carData.dailyRate + "/day");

        makeModelValueTextView.setText(carData.makeModel);
        yearValueTextView.setText(carData.year + "");

        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
        String mileage = numberFormat.format(carData.mileage);

        mileageValueTextView.setText(mileage);

        locationAddress.setText(formattedAddr.length() > 18 ? formattedAddr.substring(0, 18) + "..." : formattedAddr);

        locationAddress.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);

        locationAddress.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Intent geoIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:" + carData.lat + "," + carData.lng + "?q=" + formattedAddr));
                    mContext.startActivity(geoIntent);
                }

                return false;
            }
        });

        return rowView;
    }


}