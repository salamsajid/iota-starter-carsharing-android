/**
 * Copyright 2016 IBM Corp. All Rights Reserved.
 * <p>
 * Licensed under the IBM License, a copy of which may be obtained at:
 * <p>
 * http://www14.software.ibm.com/cgi-bin/weblap/lap.pl?li_formnum=L-DDIN-AEGGZJ&popup=y&title=IBM%20IoT%20for%20Automotive%20Sample%20Starter%20Apps%20%28Android-Mobile%20and%20Server-all%29
 * <p>
 * You may not use this file except in compliance with the license.
 */
package carsharing.starter.automotive.iot.ibm.com.mobilestarterapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

public class ActivityHelper {

    public static Intent createIntentNoHistory(final Context context, final Class<?> klass) {
        final Intent intent = new Intent(context, klass);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        return intent;
    }

    public static void jumpToFirstPage(AppCompatActivity activity) {
        activity.finishAffinity();
        Intent intent = new Intent(activity, FirstPage.class);
        activity.startActivity(intent);
    }
}
