package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Movie;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterInside;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.parceler.Parcels;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import okhttp3.Headers;

import static com.codepath.apps.restclienttemplate.TimelineActivity.TWEET_POS;

public class DetailActivity extends AppCompatActivity {
    public static final String TAG = "DetailActivity";

    // for startActivityForResult
    private final int REQUEST_CODE = 10;

    Context context;
    Tweet tweet;
    TwitterClient client;
    Integer tweetPosition;

    // declare the view fields
    ImageView ivProfileImage;
    TextView tvBody;
    TextView tvScreenName;
    TextView tvHandle;
    TextView tvRelativeTime;
    ImageView ivMediaEntity;
    TextView tvRetweets;
    TextView tvLikes;
    TextView tvTimestamp;
    TextView tvDatestamp;
    ImageButton ibReply;
    ImageButton ibRetweet;
    ImageButton ibFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        // Find the toolbar view inside the activity layout
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);

        ivProfileImage = findViewById(R.id.ivProfileImage);
        tvBody = findViewById(R.id.tvBody);
        tvScreenName = findViewById(R.id.tvScreenName);
        tvHandle = findViewById(R.id.tvHandle);
        tvTimestamp = findViewById(R.id.tvTimestamp);
        tvDatestamp = findViewById(R.id.tvDatestamp);
        tvRelativeTime = findViewById(R.id.tvRelativeTime);
        ivMediaEntity = findViewById(R.id.ivMediaEntity);
        tvRetweets = findViewById(R.id.tvRetweets);
        tvLikes = findViewById(R.id.tvLikes);
        ibFavorite = findViewById(R.id.ibFavorite);
        ibRetweet = findViewById(R.id.ibRetweet);
        ibReply = findViewById(R.id.ibReply);

        this.context = this;

        tweet = (Tweet) Parcels.unwrap(getIntent().getParcelableExtra("tweetObj"));
        tweetPosition = getIntent().getExtras().getInt(TWEET_POS);

        // set the tweet details
        tvBody.setText(tweet.body);
        tvScreenName.setText(tweet.user.name);
        tvHandle.setText("@"+tweet.user.screenName);
        tvRelativeTime.setText(tweet.relativeTime);
        tvTimestamp.setText(tweet.timestamp);
        tvDatestamp.setText(tweet.datestamp);
        tvRetweets.setText(String.format("%d Retweets", tweet.retweetCount));
        tvLikes.setText(String.format("%d Likes", tweet.favoriteCount));
        // Use glide to display images
        Glide.with(context).load(tweet.user.profileImageUrl).circleCrop().into(ivProfileImage);
        Glide.with(context).load(tweet.mediaUrl).transform(new CenterInside(), new RoundedCornersTransformation(25, 5)).into(ivMediaEntity);

        // make new client for favoriting and unfavoriting tweets
        client = TwitterApp.getRestClient(this);

        if (tweet.retweeted) {
            ibRetweet.setImageResource(R.drawable.ic_vector_retweet);
        }
        if (tweet.favorited) {
            ibFavorite.setImageResource(R.drawable.ic_vector_heart);
        }

        //------------------------FAVORITE BUTTON work------------------------------------------------//
        // set the on click listener
        ibFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // if the tweet is favorited already
                if (tweet.favorited == true) {
                    // make API call to unfavorite tweet
                    client.unfavoriteTweet(tweet.id, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            // make favorited false
                            tweet.favorited = false;
                            // decrement the number of likes the user sees by 1
                            tvLikes.setText(String.format("%d Likes", tweet.favoriteCount-1));
                            tweet.favoriteCount = tweet.favoriteCount -1;
                            // change the button to be stroke type
                            ibFavorite.setImageResource(R.drawable.ic_vector_heart_stroke);
                        }
                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.d(TAG, "failed to unfavorited tweet", throwable);
                        }
                    });
                } else {
                    client.favoriteTweet(tweet.id, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            // make favorited true
                            tweet.favorited = true;
                            // increment the number of likes the user sees by 1
                            tvLikes.setText(String.format("%d Likes", tweet.favoriteCount + 1));
                            tweet.favoriteCount = tweet.favoriteCount +1;
                            // change the button to be full
                            ibFavorite.setImageResource(R.drawable.ic_vector_heart);
                        }
                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.d(TAG, "failed to favorite tweet", throwable);
                        }
                    });
                }
            }
        });

        //------------------------RETWEET BUTTON work------------------------------------------------------------------------//

        ibRetweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int oldRetweetCount = tweet.retweetCount;
                // if I retweeted the tweet already
                if (tweet.retweeted) {
                    // make API call to unretweet tweet
                    client.unretweet(tweet.id, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            tweet.retweeted = false;
                            // decrement the number of retweets by 1
                            tvRetweets.setText(String.format("%d Retweets", oldRetweetCount - 1));
                            //tweet.retweetCount = tweet.retweetCount -1;
                            // change the button to be stroke type
                            ibRetweet.setImageResource(R.drawable.ic_vector_retweet_stroke);
                        }
                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.d(TAG, "failed to unretweet tweet", throwable);
                        }
                    });
                } else {
                    client.retweet(tweet.id, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            tweet.retweeted = true;
                            // increment the number of likes the user sees by 1
                            tvRetweets.setText(String.format("%d Retweets", oldRetweetCount + 1));
                            // tweet.retweetCount = tweet.retweetCount + 1;
                            // change the button to be full
                            ibRetweet.setImageResource(R.drawable.ic_vector_retweet);
                        }
                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.d(TAG, "failed to retweet tweet", throwable);
                            Log.d(TAG, ""+statusCode);
                        }
                    });
                }
            }
        });

        //------------------------REPLY BUTTON work------------------------------------------------------------------------//

        ibReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailActivity.this, ComposeActivity.class);
                intent.putExtra("tweet", Parcels.wrap(tweet));
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(DetailActivity.this, TimelineActivity.class);
        intent.putExtra("tweet", Parcels.wrap(tweet));
        intent.putExtra(TWEET_POS, tweetPosition);
        setResult(RESULT_OK, intent);
        finish();
    }

}