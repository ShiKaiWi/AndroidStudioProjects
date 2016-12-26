package com.example.xkwei.photogallery;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewGroupCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;


/**
 * Created by xkwei on 19/12/2016.
 */

public class PhotoGalleryFragment extends VisibleFragment {

    private RecyclerView mPhotoRecyclerView;
    private List<GalleryItem> mGalleryItems;

    private Handler mHandler;
    private ThumbnailDownloader<PhotoHolder> mThumbnailDownloader;

    private static final String TAG = "PhotoGalleryFragment";
    public static PhotoGalleryFragment newInstance(){
        return new PhotoGalleryFragment();
    }

    private class FetchItemTask extends AsyncTask<Void,Void,List<GalleryItem>> {
        private String mQuery;
        public FetchItemTask(String s){mQuery = s;}
        @Override
        protected List<GalleryItem> doInBackground(Void... params){

            if(null!=mQuery){
                return new FlickrFetchr().searchPhotos(mQuery);
            }
            return new FlickrFetchr().fetchRecentPhotos();

        }
        @Override
        public void onPostExecute(List<GalleryItem> galleryItems){
            mGalleryItems = galleryItems;
            setupAdapter();
        }
    }



    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        updateItems();
        mHandler = new Handler();
        mThumbnailDownloader = new ThumbnailDownloader<>(mHandler);
        mThumbnailDownloader.setThumbnailDownloadListener(new ThumbnailDownloader
                .ThumbnailDownloadListener<PhotoHolder>()  {
                  @Override
                  public void onThumbnailDownloaded(PhotoHolder target, Bitmap thumbnail) {
                      Drawable pic = new BitmapDrawable(getResources(),thumbnail);
                      target.bindPicture(pic);
                  }
              }
        );
        mThumbnailDownloader.start();
        mThumbnailDownloader.getLooper();
        Log.i(TAG,"background thread started");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater mif){
        super.onCreateOptionsMenu(menu,mif);
        mif.inflate(R.menu.fragment_photo_gallery,menu);

        MenuItem toggleItem = menu.findItem(R.id.menu_item_toggle_polling);
        if (PollService.isServiceAlarmOn(getActivity())) {
            toggleItem.setTitle(R.string.stop_polling);
        } else {
            toggleItem.setTitle(R.string.start_polling);
        }

        MenuItem searchItem = menu.findItem(R.id.menu_item_search);
        final SearchView sv = (SearchView) searchItem.getActionView();

        sv.setOnQueryTextListener( new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextSubmit(String s){
                Log.d(TAG,"Query Search Text Submitted "+s);
                QueryPreference.setStoredQuery(getActivity(),s);
                updateItems();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s){
                Log.d(TAG, "QueryTextChange: " + s);
                return false;
            }
        });

        sv.setOnClickListener(new SearchView.OnClickListener(){
            @Override
            public void onClick(View v){
                sv.setQuery(QueryPreference.getStoredQuery(getActivity()),false);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_clear:
                QueryPreference.setStoredQuery(getActivity(), null);
                updateItems();
                return true;
            case R.id.menu_item_toggle_polling:
                boolean isShouldStartPollingService = !PollService.isServiceAlarmOn(getActivity());
                PollService.setServiceAlarm(getActivity(),isShouldStartPollingService);
                getActivity().invalidateOptionsMenu();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateItems() {
        new FetchItemTask(QueryPreference.getStoredQuery(getActivity())).execute();
    }

    @Override
    public View onCreateView(LayoutInflater lif, ViewGroup container,Bundle savedInstanceState){
        View v = lif.inflate(R.layout.fragment_photo_gallery,container,false);
        mPhotoRecyclerView = (RecyclerView) v.findViewById(R.id.fragment_photo_gallery_recycler_view);
        mPhotoRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),3));
        return v;
    }

    private class PhotoHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView mImageView;
        GalleryItem mGalleryItem;
        public PhotoHolder(View itemView){
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.fragment_photo_gallery_image_view);
            itemView.setOnClickListener(this);
        }

        public void bindGalleryItem(GalleryItem galleryItem){
            mGalleryItem = galleryItem;
        }

        public void bindPicture(Drawable picture){
            if(null!=picture)
                mImageView.setImageDrawable(picture);
        }
        @Override
        public void onClick(View v){
//            Intent i = new Intent(Intent.ACTION_VIEW,mGalleryItem.getPhotoPageUri());
            Intent i = PhotoPageActivity.newIntent(getActivity(),mGalleryItem.getPhotoPageUri());
            startActivity(i);
        }

    }

    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder>{
        List<GalleryItem> mItems;

        public PhotoAdapter(List<GalleryItem> items){
            mItems = items;
        }

        @Override
        public PhotoHolder onCreateViewHolder(ViewGroup vg,int viewType){
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View v = inflater.inflate(R.layout.gallery_item,vg,false);
            return new PhotoHolder(v);
        }

        @Override
        public void onBindViewHolder(PhotoHolder phd,int position){
            GalleryItem git = mItems.get(position);
            Drawable pic = null;
            phd.bindPicture(pic);
            phd.bindGalleryItem(git);
            mThumbnailDownloader.queueThumbnail(phd,git.getUrl());
        }

        @Override
        public int getItemCount(){
            return mItems.size();
        }
    }

    private void setupAdapter(){
        if(isAdded()){
            mPhotoRecyclerView.setAdapter(new PhotoAdapter(mGalleryItems));
        }
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        mThumbnailDownloader.clearQueue();
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        mThumbnailDownloader.quit();
        Log.i(TAG,"background thread is destroyed");
    }
}
