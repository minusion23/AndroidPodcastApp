package com.example.android.yourpodcastapp.Fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.android.yourpodcastapp.Adapters.SubscribedactivityAdapter;
import com.example.android.yourpodcastapp.Database.MainViewModel;
import com.example.android.yourpodcastapp.Database.PodcastFeedDetails;
import com.example.android.yourpodcastapp.PodcastDetails;
import com.example.android.yourpodcastapp.R;
import com.example.android.yourpodcastapp.SearchActivity;

import java.util.List;

/**
 * Created by Szymon on 07.02.2019.
 */

public class SubscribedFragment extends android.support.v4.app.Fragment implements SubscribedactivityAdapter.GridViewClickListener {
//Set up the adapter for the favorited fragment RV
    FloatingActionButton floatButton;
    private MainViewModel model;
    private int orientation;
    private SubscribedactivityAdapter adapter;
    private RecyclerView recView;

    public SubscribedFragment(){
//        Required empty constructor
    }
    //Set up fragment to be used in the view pager + use Live data
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main_activity_recycler, container, false);
        recView = rootView.findViewById(R.id.mainRV);
        orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            recView.setLayoutManager(new GridLayoutManager(recView.getContext(), 3));
        } else {

            recView.setLayoutManager(new GridLayoutManager(recView.getContext(), 2));
        }
//set up the recycler view and LiveData/ViewModel to update hte UI only if necessary
        adapter = new SubscribedactivityAdapter(this, getContext());
        recView.setAdapter(adapter);
        model = ViewModelProviders.of(getActivity()).get(MainViewModel.class);
        model.getAllFeedDetails().observe(getActivity(), new Observer<List<PodcastFeedDetails>>(){

            @Override
            public void onChanged(@Nullable List<PodcastFeedDetails> podcastFeedDetails) {
                adapter.setPodcasts(podcastFeedDetails);
            }
        });

        floatButton  = rootView.findViewById(R.id.floatButtonSearchStart);
        floatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), SearchActivity.class);
                startActivity(intent);
            }
        });
        return rootView;

    }
    //Implement a click listener from the adapter to start the activity
    @Override
    public void onGridItemClick(int clickedItemIndex) {

        PodcastFeedDetails podcastFeedDetails = adapter.getItem(clickedItemIndex);
        Intent intent = new Intent(getContext(), PodcastDetails.class);
        intent.putExtra("FavoriteFeed", podcastFeedDetails);
        startActivity(intent);
    }

}

