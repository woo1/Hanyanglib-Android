package com.wooil.hanyanglib;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

/**
 * Created by wijang on 2017. 5. 3..
 */
public class WebviewActivity extends AppCompatActivity {
    private WebView mWebview;
    private WebSettings mWebSettings;
    String js;
    Boolean jsCalled = false;
    Boolean locUpdated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_activity);

        mWebview = (WebView)findViewById(R.id.webview);
        mWebview.getSettings().setJavaScriptEnabled(true);

        Intent intent = getIntent();
        String url = intent.getExtras().getString("url");
        String left = intent.getExtras().getString("left");
        String tot = intent.getExtras().getString("tot");
        js  = intent.getExtras().getString("js");
        String name = intent.getExtras().getString("name");

        setTitle(name);

        mWebview.loadUrl(url);
        mWebview.setWebViewClient(new WebViewClientClass());

        TextView leftTxtv = (TextView)findViewById(R.id.web_left);
        TextView totTxtv  = (TextView)findViewById(R.id.web_tot);

        leftTxtv.setText(left);
        totTxtv.setText("/"+tot);
    }

    private class WebViewClientClass extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if(js != null && !js.equals("") && jsCalled == false){
                view.loadUrl("javascript:"+js);
                jsCalled = true;
            }
            if(locUpdated == false){
                view.scrollBy(650, 900);
                locUpdated = true;
            }
        }
    }
}
