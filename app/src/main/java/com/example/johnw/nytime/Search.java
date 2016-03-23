package com.example.johnw.nytime;

import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class Search extends AppCompatActivity implements SingleChoiceDialogFrqgment.SelectionListener{


    String KEY = "347a6b323c94cbf625b8de7c59a23a2d:18:74723396";
    GridView gvResult;
    ArrayList<Article> articles;
    ArticleArrayAdapter adapter;
    ActionBar actionBar;
    String sortBy;
    Menu menu;
    String beginDate,endDate,field;
    public String query;
    private RecyclerViewUtils.ShowHideToolbarOnScrollingListener showHideToolbarListener;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        beginDate = "";
        endDate = "";
        field = "";
        sortBy = "";
        setupViews();
        actionBar = getSupportActionBar();

        getSupportActionBar().setTitle("News Search");
        actionBar.setTitle("News Search");
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client2 = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    // NETWORK FUNCTION

    public boolean isNetworkAvailable(){
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }
        return false;
    }
    
    private void setupViews() {

        gvResult = (GridView) findViewById(R.id.gvResult);
        articles = new ArrayList<Article>();
        adapter = new ArticleArrayAdapter(this, articles);
        gvResult.setAdapter(adapter);
        gvResult.setOnScrollListener(new InfiniteScrollListener(5) {
            @Override
            public void loadMore(int page, int totalItemsCount) {
                getNews(page);
            }
        });

        gvResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getApplicationContext(), ArticleActivity.class);
                Article article = articles.get(position);
                i.putExtra("article", article);
                startActivity(i);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                setQuery(query);
                adapter.clear();
                getNews(0);
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }

        });
        MenuItemCompat.expandActionView(searchItem);
        searchView.requestFocus();
        this.menu = menu;
        return super.onCreateOptionsMenu(menu);
    }
    public void setQuery(String query){
        this.query = query;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.sort:
                adapter.clear();
                showEditDialog();
                return true;
            case R.id.setting:
                MenuItem itemSort= menu.findItem(R.id.sort);
                MenuItem itemSearch = menu.findItem(R.id.search);
                MenuItem itemAdvance = menu.findItem(R.id.advance);
                if(itemSort.isVisible())      {
                    actionBar.setTitle("News Search");
                    itemSort.setVisible(false);
                    itemAdvance.setVisible(false);
                    itemSearch.setVisible(true);
                }else{
                    actionBar.setTitle("Settings");
                    itemAdvance.setVisible(true);
                    itemSort.setVisible(true);
                    itemSearch.setVisible(false);
                }
                return true;
            case R.id.advance:
                Intent intent = new Intent(this,AdvanceSetting.class);
                startActivityForResult(intent,0);
            default:
                return super.onOptionsItemSelected(item);
        }


    }

    private void showEditDialog() {
        FragmentManager manager = getFragmentManager();
        SingleChoiceDialogFrqgment dialog = new SingleChoiceDialogFrqgment();
        Bundle bundle = new Bundle();
        bundle.putStringArrayList(SingleChoiceDialogFrqgment.DATA,getItems());
        bundle.putInt(SingleChoiceDialogFrqgment.SELECTED, -1);
        dialog.setArguments(bundle);
        dialog.show(manager, "Dialog");
    }

    private ArrayList<String> getItems(){
        ArrayList<String> ret_val = new ArrayList<String>();
        ret_val.add("Sort by oldest");
        ret_val.add("Sort by newest");
        return ret_val;
    }
public RequestParams getParams(int page){
    RequestParams params = new RequestParams();
    params.put("api-key", KEY);
    params.put("page", String.valueOf(page));
    params.put("q", query);
    if(!beginDate.isEmpty()&& beginDate!=null){
        params.put("begin_date",beginDate);
    }
    if(!endDate.isEmpty()&& endDate!=null){
        params.put("end_date",endDate);
    }
    if(!field.isEmpty()&& field!=null){
        params.put("fq",field);
    }
    if(!TextUtils.isEmpty(sortBy)){
        params.put("sort",sortBy);
    }
    return params;

}
    public void getNews(int page){
        if(isNetworkAvailable()&&isOnline()){
            AsyncHttpClient client = new AsyncHttpClient();
            String url = "http://api.nytimes.com/svc/search/v2/articlesearch.json";
            client.get(url,getParams(page), new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    JSONArray articleJSONresult = null;
                    try {
                        articleJSONresult = response.getJSONObject("response").getJSONArray("docs");
                       adapter.addAll(Article.fromJSONArray(articleJSONresult));
                        Log.d("DEBUG", articles.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
        }
        else {

            new AlertDialog.Builder(this)
                    .setTitle("No Internet")
                    .setMessage("Would you like to retry")
                    .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            getNews(0);
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .show();
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if(resultCode == RESULT_OK){
                Bundle bundle = data.getBundleExtra("DATA");
                beginDate="";
                endDate="";
                field = "";
                if(bundle.containsKey("Begin Date")) beginDate = String.valueOf(bundle.get("Begin Date"));
                if(bundle.containsKey("End Date"))endDate = String.valueOf(bundle.get("End Date"));
                if(bundle.containsKey("Field"))field = String.valueOf(bundle.get("Field"));
                adapter.clear();
                getNews(0);
            }
        }
    }

    @Override
    public void selectItem(int position) {
        if(position==0)  sortBy = "oldest";

        else if( position ==1) sortBy = "newest";

        else sortBy ="";



    }
}