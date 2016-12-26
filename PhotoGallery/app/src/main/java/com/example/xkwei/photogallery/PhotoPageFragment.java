package com.example.xkwei.photogallery;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

/**
 * Created by xkwei on 25/12/2016.
 */

public class PhotoPageFragment extends VisibleFragment {
    private static final String ARG_URI="photo_page_uri";

    private Uri mUri;

    public WebView getWebView() {
        return mWebView;
    }

    private WebView mWebView;
    private ProgressBar mProgressBar;

    public static PhotoPageFragment newInstance(Uri uri){
        Bundle args = new Bundle();
        args.putParcelable(ARG_URI,uri);

        PhotoPageFragment ppf = new PhotoPageFragment();
        ppf.setArguments(args);
        return ppf;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mUri = getArguments().getParcelable(ARG_URI);
    }
    @Override
    public View onCreateView(LayoutInflater lif, ViewGroup container,Bundle savedInstanceState){
        super.onCreateView(lif,container,savedInstanceState);
        View v = lif.inflate(R.layout.fragment_photo_page,container,false);
        mWebView = (WebView) v.findViewById(R.id.fragment_photo_page_web_view);
        mProgressBar = (ProgressBar) v.findViewById(R.id.fragment_photo_page_progress_bar);
        mProgressBar.setMax(100);

        //set the url of the webview
        mWebView.loadUrl(mUri.toString());

        //enable javascript
        mWebView.getSettings().setJavaScriptEnabled(true);

        //override webclient
        mWebView.setWebViewClient(new WebViewClient(){
            public boolean shouldOverrideUrlLoading(WebView wv,WebResourceRequest request){
                String protocol = request.getRequestHeaders().get("scheme");
                if(protocol.equals("https")||protocol.equals("http"))
                    return false;
                else{
                    Intent i = new Intent(Intent.ACTION_VIEW,request.getUrl());
                    startActivity(i);
                    return true;
                }
            }
            
            public boolean shouldOverrideUrlLoading(WebView wv,String url){
                String protocol = url.split(":")[0];
                if(protocol.equals("https")||protocol.equals("http"))
                    return false;
                else{
                    Intent i = new Intent(Intent.ACTION_VIEW,Uri.parse(url));
                    if(i.resolveActivity(getActivity().getPackageManager())!=null) {
                        startActivity(i);
                        return true;
                    }
                    return false;
                }
            }
        });

        //Override webChromeClient
        mWebView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView wv,int newProgress){
                if(100==newProgress){
                    mProgressBar.setVisibility(View.GONE);
                }
                else{
                    mProgressBar.setVisibility(View.VISIBLE);
                    mProgressBar.setProgress(newProgress);
                }
            }

            @Override
            public void onReceivedTitle(WebView wv,String title){
                AppCompatActivity activity = (AppCompatActivity) getActivity();
                activity.getSupportActionBar().setSubtitle(title);
            }
        });
        return v;
    }
}
