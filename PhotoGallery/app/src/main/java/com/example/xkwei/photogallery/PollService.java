package com.example.xkwei.photogallery;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import java.util.List;

/**
 * Created by xkwei on 24/12/2016.
 */

public class PollService extends IntentService {

    private static final int POLL_INTERVAL = 1000 * 60;
    private static final String TAG = "PollService";

    public static final String REQUEST_CODE = "REQUEST_CODE";
    public static final String NOTIFICATION = "NOTIFICATION";
    public static final String ACTION_SHOW_NOTIFICATION = "com.example.xkwei.photogallery.SHOW_NOTIFICATION";
    public static final String PERM_PRIVATE = "com.example.xkwei.photogallery.PRIVATE";


    public static Intent newIntent(Context context){
        return new Intent(context,PollService.class);
    }
    public PollService(){
        super(TAG);
    }
    public static void setServiceAlarm(Context context,boolean isOn){
        Intent i =PollService.newIntent(context);
        PendingIntent pi = PendingIntent.getService(context,0,i,0);

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if(isOn){
            am.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,SystemClock.elapsedRealtime(),POLL_INTERVAL,pi);
        }
        else{
            am.cancel(pi);
            pi.cancel();
        }

        QueryPreference.setAlarmOn(context,isOn);
    }
    @Override
    public void onHandleIntent(Intent intent){
        if(!isNetworkAvailableAndConnected()){
            return;
        }

        List<GalleryItem> items;
        String query = QueryPreference.getStoredQuery(this);
        String lastResultId = QueryPreference.getLastResultId(this);

        if(null==query){
            items = new FlickrFetchr().fetchRecentPhotos();
        }
        else{
            items = new FlickrFetchr().searchPhotos(query);
        }

        if(0==items.size())return;

        String resultId = items.get(0).getId();
        if (resultId.equals(lastResultId)) {
            Log.i(TAG, "Got an old result: " + resultId);
        } else {
            Log.i(TAG, "Got a new result: " + resultId);
        }
        QueryPreference.setLastResultId(this, resultId);

        Resources res = getResources();
        Intent i = PhotoGalleryActivity.newIntent(this);
        PendingIntent pi = PendingIntent.getActivity(this,0,i,0);
        Notification ntf = new NotificationCompat.Builder(this)
                .setTicker(res.getString(R.string.new_pictures_title))
                .setSmallIcon(android.R.drawable.ic_menu_report_image)
                .setContentTitle(res.getString(R.string.new_pictures_title))
                .setContentText(res.getString(R.string.new_pictures_text))
                .setContentIntent(pi)
                .setAutoCancel(true)
                .build();
//        NotificationManagerCompat ntfm = NotificationManagerCompat.from(this);
//        ntfm.notify(0,ntf);
//        sendBroadcast(new Intent(ACTION_SHOW_NOTIFICATION),PERM_PRIVATE);
        showBackgroundNotification(0,ntf);
    }

    private void showBackgroundNotification(int requestCode,Notification notification){
        Intent i = new Intent(ACTION_SHOW_NOTIFICATION);
        i.putExtra(REQUEST_CODE,requestCode);
        i.putExtra(NOTIFICATION,notification);
        sendOrderedBroadcast(i,PERM_PRIVATE,null,null, Activity.RESULT_OK,null,null);
    }
    private boolean isNetworkAvailableAndConnected(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo()!=null && cm.getActiveNetworkInfo().isConnected();
    }

    public static boolean isServiceAlarmOn(Context context) {
        Intent i = PollService.newIntent(context);
        PendingIntent pi = PendingIntent
                .getService(context, 0, i, PendingIntent.FLAG_NO_CREATE);
        return pi != null;
    }
}
