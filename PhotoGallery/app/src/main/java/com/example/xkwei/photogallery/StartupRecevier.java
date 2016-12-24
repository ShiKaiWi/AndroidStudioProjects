package com.example.xkwei.photogallery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by xkwei on 24/12/2016.
 */

public class StartupRecevier extends BroadcastReceiver {
    private static final String TAG = "StartupReceiver";

    @Override
    public void onReceive(Context context,Intent i){
        PollService.setServiceAlarm(context,QueryPreference.isAlarmOn(context));
    }
}
