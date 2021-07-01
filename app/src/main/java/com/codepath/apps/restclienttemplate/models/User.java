package com.codepath.apps.restclienttemplate.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel
public class User {
    public String name;
    public String screenName;
    public String profileImageUrl;

    public User() {}

    public static User fromJson(JSONObject jsonObject) throws JSONException {
        User user = new User();
        user.name = jsonObject.getString("name");
        user.screenName = jsonObject.getString("screen_name");
        user.profileImageUrl = jsonObject.getString("profile_image_url_https");
        return user;
    }

    public static List<User> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<User> followers = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            followers.add(fromJson(jsonArray.getJSONObject(i)));
        }
        return followers;
    }
}
