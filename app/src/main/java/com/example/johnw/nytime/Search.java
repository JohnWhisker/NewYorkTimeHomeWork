package com.example.johnw.nytime;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class Search extends AppCompatActivity implements SingleChoiceDialogFrqgment.SelectionListener{
    EditText etQuery;
    String KEY = "347a6b323c94cbf625b8de7c59a23a2d:18:74723396";
    GridView gvResult;
    Button btnSearch;
    ArrayList<Article> articles;
    ArticleArrayAdapter adapter;
    ActionBar actionBar;
    String sortBy;


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
        setupViews();
        
        actionBar = getSupportActionBar();
        getSupportActionBar().setTitle("News Search");
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client2 = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void setupViews() {
        etQuery = (EditText) findViewById(R.id.etQuerry);
        btnSearch = (Button) findViewById(R.id.btnSearch);
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
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.sort:
                showEditDialog();

                return true;
            case R.id.setting:
                return true;
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
    String query = etQuery.getText().toString();
    RequestParams params = new RequestParams();
    params.put("api-key", KEY);
    params.put("page", String.valueOf(page));
    params.put("q", query);
    if(!TextUtils.isEmpty(sortBy)){
        params.put("sort",sortBy);
    }
    return params;

}
    public void getNews(int page){
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
    public void onArticleSearch(View view) {
        adapter.clear();
        getNews(0);

    }


    @Override
    public void selectItem(int position) {
        if(position==0)  sortBy = "oldest";

        else if( position ==1) sortBy = "newest";

        else sortBy ="";



    }
}