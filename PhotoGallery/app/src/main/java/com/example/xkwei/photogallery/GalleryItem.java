package com.example.xkwei.photogallery;

/**
 * Created by xkwei on 19/12/2016.
 */

public class GalleryItem {
    private String mCaption;
    private String mId;
    private String mUrl;

    public void setCaption(String caption) {
        mCaption = caption;
    }

    public void setId(String id) {
        mId = id;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public String getCaption() {

        return mCaption;
    }

    public String getId() {
        return mId;
    }

    public String getUrl() {
        return mUrl;
    }

    @Override
    public String toString(){
        return mCaption;
    }
}
