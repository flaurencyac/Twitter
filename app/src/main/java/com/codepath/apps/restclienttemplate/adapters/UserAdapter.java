package com.codepath.apps.restclienttemplate.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.apps.restclienttemplate.models.User;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    // Declare properties
    Context context;
    List<User> users;

    // constructor for User Adapter
    public UserAdapter(Context context, List<User> users) {
        this.context = context;
        this.users = users;
    }

    // FOr each row, inflate the layout for a tweet
    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new ViewHolder(view);
    }

    // for the specific position in the recycler view, bind the user to the viewholder in that position
    @Override
    public void onBindViewHolder(@NonNull @NotNull UserAdapter.ViewHolder holder, int position) {
        User user = users.get(position);
        holder.bind(user);
    }

    // Remove all elements of the recycler
    public void clear() {
        users.clear();
        notifyDataSetChanged();
    }

    // returns num of items in the adapter
    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // declare views
        ImageView ivProfileImage;
        TextView tvScreenName;
        TextView tvName;

        // find the views
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            tvScreenName = itemView.findViewById(R.id.tvScreenName);
            tvName = itemView.findViewById(R.id.tvName);
        }

        // set the data
        public void bind(final User user) {
            tvScreenName.setText(user.screenName);
            tvName.setText(user.name);
            Glide.with(context).load(user.profileImageUrl).circleCrop().into(ivProfileImage);
        }
    }
}
