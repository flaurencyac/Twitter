package com.codepath.apps.restclienttemplate.adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.codepath.apps.restclienttemplate.fragments.FollowersFragment;
import com.codepath.apps.restclienttemplate.models.User;

import org.jetbrains.annotations.NotNull;

public class FragmentAdapter extends FragmentStateAdapter {
    Context context;
    User user;

    // constructor for fragment adapter
    public FragmentAdapter(@NonNull @NotNull FragmentManager fragmentManager, @NonNull @NotNull Lifecycle lifecycle, Context context, User user){
        super(fragmentManager, lifecycle);
        this.context = context;
        this.user = user;
    }

    // this method creates/returns a fragment given the tab position
    @NonNull
    @NotNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new FollowersFragment(context, user, 0);
            default:
                return new FollowersFragment(context, user, 1);
        }
    }

    // gets num of tabs in tab layout
    @Override
    public int getItemCount() {
        return 2;
    }
}
