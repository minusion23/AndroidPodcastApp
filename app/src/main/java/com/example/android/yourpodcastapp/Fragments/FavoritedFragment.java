package com.example.android.yourpodcastapp.Fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.android.yourpodcastapp.Adapters.FavoritedFragmentAdapter;
import com.example.android.yourpodcastapp.Database.MainViewModel;
import com.example.android.yourpodcastapp.Database.PodcastEpisodeDetails;
import com.example.android.yourpodcastapp.PlayerActivity;
import com.example.android.yourpodcastapp.R;
import com.example.android.yourpodcastapp.SearchActivity;

import java.util.List;
import java.util.Set;

/**
 * Created by Szymon on 07.02.2019.
 */



public class FavoritedFragment extends android.support.v4.app.Fragment implements FavoritedFragmentAdapter.EpisodeItemClickListener {
    FloatingActionButton floatButton;

    private MainViewModel model;
    private FavoritedFragmentAdapter adapter;
    private RecyclerView recView;

    public FavoritedFragment(){
//        Required empty constructor
    }
//Set up fragment to be used in the view pager + use Live data
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main_activity_recycler, container, false);
        recView = rootView.findViewById(R.id.mainRV);
        recView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new FavoritedFragmentAdapter(this, getContext());
        recView.setAdapter(adapter);
//set up the recycler view and LiveData/ViewModel to update hte UI only if necessary
        model = ViewModelProviders.of(getActivity()).get(MainViewModel.class);

        model.getAllEpisodeDetails().observe(getActivity(), new Observer<List<PodcastEpisodeDetails>>(){

            @Override
            public void onChanged(@Nullable List<PodcastEpisodeDetails> podcastEpisodeDetails) {
                adapter.setFavoritedEpisodes(podcastEpisodeDetails);
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
    public void onEpisodeItemClickListener(int clickedItemIndex) {
        PodcastEpisodeDetails podcastEpisodeDetails = adapter.getItem(clickedItemIndex);
        Intent intent = new Intent(getContext(), PlayerActivity.class);
        intent.putExtra("FavoritedEpisode", podcastEpisodeDetails);
        startActivity(intent);
    }
}
