/**
 * Copyright 2016 IBM Corp. All Rights Reserved.
 * <p>
 * Licensed under the IBM License, a copy of which may be obtained at:
 * <p>
 * http://www14.software.ibm.com/cgi-bin/weblap/lap.pl?li_formnum=L-DDIN-AEGGZJ&popup=y&title=IBM%20IoT%20for%20Automotive%20Sample%20Starter%20Apps%20%28Android-Mobile%20and%20Server-all%29
 * <p>
 * You may not use this file except in compliance with the license.
 */
package carsharing.starter.automotive.iot.ibm.com.mobilestarterapp.Home;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import carsharing.starter.automotive.iot.ibm.com.mobilestarterapp.ConnectedDriverAPI.CarData;
import carsharing.starter.automotive.iot.ibm.com.mobilestarterapp.R;

public class CreateReservationAdapter extends BaseAdapter {
    static final GregorianCalendar[] pickupCal = {new GregorianCalendar()};
    static final GregorianCalendar[] dropoffCal = {new GregorianCalendar()};

    final private Context mContext;
    final private LayoutInflater inflater;
    final private CarData carData;
    final private String formattedAddr;

    EditText pickupTime;
    EditText dropoffTime;

    public CreateReservationAdapter(final Context context, final CarData data, final String formattedAddress) {
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
        final View rowView = inflater.inflate(R.layout.list_item_createreservation, parent, false);

        pickupTime = (EditText) rowView.findViewById(R.id.pickupTime);
        pickupTime.setText(dateToString(pickupCal[0]));

        // Add 5 hours to pickupTime
        dropoffCal[0].setTimeInMillis(pickupCal[0].getTimeInMillis() + 18000000);

        dropoffTime = (EditText) rowView.findViewById(R.id.dropoffTime);
        dropoffTime.setText(dateToString(dropoffCal[0]));

        calculateTime(rowView, pickupCal[0], dropoffCal[0]);

        final TextView locationAddress = (TextView) rowView.findViewById(R.id.locationAddress);
        locationAddress.setText(formattedAddr.length() > 22 ? formattedAddr.substring(0, 20) + "..." : formattedAddr);

        locationAddress.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);

        locationAddress.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    final Intent geoIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:" + carData.lat + "," + carData.lng + "?q=" + formattedAddr));
                    mContext.startActivity(geoIntent);
                }

                return false;
            }
        });

        pickupTime.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    DatePickerDialog dateDialog = new DatePickerDialog(rowView.getContext(), new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, final int year, final int monthOfYear, final int dayOfMonth) {
                            final TimePickerDialog timeDialog = new TimePickerDialog(rowView.getContext(), new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker view, final int hourOfDay, final int minute) {
                                    final GregorianCalendar calendar = new GregorianCalendar(year, monthOfYear, dayOfMonth, hourOfDay, minute);

                                    if (calendar.getTimeInMillis() <= dropoffCal[0].getTimeInMillis()) {
                                        pickupTime.setText(dateToString(calendar));
                                        pickupCal[0] = calendar;

                                        calculateTime(rowView, pickupCal[0], dropoffCal[0]);
                                    } else {
                                        final Toast toast = Toast.makeText(mContext, "Please choose a date & time that's before the Dropoff date & time", Toast.LENGTH_LONG);
                                        toast.show();
                                    }
                                }
                            }, pickupCal[0].get(Calendar.HOUR_OF_DAY), pickupCal[0].get(Calendar.MINUTE), true);
                            timeDialog.show();
                        }
                    }, pickupCal[0].get(Calendar.YEAR), pickupCal[0].get(Calendar.MONTH), pickupCal[0].get(Calendar.DAY_OF_MONTH));
                    dateDialog.show();
                }

                return false;
            }
        });

        dropoffTime.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    final DatePickerDialog dateDialog = new DatePickerDialog(rowView.getContext(), new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, final int year, final int monthOfYear, final int dayOfMonth) {
                            final TimePickerDialog timeDialog = new TimePickerDialog(rowView.getContext(), new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker view, final int hourOfDay, final int minute) {
                                    final GregorianCalendar calendar = new GregorianCalendar(year, monthOfYear, dayOfMonth, hourOfDay, minute);

                                    if (calendar.getTimeInMillis() >= pickupCal[0].getTimeInMillis()) {
                                        dropoffTime.setText(dateToString(calendar));
                                        dropoffCal[0] = calendar;

                                        calculateTime(rowView, pickupCal[0], dropoffCal[0]);
                                    } else {
                                        final Toast toast = Toast.makeText(mContext, "Please choose a date & time that's after the Pickup date & time", Toast.LENGTH_LONG);
                                        toast.show();
                                    }
                                }
                            }, dropoffCal[0].get(Calendar.HOUR_OF_DAY), dropoffCal[0].get(Calendar.MINUTE), true);
                            timeDialog.show();
                        }
                    }, dropoffCal[0].get(Calendar.YEAR), dropoffCal[0].get(Calendar.MONTH), dropoffCal[0].get(Calendar.DAY_OF_MONTH));
                    dateDialog.show();
                }

                return false;
            }
        });

        return rowView;
    }

    private String dateToString(final GregorianCalendar cal) {
        final SimpleDateFormat formattedCal = new SimpleDateFormat("MMM dd, yyyy, hh:mm a");
        formattedCal.setCalendar(cal);

        final String dateFormatted = formattedCal.format(cal.getTime());

        return dateFormatted;
    }

    private void calculateTime(View rowView, GregorianCalendar pickupCal, GregorianCalendar dropoffCal) {
        long diffInSecs = (dropoffCal.getTimeInMillis() - pickupCal.getTimeInMillis()) / 1000;

        final double days = Math.floor(diffInSecs / 86400);
        diffInSecs -= days * 86400;

        final double hours = Math.floor(diffInSecs / 3600) % 24;
        diffInSecs -= hours * 3600;

        final double mins = Math.floor(diffInSecs / 60) % 60;

        String durationString = "";
        int cost = 0;

        if (days > 0) {
            durationString = (days == 1 ? "1 day, " : (int) days + " days, ");
            cost += (int) days * 40;
        }
        if (hours > 0) {
            durationString += (hours == 1 ? "1 hour " : (int) hours + " hours ");
            cost += (int) hours * 15;
        }
        if (days == 0 && hours == 0) {
            durationString = "1 hour";
        }

        final TextView hourDifference = (TextView) rowView.findViewById(R.id.hourDifference);
        hourDifference.setText(durationString);

        final TextView estCost = (TextView) rowView.findViewById(R.id.estCost);
        estCost.setText("$" + cost);
    }
}