package com.codepath.apps.restclienttemplate.models;

import android.text.format.DateUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Tweet {
    public String body;
    public String createdAt;
    public User user;
    public String relativeTime;

    public static Tweet fromJson(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();
        tweet.body = jsonObject.getString("text");
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.user = User.fromJson(jsonObject.getJSONObject("user"));
        tweet.relativeTime = getRelativeTimeAgo(tweet.createdAt);
        return tweet;
    }

    public static List<Tweet> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Tweet> tweets = new ArrayList<>();
         for (int i = 0; i < jsonArray.length(); i++) {
             tweets.add(fromJson(jsonArray.getJSONObject(i)));
         }
         return tweets;
    }

    // you can use static methods even if you don't have a instance of the class that the method belongs to
    public static String getRelativeTimeAgo(String jsonTimestamp) {
        String tweetFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyyy";
        SimpleDateFormat sf = new SimpleDateFormat(tweetFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(jsonTimestamp).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis, System.currentTimeMillis(),
                    DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return relativeDate;
    }
}
