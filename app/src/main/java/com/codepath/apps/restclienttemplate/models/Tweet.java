package com.codepath.apps.restclienttemplate.models;

import android.text.format.DateUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


// Never forget! To use a parcel:
// 1) @Parcel decorator to the class model
// 2) add the two lines of implementation/annotationProcessor into build.gradle (app)
// 3) make a default constructor for the class model
// NOTICE: B/c Tweet contains User model, repeat steps 1 & 3 for User class
@Parcel
public class Tweet {
    public String body;
    public String createdAt;
    public User user;
    public String relativeTime;
    public String timestamp;
    public String datestamp;
    public String mediaUrl;
    public long id;
    public int retweetCount;
    public int favoriteCount;
    public boolean favorited;
    public boolean retweeted;


    public Tweet() {}

    // obtain data from json and define the variables
    public static Tweet fromJson(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();
        tweet.body = jsonObject.getString("text");
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.user = User.fromJson(jsonObject.getJSONObject("user"));
        tweet.relativeTime = getRelativeTimeAgo(tweet.createdAt);
        tweet.timestamp = getTimestamp(tweet.createdAt);
        tweet.datestamp = getDatestamp(tweet.createdAt);
        tweet.id = jsonObject.getLong("id");
        tweet.retweetCount = jsonObject.getInt("retweet_count");
        tweet.favoriteCount = jsonObject.getInt("favorite_count");
        tweet.favorited = jsonObject.getBoolean("favorited");
        tweet.retweeted = jsonObject.getBoolean("retweeted");
        JSONObject entities = jsonObject.getJSONObject("entities");
        if (entities.has("media")) {
            tweet.mediaUrl = entities.getJSONArray("media").getJSONObject(0).getString("media_url_https");
        } else {
            tweet.mediaUrl = "";
        }
        // display_url --> URL of the media to display to clients
        // expanded_url --> links to media display page
        return tweet;
    }

    // this method loops through all the objects in the json array creating a tweet object from each obj
    public static List<Tweet> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Tweet> tweets = new ArrayList<>();
         for (int i = 0; i < jsonArray.length(); i++) {
             tweets.add(fromJson(jsonArray.getJSONObject(i)));
         }
         return tweets;
    }

    // you can use static methods even if you don't have an instance of the class that the method belongs to
    // method that gets the raw JSON string data and obtains the relative time form a user's current time
    public static String getRelativeTimeAgo(String rawString) {
        String tweetFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyyy";
        SimpleDateFormat sf = new SimpleDateFormat(tweetFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawString).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis, System.currentTimeMillis(),
                    DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return relativeDate;
    }

    // gets raw JSON string and uses substring to extract the time in HH:MM AM or PM
    public static String getTimestamp(String rawString) {
        Integer hour = Integer.parseInt(rawString.substring(11,13));
        Integer minutes = Integer.parseInt(rawString.substring(14,16));
        if (hour > 12){
            hour = hour % 12;
            return String.format("%d:%d PM",hour, minutes);
        } else {
            return String.format("%d:%d AM",hour, minutes);
        }
    }

    // gets raw JSON string and uses substring to extract the date in MMM DD YYYY
    public static String getDatestamp(String rawString) {
        String month = rawString.substring(4,7);
        String day = rawString.substring(8,10);
        String year = rawString.substring(rawString.length()-4);
        return String.format("%s %s, %s", month, day, year);
    }
}
