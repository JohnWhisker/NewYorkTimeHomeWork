package com.example.johnw.nytime.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.johnw.nytime.R;
import com.example.johnw.nytime.Types.Article;

public class ArticleActivity extends AppCompatActivity {

    // INITIALIZE FUNCTION
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
        Article article = (Article)getIntent().getSerializableExtra("article");
        WebView webview = (WebView) findViewById(R.id.wvArticle);
        webview.setWebViewClient(new WebViewClient()
        {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
               view.loadUrl(url);
                return true;
            }
        });
        webview.loadUrl(article.getWeburl());
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_article, menu);
        MenuItem item = menu.findItem(R.id.Share);
        ShareActionProvider miShare = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        WebView wvArticle = (WebView) findViewById(R.id.wvArticle);
        shareIntent.putExtra(Intent.EXTRA_TEXT, wvArticle.getUrl());
        if(miShare!= null) {
            miShare.setShareIntent(shareIntent);
        }
        return super.onCreateOptionsMenu(menu);
    }

}
