package com.example.xkwei.photogallery;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewGroupCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;


/**
 * Created by xkwei on 19/12/2016.
 */

public class PhotoGalleryFragment extends Fragment {

    private RecyclerView mPhotoRecyclerView;
    private List<GalleryItem> mGalleryItems;

    private Handler mHandler;
    private ThumbnailDownloader<PhotoHolder> mThumbnailDownloader;

    private static final String TAG = "PhotoGalleryFragment";
    public static PhotoGalleryFragment newInstance(){
        return new PhotoGalleryFragment();
    }

    private class FetchItemTask extends AsyncTask<Void,Void,List<GalleryItem>> {
        @Override
        protected List<GalleryItem> doInBackground(Void... params){
            return new FlickrFetchr().fetchItems();

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
        new FetchItemTask().execute();
        mHandler = new Handler();
        mThumbnailDownloader = new ThumbnailDownloader<>(mHandler);
        mThumbnailDownloader.setThumbnailDownloadListener(new ThumbnailDownloader
                .ThumbnailDownloadListener<PhotoHolder>()  {
                  @Override
                  public void onThumbnailDownloaded(PhotoHolder target, Bitmap thumbnail) {
                      Drawable pic = new BitmapDrawable(getResources(),thumbnail);
                      target.bindGalleryItem(pic);
                  }
              }
        );
        mThumbnailDownloader.start();
        mThumbnailDownloader.getLooper();
        Log.i(TAG,"background thread started");
    }

    @Override
    public View onCreateView(LayoutInflater lif, ViewGroup container,Bundle savedInstanceState){
        View v = lif.inflate(R.layout.fragment_photo_gallery,container,false);
        mPhotoRecyclerView = (RecyclerView) v.findViewById(R.id.fragment_photo_gallery_recycler_view);
        mPhotoRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),3));
        return v;
    }

    private class PhotoHolder extends RecyclerView.ViewHolder{
        ImageView mImageView;
        public PhotoHolder(View itemView){
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.fragment_photo_gallery_image_view);
        }

        public void bindGalleryItem(Drawable picture){
            if(null!=picture)
                mImageView.setImageDrawable(picture);
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
            phd.bindGalleryItem(pic);
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
