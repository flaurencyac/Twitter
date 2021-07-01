package com.codepath.apps.restclienttemplate.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterInside;
import com.codepath.apps.restclienttemplate.ComposeActivity;
import com.codepath.apps.restclienttemplate.DetailActivity;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TimelineActivity;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.jetbrains.annotations.NotNull;
import org.parceler.Parcels;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import okhttp3.Headers;

import static com.codepath.apps.restclienttemplate.TimelineActivity.TWEET_POS;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {

    Context context;
    List<Tweet> tweets;

    // Pass in the context and the list of tweets
    public TweetsAdapter(Context context, List<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;
    }

    // For each row, inflate the layout for a tweet
    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tweet, parent, false);
        return new ViewHolder(view);
    }

    // Bind values based on the position of the element
    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        // Get the data at position
        Tweet tweet = tweets.get(position);
        // Bind the tweet with the view holder
        holder.bind(tweet);
    }

    // Clean all elements of the recycler
    public void clear() {
        tweets.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Tweet> list) {
        tweets.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    // Define the view holder
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public static final String TAG = "ViewHolder";

        TwitterClient client;

        // declare for each view
        ImageView ivProfileImage;
        TextView tvBody;
        TextView tvScreenName;
        TextView tvRelativeTime;
        ImageView ivMediaEntity;
        ImageButton ibReply;
        ImageButton ibRetweet;
        ImageButton ibFavorite;
        TextView tvRetweets;
        TextView tvLikes;
        TextView tvReply;

        // this view holder and the itemView passed in reps one row
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvScreenName = itemView.findViewById(R.id.tvScreenName);
            tvRelativeTime = itemView.findViewById(R.id.tvRelativeTime);
            ivMediaEntity = itemView.findViewById(R.id.ivMediaEntity);
            tvRetweets = itemView.findViewById(R.id.tvRetweets);
            tvLikes = itemView.findViewById(R.id.tvLikes);
            tvReply = itemView.findViewById(R.id.tvReply);
            ibFavorite = itemView.findViewById(R.id.ibFavorite);
            ibRetweet = itemView.findViewById(R.id.ibRetweet);
            ibReply = itemView.findViewById(R.id.ibReply);
            itemView.setOnClickListener(this);
        }

        // take out the attributes of the tweet and assign them to the different views
        public void bind(final Tweet tweet) {
            // set the text views
            tvBody.setText(tweet.body);
            tvScreenName.setText(tweet.user.screenName);
            tvRelativeTime.setText(tweet.relativeTime);

            // in case the tweet object doesn't have a retweetCount or favoriteCount set their textviews' text to 0
            try {
                tvRetweets.setText(Integer.toString(tweet.retweetCount));
            } catch (Exception e) {
                Log.d(TAG, "No retweetCount for tweet item: " + tweet.id, e);
                tvRetweets.setText("");
            }
            try {
                tvLikes.setText(Integer.toString(tweet.favoriteCount));
            } catch (Exception e) {
                Log.d(TAG, "No favoriteCount for tweet item: " + tweet.id, e);
                tvLikes.setText("0");
            }

            // display images with Glide
            Glide.with(context).load(tweet.user.profileImageUrl).circleCrop().into(ivProfileImage);
            Glide.with(context).load(tweet.mediaUrl).transform(new CenterInside(), new RoundedCornersTransformation(25, 5)).into(ivMediaEntity);

            // depending on the tweet's state (is it retweeted? is it favorited? true/false), set the proper vector asset
            if (tweet.retweeted) {
                ibRetweet.setImageResource(R.drawable.ic_vector_retweet);
            }
            if (tweet.favorited) {
                ibFavorite.setImageResource(R.drawable.ic_vector_heart);
            }

            // set on click listener to name or tweeter
            tvScreenName.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    onClick(view);
                }
            });

            // set on click listener to reply button
            ibReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ComposeActivity.class);
                    intent.putExtra("tweet", Parcels.wrap(tweet));
                    context.startActivity(intent);
                }
            });

            // create API client for favoriting/unfavoriting tweets
            client = TwitterApp.getRestClient(context);

            // set on click listener to favorite button
            ibFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // if the tweet is favorited already
                    if (tweet.favorited) {
                        // make API call to unfavorite tweet
                        client.unfavoriteTweet(tweet.id, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                // make favorited false
                                tweet.favorited = false;
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
                                tvLikes.setText(String.format("%d", tweet.favoriteCount + 1));
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

            // set on click listener to retweet button
            ibRetweet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // if I retweeted the tweet already
                    if (tweet.retweeted) {
                        // make API call to unretweet tweet
                        client.unretweet(tweet.id, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                tweet.retweeted = false;
                                tvRetweets.setText(String.format("%d", tweet.retweetCount-1));
                                tweet.retweetCount = tweet.retweetCount -1;
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
                                Toast.makeText(((TimelineActivity) context), "Retweeted!", Toast.LENGTH_SHORT).show();
                                tvRetweets.setText(String.format("%d", tweet.retweetCount+1));
                                tweet.retweetCount = tweet.retweetCount + 1;
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

        }

        // when the view is clicked, go to the Details Activity
        @Override
        public void onClick(View v) {
            // Gets item position
            int position = getAdapterPosition();
            // make sure the position is valid, i.e. actually exists in the view
            if (position != RecyclerView.NO_POSITION) {
                // get the tweet at the position, this won't work if the class is static
                Tweet tweet = tweets.get(position);
                // create intent for the new activity
                Intent intent = new Intent(context, DetailActivity.class);
                // serialize the tweet using parceler
                intent.putExtra("tweetObj", Parcels.wrap(tweet));
                intent.putExtra(TWEET_POS, position);
                Pair<View, String> p1 = Pair.create((View)ivProfileImage, "profile");
                Pair<View, String> p2 = Pair.create((View) ivMediaEntity, "mediaEntity");
                Pair<View, String> p3 = Pair.create((View)tvBody, "tweetBody");
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((TimelineActivity) context, p1, p2, p3);
                // show the activity
                ((TimelineActivity) context).startActivityForResult(intent, ((TimelineActivity) context).REQUEST_CODE_FAVORITE, options.toBundle());
            }
        }
    }
}
