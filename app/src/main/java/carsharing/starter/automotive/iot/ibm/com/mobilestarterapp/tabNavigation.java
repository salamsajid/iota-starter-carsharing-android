/**
 * Copyright 2016 IBM Corp. All Rights Reserved.
 *
 * Licensed under the IBM License, a copy of which may be obtained at:
 *
 * http://www14.software.ibm.com/cgi-bin/weblap/lap.pl?li_formnum=L-DDIN-AEGGZJ&popup=y&title=IBM%20IoT%20for%20Automotive%20Sample%20Starter%20Apps%20%28Android-Mobile%20and%20Server-all%29
 *
 * You may not use this file except in compliance with the license.
 */
package carsharing.starter.automotive.iot.ibm.com.mobilestarterapp;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Arrays;

import carsharing.starter.automotive.iot.ibm.com.mobilestarterapp.Home.CarBrowse;
import carsharing.starter.automotive.iot.ibm.com.mobilestarterapp.Profile.Profile;
import carsharing.starter.automotive.iot.ibm.com.mobilestarterapp.Reservations.Reservations;
import carsharing.starter.automotive.iot.ibm.com.mobilestarterapp.Trips.Trips;

public class tabNavigation extends AppCompatActivity {
    private CarBrowse carBrowse;
    private Reservations reservations;
    private Profile profile;
    private Trips trips;

    private TabLayout tabBar;

    private ArrayList<Fragment> fragments = new ArrayList<Fragment>();
    private ArrayList<String> tabTitles = new ArrayList<String>(Arrays.asList("Search", "Reservations", "Profile", "Trips"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_navigation);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);

        tabBar = (TabLayout) findViewById(R.id.tabBar);

        tabBar.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                getItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        initalizeFragments();
        getTabTitle();
    }

    private void initalizeFragments() {
        carBrowse = new CarBrowse();
        fragments.add(carBrowse);

        reservations = new Reservations();
        fragments.add(reservations);

        profile = new Profile();
        fragments.add(profile);

        trips = new Trips();
        fragments.add(trips);
    }

    private void getItem(int position)
    {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.tabsFrame, fragments.get(position));
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
    }

    private void getTabTitle() {
        for (int i = 0; i < tabTitles.size(); i++) {
            if (i == 0) {
                tabBar.addTab(tabBar.newTab().setText(tabTitles.get(i)), true);
            } else {
                tabBar.addTab(tabBar.newTab().setText(tabTitles.get(i)));
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
