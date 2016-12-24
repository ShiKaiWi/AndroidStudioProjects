package com.example.xkwei.photogallery;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by xkwei on 24/12/2016.
 */

public abstract class VisibleFragment extends Fragment {
    private static final String TAG = "VisibleFragment";
    private BroadcastReceiver mShowNotification = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context,Intent intent){
//            Toast.makeText(getActivity(),"Got a broadcast: "+intent.getAction(),Toast.LENGTH_LONG).show();
            Log.i(TAG, "canceling notification");
            setResultCode(Activity.RESULT_CANCELED);
        }
    };

    @Override
    public void onStart(){
        super.onStart();
        IntentFilter intentFilter = new IntentFilter(PollService.ACTION_SHOW_NOTIFICATION);
        getActivity().registerReceiver(mShowNotification,intentFilter,PollService.PERM_PRIVATE,null);
    }

    @Override
    public void onStop(){
        super.onStop();
        getActivity().unregisterReceiver(mShowNotification);
    }
}
