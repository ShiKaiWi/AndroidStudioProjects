package com.example.xkwei.photogallery;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.util.LruCache;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


/**
 * Created by xkwei on 19/12/2016.
 */

public class ThumbnailDownloader<T> extends HandlerThread {

    private static final String TAG = "ThumbnailDownloader";
    private static final int DOWNLOAD_PIC = 0;
    private Handler mRequestHandler;
    private ConcurrentMap<T,String> mRequestMap = new ConcurrentHashMap<>();
    private ThumbnailDownloadListener<T> mThumbnailDownloadListener;
    private Handler mResponseHandler;
    private int mCacheSize = 4*1024*1024;
    private LruCache<String,Bitmap> mPictureCache = new LruCache<String,Bitmap>(mCacheSize){
        @Override
        protected int sizeOf(String key,Bitmap value){
            return value.getByteCount();
        }
    };
    private Boolean mHasQuit = false;
    public ThumbnailDownloader(Handler responseHandler){
        super(TAG);
        mResponseHandler = responseHandler;
    }


    public interface ThumbnailDownloadListener<T>{
        void onThumbnailDownloaded(T target,Bitmap thumbnail);
    }

    public void setThumbnailDownloadListener(ThumbnailDownloadListener tdl){
        mThumbnailDownloadListener = tdl;
    }

    @Override
    public boolean quit(){
        mHasQuit = true;
        return super.quit();
    }

    @Override
    protected void onLooperPrepared(){
        mRequestHandler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                if(msg.what==DOWNLOAD_PIC){
                    T target = (T)msg.obj;
                    handleRequest(target);
                }
            }
        };
    }
    public void queueThumbnail(T target,String url){

        if(url==null)
            mRequestMap.remove(target);
        else{
            mRequestMap.put(target,url);
            Message msg = mRequestHandler.obtainMessage(DOWNLOAD_PIC,target);
            msg.sendToTarget();
        }

    }

    private void handleRequest(final T target){


        final String url = mRequestMap.get(target);
        if(null==url){
            return;
        }

        try{
            byte[] bitmapBytes = new FlickrFetchr().getUrlBytes(url);
            final Bitmap bitmap;
            if(null!=mPictureCache.get(url)){
                bitmap = mPictureCache.get(url);
                Log.i(TAG,"get the cached picture: "+url);
            }
            else{
                bitmap = BitmapFactory.decodeByteArray(bitmapBytes,0,bitmapBytes.length);
                mPictureCache.put(url,bitmap);
                Log.i(TAG,"downloaded the picture from "+url);
            }



            mResponseHandler.post(new Runnable(){
                @Override
                public void run(){
                    if(mRequestMap.get(target)!=url || mHasQuit){
                        return;
                    }

                    mRequestMap.remove(target);
                    mThumbnailDownloadListener.onThumbnailDownloaded(target,bitmap);
                }
            });
        }catch(IOException ie){
            Log.i(TAG,"failed to download the picture from "+url,ie);
        }
    }
    public void clearQueue() {
        mRequestHandler.removeMessages(DOWNLOAD_PIC);
    }
}
