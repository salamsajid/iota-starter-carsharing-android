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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import carsharing.starter.automotive.iot.ibm.com.mobilestarterapp.R;

import carsharing.starter.automotive.iot.ibm.com.mobilestarterapp.ConnectedDriverAPI.CarData;

import java.util.ArrayList;

public class CarDataAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater inflater;
    private ArrayList<CarData> data;

    public CarDataAdapter(Context context, ArrayList<CarData> carsArray) {
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
        View rowView = inflater.inflate(R.layout.list_item_cardata, parent, false);

        TextView carTitleTextView = (TextView) rowView.findViewById(R.id.carTitle);

        TextView carDistanceTextView = (TextView) rowView.findViewById(R.id.dateAndTime);

        TextView carStarsTextView = (TextView) rowView.findViewById(R.id.carStars);

        TextView carPriceTextView = (TextView) rowView.findViewById(R.id.carPrice);

        ImageView carThumbnailImageView = (ImageView) rowView.findViewById(R.id.carThumbnail);

        ImageView carRecommended = (ImageView) rowView.findViewById(R.id.carRecommended);

        carTitleTextView.setText(data.get(position).title);
        carDistanceTextView.setText(data.get(position).distance + " meters away");

        String stars = new String(new char[data.get(position).stars]).replace("\0", "\u2605");
        String emptyStars = new String(new char[5 - data.get(position).stars]).replace("\0", "\u2606");
        carStarsTextView.setText(stars + emptyStars);

        carPriceTextView.setText("$" + data.get(position).hourlyRate + "/hr, $" + data.get(position).dailyRate + "/day");

        if (position > 0) {
            carRecommended.setVisibility(View.GONE);
        }

        Picasso.with(mContext).load(data.get(position).thumbnailURL).placeholder(R.drawable.models).into(carThumbnailImageView);

        return rowView;
    }


}