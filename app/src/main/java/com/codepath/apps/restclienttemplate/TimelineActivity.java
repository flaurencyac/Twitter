package com.codepath.apps.restclienttemplate;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.codepath.apps.restclienttemplate.adapters.TweetsAdapter;
import com.codepath.apps.restclienttemplate.databinding.ActivityTimelineBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class TimelineActivity extends AppCompatActivity {
    public static final String TWEET_POS = "TWEET_POS";
    public static final String TAG ="TimelineActivity";

    private SwipeRefreshLayout swipeContainer;
    private EndlessRecyclerViewScrollListener scrollListener;

    // create a request code for calls to startActivityForResult()
    private final int REQUEST_CODE_PUBLISH = 8;
    public final int REQUEST_CODE_FAVORITE = 10;

    // declare views
    TwitterClient client;
    RecyclerView rvTweets;
    List<Tweet> tweets;
    TweetsAdapter adapter;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityTimelineBinding binding = ActivityTimelineBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // Find the toolbar view inside the activity layout
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        // Sets the Toolbar to act as the ActionBar for this Activity window.
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // set up the fab and assign an OnClickListener
        fab = binding.fabCompose;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TimelineActivity.this, ComposeActivity.class);
                startActivityForResult(intent, REQUEST_CODE_PUBLISH);
            }
        });

         // Find the recycler view
        rvTweets = binding.rvTweets;

        // Init the list of tweets and adapter
        tweets = new ArrayList<>();
        adapter = new TweetsAdapter(this, tweets);

        // Recycler view setup: layout manager and the adapter
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        // assign the layout manager to the recycler view
        rvTweets.setLayoutManager(linearLayoutManager);

        // assign the adapter to the recycler view
        rvTweets.setAdapter(adapter);

        // set a divider item decoration for each view holder in the recycler view
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvTweets.getContext(), DividerItemDecoration.VERTICAL);
        rvTweets.addItemDecoration(dividerItemDecoration);

        // create a scroll listener
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // load additional items into the adapter
                // append new tweets to the bottom of the list
                loadNextDataFromApi(page);
            }
        };

        // assign scroll listener to the recycler view
        rvTweets.addOnScrollListener(scrollListener);

        // get the twitter rest client
        client = TwitterApp.getRestClient(this);

        // get the home timeline tweets and notify the adapter
        populateHomeTimeline();

        // Setup refresh listener which triggers new data loading
        swipeContainer = binding.swipeContainer;
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // we clear the adapter, add all the new tweets, & call setRefreshing(false) in fetchTimelineAsync
                fetchTimelineAsync(0);
            }
        });

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    // Upon navigating back from the details activity or the compose activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        // check which activity you just navigated back from and if the activity succeeded
        // if you just finished composing/publishing a tweet
        if (requestCode == REQUEST_CODE_PUBLISH && resultCode == RESULT_OK) {
            // Get data from the intent (tweet)
            Tweet tweet = Parcels.unwrap(data.getParcelableExtra("tweet"));
            // Update the recycler view with the tweet
            // Modify data source of tweets
            tweets.add(0, tweet);
            // Update the adapter
            adapter.notifyItemInserted(0);
            rvTweets.smoothScrollToPosition(0);
        }
        // if you just finished favoriting a tweet in the details activity
        if (requestCode == REQUEST_CODE_FAVORITE && resultCode == RESULT_OK) {
            Parcelable tweetParcel = data.getParcelableExtra("tweet");
            Tweet tweet = Parcels.unwrap(tweetParcel);
            int tweetPos = data.getExtras().getInt(TWEET_POS);
            // Update the recycler view with the tweet
            tweets.set(tweetPos, tweet);
            // Update the adapter
            adapter.notifyItemChanged(tweetPos);
            rvTweets.smoothScrollToPosition(tweetPos);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //------------Toolbar methods--------------------------------------------------------------------//

    // Menu icons are inflated just as they were with actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_timeline, menu);
        return true;
    }

    // Handle action bar item clicks here. The action bar will automatically handle clicks on the Home/Up button,
    // so long as you specify a parent activity in AndroidManifest.xml.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        // if the user taps "logout"
        if (id == R.id.action_logout) {
            Log.i(TAG, "logout");
            userLogout();
            return true;
        }
        return true;
    }

    // called when the logout item in the toolbar is clicked
    public void userLogout() {
        // forget who is logged in
        client.clearAccessToken();
        // navigate backwards to login screen
        finish();
    }

    //----------------API METHODS-----------------------------------------------------------------------//

    // make API call to get home timeline tweets
    private void populateHomeTimeline() {
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "onSuccess!" + json.toString());
                try {
                    // append list of tweet objects to tweets list
                    tweets.addAll(Tweet.fromJsonArray(json.jsonArray));
                    // notify adapter of the change in the source of data
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    Log.e(TAG, "Json exception", e);
                }
            }
            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "onFailure!" + response, throwable);
            }
        });
    }

    // This method allows for infinite pagination by sending out a network request and appends new data items to your adapter.
    public void loadNextDataFromApi(int offset) {
        // Send an API request to retrieve appropriate paginated data
        // Second query param of getOlderTweetsPage is the offset value (the page number) so the API knows from which page you want older tweets
        client.getOlderTweetsPage(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                JSONArray jsonArray = json.jsonArray;
                try {
                    // get number of old tweets in adapter
                    int numOldTweets = adapter.getItemCount();
                    // get new tweets
                    List<Tweet> tweets = Tweet.fromJsonArray(jsonArray);
                    // Deserialize and construct new model objects from the API response
                    // Append the new data objects to the existing set of items inside the array of items
                    adapter.addAll(tweets);
                    //  Notify the adapter of the new items made with `notifyItemRangeInserted()`
                    adapter.notifyItemRangeInserted(numOldTweets-1, tweets.size()-numOldTweets);
                } catch (JSONException e) {
                    Log.e(TAG, "Json exception", e);
                }
            }
            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d("DEBUG", "Fetch timeline error: ", throwable);
            }
        }, tweets.get(tweets.size()-1).id);
    }

    // Send the network request to fetch the updated data (aka newest tweets)
    public void fetchTimelineAsync(int page) {
        // 'client' here is an instance of Android Async HTTP
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                // CLEAR OUT old items before appending the new ones
                adapter.clear();
                populateHomeTimeline();
                // Now we call setRefreshing(false) to signal refresh has finished
                swipeContainer.setRefreshing(false);
            }
            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d("DEBUG", "Fetch timeline error: ", throwable);
            }
        });
    }
}