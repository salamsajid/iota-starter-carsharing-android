/**
 * Copyright 2016 IBM Corp. All Rights Reserved.
 *
 * Licensed under the IBM License, a copy of which may be obtained at:
 *
 * http://www14.software.ibm.com/cgi-bin/weblap/lap.pl?li_formnum=L-DDIN-AEGGZJ&popup=y&title=IBM%20IoT%20for%20Automotive%20Sample%20Starter%20Apps%20%28Android-Mobile%20and%20Server-all%29
 *
 * You may not use this file except in compliance with the license.
 */
package carsharing.starter.automotive.iot.ibm.com.mobilestarterapp.Profile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import carsharing.starter.automotive.iot.ibm.com.mobilestarterapp.ConnectedDriverAPI.DriverStatistics;
import carsharing.starter.automotive.iot.ibm.com.mobilestarterapp.ConnectedDriverAPI.ScoringBehavior;
import carsharing.starter.automotive.iot.ibm.com.mobilestarterapp.R;

public class ProfileDataAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater inflater;

    private ArrayList<DriverStatistics> data;
    private ArrayList<ScoringBehavior> behaviorData;
    private DriverStatistics.TimeRange timeRangeData;
    private DriverStatistics.SpeedPattern trafficConditionsData;
    private DriverStatistics.RoadType roadTypeData;


    // Integers that'll hold the number of behaviors each section will show
    private int behaviors;
    private int timeRanges;
    private int trafficConditions;
    private int roadTypes;

    public ProfileDataAdapter(Context context, ArrayList<DriverStatistics> stats, ArrayList<ScoringBehavior> behaviorArray, DriverStatistics.TimeRange timeRange, DriverStatistics.SpeedPattern trafficCondition, DriverStatistics.RoadType roadType) {
        mContext = context;

        data = stats;
        behaviorData = behaviorArray;
        timeRangeData = timeRange;
        trafficConditionsData = trafficCondition;
        roadTypeData = roadType;

        // NUMBER OF BEHAVIORS FOR EACH OF THE 4 SECTIONS
        behaviors = behaviorData.size();
        timeRanges = timeRangeData.timesOfDaySortedKeys.size();
        trafficConditions = trafficConditionsData.trafficConditionsSortedKeys.size();
        roadTypes = roadType.roadTypesSortedKeys.size();


        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() { return behaviors + timeRanges + trafficConditions + roadTypes; }

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
        View rowView = inflater.inflate(R.layout.list_item_profile, parent, false);

        String key = "";
        String value = "";

        Double totalDistance = 0.0;
        Map<String, Double> dict = new HashMap<String, Double>();

        TextView sectionTitle = (TextView) rowView.findViewById(R.id.sectionTitle);
        TextView behaviorTitle = (TextView) rowView.findViewById(R.id.behaviorTitle);
        TextView behaviorOccurences = (TextView) rowView.findViewById(R.id.behaviorOccurences);

        int section = 0;

        // FIND OUT WHAT SECTION THIS ITEM BELONGS TO OUT OF THE 4
        if (position < behaviors);
        else if ((position - behaviors) < timeRanges) section = 1;
        else if ((position - behaviors - timeRanges) < trafficConditions) section = 2;
        else section = 3;

        switch (section) {
            case 0:
                if (position == 0) {
                    sectionTitle.setText("Behaviors");
                } else {
                    sectionTitle.setVisibility(View.GONE);
                }

                key = behaviorData.get(position).name;
                value = behaviorData.get(position).count + "";

                Double totalPointsPerBehavior = (double) (100 / behaviors);
                Double pointsForThisBehavior = (behaviorData.get(position).score / 100) *  totalPointsPerBehavior;
                int pointsDeducted = (int) Math.round(pointsForThisBehavior - totalPointsPerBehavior);

                value += " (" + pointsDeducted + ")";

                break;

            case 1:
                int index = position - behaviors;

                if (index == 0) {
                    sectionTitle.setText("Time of Day");
                } else {
                    sectionTitle.setVisibility(View.GONE);
                }

                dict = timeRangeData.timesOfDay;
                key = timeRangeData.timesOfDaySortedKeys.get(index);
                totalDistance = timeRangeData.totalDistance;

                break;

            case 2:
                index = position - behaviors - timeRanges;

                if (index == 0) {
                    sectionTitle.setText("Traffic Conditions");
                } else {
                    sectionTitle.setVisibility(View.GONE);
                }

                dict = trafficConditionsData.trafficConditions;
                key = trafficConditionsData.trafficConditionsSortedKeys.get(index);
                totalDistance = trafficConditionsData.totalDistance;

                break;

            case 3:
                index = position - behaviors - timeRanges - trafficConditions;

                if (index == 0) {
                    sectionTitle.setText("Type of Road");
                } else {
                    sectionTitle.setVisibility(View.GONE);
                }

                dict = roadTypeData.roadTypes;
                key = roadTypeData.roadTypesSortedKeys.get(index);
                totalDistance = roadTypeData.totalDistance;

                break;
        }

        if (section != 0 && dict != null)
            value = Math.round((dict.get(key) / totalDistance) * 100) + "% (" + (Math.round(dict.get(key) / 16.0934) / 100) + " miles)";

        // Make sure the key is not longer than 25 characters
        behaviorTitle.setText((key.length() > 25) ? key.substring(0, 25) + "..." : key);
        behaviorOccurences.setText(value);

        return rowView;
    }
}