package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.databinding.ActivityComposeBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

public class ComposeActivity extends AppCompatActivity {
    public static final int MAX_TWEET_LENGTH = 140;
    public static final String TAG = "ComposeActivity";

    // declare views
    EditText etCompose;
    Button btnTweet;
    TwitterClient client;
    Tweet tweet;
    Boolean replyToTweet;
    ImageButton ibClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityComposeBinding binding = ActivityComposeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // define vars and find views
        client = TwitterApp.getRestClient(this);
        replyToTweet = false;
        etCompose = binding.etCompose;
        btnTweet = binding.btnTweet;
        ibClose = binding.ibClose;

        // unwrap parcel from Details Activity/Timeline Activity
        tweet = Parcels.unwrap(getIntent().getParcelableExtra("tweet"));
        // if the tweet in the parcel is not null, this means that we are replying to a tweet
        if (tweet != null) {
            // set text to show that the user is replying to another Twitter user's tweet
            etCompose.setText("Replying to @"+tweet.user.screenName + " ");
            replyToTweet = true;
        }

        // set click listener on close out button
        ibClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Set click listener on tweet button
        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // capture tweet contents
                String tweetContent = etCompose.getText().toString();
                // if the tweet is empty, the user isn't allowed to tweet an empty tweet, returned to compose activity
                if (tweetContent.isEmpty()) {
                    Toast.makeText(ComposeActivity.this, "Sorry your tweet cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                // if the tweet is longer than the max tweet length they aren't allowed to tweet it, returned to compose activity
                if (tweetContent.length()>MAX_TWEET_LENGTH) {
                    Toast.makeText(ComposeActivity.this, "Sorry, your tweet is too long", Toast.LENGTH_SHORT).show();
                    return;
                }
                // if the tweet is a reply to another tweet
                if (replyToTweet) {
                    client.replyTweet(tweet.id, tweetContent, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            Log.i(TAG, "Successfully replied to tweet");
                            // let user know that they successfully replied to the tweet
                            Toast.makeText(ComposeActivity.this, "Replied to tweet", Toast.LENGTH_SHORT).show();
                            try {
                                Tweet tweet = Tweet.fromJson(json.jsonObject);
                                Log.i(TAG, "Reply tweet:" + tweet.body);
                                finish();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.e(TAG, "Failed to reply to tweet", throwable);
                        }
                    });
                }
                // if the tweet is not a reply to an existing tweet
                else {
                    client.publishTweet(tweetContent, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            Log.i(TAG, "onSuccess to publish tweet");
                            Toast.makeText(ComposeActivity.this, "Replied to tweet", Toast.LENGTH_SHORT).show();
                            try {
                                Tweet tweet = Tweet.fromJson(json.jsonObject);
                                Log.i(TAG, "Published tweet:" + tweet.body);
                                // create intent to send into the timeline so the adapter can update the first viewholder with new tweet
                                Intent intent = new Intent();
                                intent.putExtra("tweet", Parcels.wrap(tweet));
                                // Set result code and bundle data for response
                                setResult(RESULT_OK, intent);
                                // closes the activity, pass data to parent
                                finish();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.e(TAG, "onFailure to publish tweet", throwable);
                        }
                    });
                }
            }
        });
    }
}