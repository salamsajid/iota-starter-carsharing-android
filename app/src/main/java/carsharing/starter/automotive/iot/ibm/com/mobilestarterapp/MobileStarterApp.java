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

import android.app.Application;
import android.content.Context;

public class MobileStarterApp extends Application {
    private static Context context;

    public void onCreate() {
        super.onCreate();

        MobileStarterApp.context = getApplicationContext();
    }

    public static Context appContext() {
        return MobileStarterApp.context;
    }
}