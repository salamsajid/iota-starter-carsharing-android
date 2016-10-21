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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;

import carsharing.starter.automotive.iot.ibm.com.mobilestarterapp.R;

public class TripDetailsDataAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater inflater;
    private Map data;

    public TripDetailsDataAdapter(Context context, Map behavior) {
        mContext = context;
        data = behavior;
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
        View rowView = inflater.inflate(R.layout.list_item_tripdetails, parent, false);

        TextView behaviorTitle = (TextView) rowView.findViewById(R.id.behaviorTitle);
        TextView behaviorOccurences = (TextView) rowView.findViewById(R.id.behaviorOccurences);

        String behaviorName = (String) data.keySet().toArray()[position];
        behaviorTitle.setText(behaviorName);

        ArrayList<ArrayList<LatLng>> occurences = (ArrayList<ArrayList<LatLng>>) data.get(behaviorName);
        behaviorOccurences.setText(occurences.size() + "");

        return rowView;
    }
}