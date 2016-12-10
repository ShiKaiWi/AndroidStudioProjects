package com.example.xkwei.nerdlauncher;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * Created by xkwei on 10/12/2016.
 */

public class NerdLauncherFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private static final String LOG_TAG="nerdlauncherfragment";

    public static Fragment newInstance(){
        return new NerdLauncherFragment();
    }
    @Override
    public View onCreateView(LayoutInflater inf, ViewGroup vg, Bundle savedInstanceState){
        View v = inf.inflate(R.layout.fragment_nerd_launcher,vg,false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.fragment_nerd_launcher);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        setUpAdapter();
        return v;
    }

    private class ActivityHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ResolveInfo mResolveInfo;
        private TextView mTextView;
        private ImageView mImageView;


        public ActivityHolder(View itemView){
            super(itemView);
            mTextView = (TextView)itemView.findViewById(R.id.list_item_textview);
            mImageView = (ImageView)itemView.findViewById(R.id.list_item_imageview);
            itemView.setOnClickListener(this);
        }

        public void bindActivity(ResolveInfo relinfo){
            mResolveInfo = relinfo;
            PackageManager pm = getActivity().getPackageManager();
            String appName = mResolveInfo.loadLabel(pm).toString();
            Drawable appIcon = mResolveInfo.loadIcon(pm);
            mTextView.setText(appName);
            mImageView.setImageDrawable(appIcon);
        }

        @Override
        public void onClick(View v){
            ActivityInfo aif = mResolveInfo.activityInfo;
            Intent i = new Intent(Intent.ACTION_MAIN).setClassName(aif.applicationInfo.packageName,aif.name).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }
    }

    private class ActivityAdapter extends RecyclerView.Adapter<ActivityHolder>{
        private final List<ResolveInfo> mApps;

        public ActivityAdapter(List<ResolveInfo> apps){
            mApps = apps;
        }

        @Override
        public ActivityHolder onCreateViewHolder(ViewGroup parent,int viewType){
            LayoutInflater lif = LayoutInflater.from(getActivity());
            View view = lif.inflate(R.layout.recycler_view_list_item,parent,false);
            return new ActivityHolder(view);
        }

        @Override
        public void onBindViewHolder(ActivityHolder ahder,int position){
            ResolveInfo rif = mApps.get(position);
            ahder.bindActivity(rif);
        }

        @Override
        public int getItemCount(){
            return mApps.size();
        }

    }

    private void setUpAdapter(){
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        PackageManager pm = getActivity().getPackageManager();
        List<ResolveInfo> apps = pm.queryIntentActivities(intent,0);
        Log.i(LOG_TAG,"find "+apps.size()+" applications");
        Collections.sort(apps,new Comparator<ResolveInfo>(){
            public int compare(ResolveInfo a,ResolveInfo b){
                PackageManager pm = getActivity().getPackageManager();
                String as = a.loadLabel(pm).toString();
                String bs = b.loadLabel(pm).toString();
                return String.CASE_INSENSITIVE_ORDER.compare(as,bs);
            }
        });

        mRecyclerView.setAdapter(new ActivityAdapter(apps));

    }

}
