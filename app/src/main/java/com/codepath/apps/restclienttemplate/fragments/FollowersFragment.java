package com.codepath.apps.restclienttemplate.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.apps.restclienttemplate.adapters.TweetsAdapter;
import com.codepath.apps.restclienttemplate.adapters.UserAdapter;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class FollowersFragment extends Fragment {
    public static final String TAG = "FollowersFragment";

    // declare the fragment's properties
    Context context;
    User user;
    RecyclerView rvFollowers;
    List<User> followers;
    UserAdapter adapter;
    Integer mode;

    // constructor for the fragment
    public FollowersFragment(Context context, final User user, int mode) {
        this.context = context;
        this.user = user;
        this.mode = mode;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // define Recycler view and movies list array
        rvFollowers = view.findViewById(R.id.rvFollowers);
        followers = new ArrayList<>();

        // create a layout manager and assign it to the recycler view
        LinearLayoutManager llm = new LinearLayoutManager(context);
        rvFollowers.setLayoutManager(llm);

        // create a new movie adapter and assign it to the recycler view
        adapter = new UserAdapter(context, followers);
        rvFollowers.setAdapter(adapter);

        // set a divider item decoration for each view holder in the recycler view
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvFollowers.getContext(), DividerItemDecoration.VERTICAL);
        rvFollowers.addItemDecoration(dividerItemDecoration);

        // clear the adapter
        adapter.clear();

        // if mode == 1 get followers
        if (mode == 1) {
            // get user's followers
            getUsersFollowers(user);
        } else {
            // else get following (aka the user's friends)
            getUsersFriends(user);
        }

        // set the data
        adapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_followers, container, false);
    }

    // get list of followers as list of user objects through API call
    public void getUsersFollowers(User user) {
        TwitterClient client = TwitterApp.getRestClient(context);
        client.getFollowers(user.screenName, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                try {
                    // append the followers to the followers list
                    followers.addAll(User.fromJsonArray(json.jsonObject.getJSONArray("users")));
                    // notify adapter that the source of data changed
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d(TAG, "failed to get followers", throwable);
            }
        });
    }

    // get list of friends (aka following) as list of user objects through API call
    public void getUsersFriends(User user) {
        TwitterClient client = TwitterApp.getRestClient(context);
        client.getFriends(user.screenName, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                try {
                    // append the friends to the followers list (we can reuse the followers list/adapter/layoutm manager because we cleared it when we switched tabs)
                    followers.addAll(User.fromJsonArray(json.jsonObject.getJSONArray("users")));
                    // notify adapter that source of data changed
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d(TAG, "failed to get following", throwable);
            }
        });
    }
}
