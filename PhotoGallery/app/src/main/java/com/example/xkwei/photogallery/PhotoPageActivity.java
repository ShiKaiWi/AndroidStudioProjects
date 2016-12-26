package com.example.xkwei.photogallery;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.webkit.WebView;

/**
 * Created by xkwei on 25/12/2016.
 */

public class PhotoPageActivity extends SingleFragmentActivity {

    public static Intent newIntent(Context context, Uri photoUri){
        Intent i = new Intent(context,PhotoPageActivity.class);
        i.setData(photoUri);
        return i;
    }

    @Override
    protected Fragment createFragment(){
        return PhotoPageFragment.newInstance(getIntent().getData());
    }

    @Override
    public void onBackPressed(){
        FragmentManager fm = getSupportFragmentManager();
        PhotoPageFragment fragment = (PhotoPageFragment) fm.findFragmentById(R.id.fragment_container);
        WebView wv = fragment.getWebView();
        if(wv.canGoBack()){
            wv.goBack();
        }
        else{
            super.onBackPressed();
        }
    }
}
