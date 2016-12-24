package com.example.xkwei.photogallery;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xkwei on 19/12/2016.
 */

public class FlickrFetchr {
    private static final String TAG = "FlickrFetchr";
    private static final String API_KEY = "11229ac56afc4eb93a3d2ed43cc5b893";

    private static final String FETCH_RECENTS_METHOD = "flickr.photos.getRecent";
    private static final String SEARCH_METHOD = "flickr.photos.search";
    private static final Uri ENDPOINT = Uri.parse("https://api.flickr.com/services/rest/")
            .buildUpon()
            .appendQueryParameter("api_key",API_KEY)
            .appendQueryParameter("format","json")
            .appendQueryParameter("nojsoncallback","1")
            .appendQueryParameter("extras","url_s")
            .build();
    public String getStringUrl (String urlSpec)throws IOException{
        return new String(getUrlBytes(urlSpec));
    }

    private List<GalleryItem> parseItems(JSONObject jsonBody){
        List<GalleryItem> items = new ArrayList<GalleryItem>();

        try {
            JSONObject photoObject = jsonBody.getJSONObject("photos");
            JSONArray photos = photoObject.getJSONArray("photo");

            for(int i=0;i<photos.length();i++)
            {
                JSONObject item = photos.getJSONObject(i);
                GalleryItem galleryItem = new GalleryItem();
                galleryItem.setId(item.getString("id"));
                galleryItem.setCaption(item.getString("title"));
                if(item.has("url_s")){
                    galleryItem.setUrl(item.getString("url_s"));
                }
                items.add(galleryItem);
            }

        }catch(JSONException je){
            Log.e(TAG,"fail to parse json file",je);
        }

        return items;
    }


    private List<GalleryItem> downloadGalleryItems(String url){
        List<GalleryItem> galleryItems = null;
        try{
            String jsonStr = getStringUrl(url);
            JSONObject jsonBody = new JSONObject(jsonStr);
            galleryItems =  parseItems(jsonBody);

        }catch(JSONException je)
        {
            Log.e(TAG,"fail to get the json body",je);
        }
        catch(IOException e){
            Log.e(TAG,"fail to get the json item",e);
        }
        return galleryItems;
    }

    private String buildUrl(String method,String query){
        Uri.Builder uriBuilder = ENDPOINT.buildUpon().appendQueryParameter("method",method);

        if(method.equals(SEARCH_METHOD)) {
            uriBuilder.appendQueryParameter("text", query);
        }
        return uriBuilder.build().toString();
    }

    public List<GalleryItem> fetchRecentPhotos(){
        String url = buildUrl(FETCH_RECENTS_METHOD,null);
        return downloadGalleryItems(url);
    }

    public List<GalleryItem> searchPhotos(String query){
        String url = buildUrl(SEARCH_METHOD,query);
        return downloadGalleryItems(url);
    }


    public byte[] getUrlBytes(String urlSpec) throws IOException{
        URL url;
        HttpURLConnection connection=null;
        ByteArrayOutputStream out = null;

        try{
            url = new URL(urlSpec);
            connection = (HttpURLConnection)url.openConnection();
            out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();
            if(connection.getResponseCode()!=HttpURLConnection.HTTP_OK)
                throw new IOException(connection.getResponseMessage()+": with "+urlSpec);

            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while((bytesRead=in.read(buffer))>0)
                out.write(buffer,0,bytesRead);
            out.close();
        }catch (IOException e){
            throw e;
        }
        catch(Exception e){

        }finally{
            if(connection!=null)
                connection.disconnect();
            if(out!=null)
                return out.toByteArray();
            else
                return null;
        }
    }
}
