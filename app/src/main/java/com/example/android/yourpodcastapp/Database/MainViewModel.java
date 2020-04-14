package com.example.android.yourpodcastapp.Database;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.graphics.Movie;
import android.support.annotation.NonNull;

import java.util.List;

/**
 * Created by Szymon on 09.02.2019.
 */

public class MainViewModel extends AndroidViewModel{
//View model used with the main activity

    private LiveData<List<PodcastEpisodeDetails>> podcastEpisodeDetailsList;

    private LiveData<List<PodcastFeedDetails>> podcastFeedDetailsList;

    private YourPodcastAppMainRepository podcastRepository;

    private PodcastEpisodeDetails episodeDetailsItem;

    private PodcastFeedDetails podcastFeedDetailsItem;


    public MainViewModel(@NonNull Application application) {
        super(application);
        podcastRepository = new YourPodcastAppMainRepository(application);
        podcastEpisodeDetailsList = podcastRepository.getAllPodcastEpisodeDetails();
        podcastFeedDetailsList = podcastRepository.getAllPodcastFeedDetails();

    }

//Methods to be used with the DAO operations
    public void insertPodcastEpisodeDetails(PodcastEpisodeDetails podcastEpisodeDetails){
        podcastRepository.insertPodcastEpisodeDetails(podcastEpisodeDetails);
    }

    public void updatePodcastEpisodeDetails(PodcastEpisodeDetails podcastEpisodeDetails) {

        podcastRepository.updatePodcastEpisodeDetails(podcastEpisodeDetails);
    }

    public void deletePodcastEpisodeDetails(PodcastEpisodeDetails podcastEpisodeDetails) {

        podcastRepository.deletePodcastEpisodeDetails(podcastEpisodeDetails);
    }

    public void deletePodcastEpisodeDetailsItem(String daudioLink) {
         podcastRepository.deletePodcastEpisodeDetailsItem(daudioLink);
    }

    public PodcastEpisodeDetails GetPodcastEpisodeDetailsItem(String daudioLink) {

        episodeDetailsItem = podcastRepository.GetPodcastEpisodeDetailsItem(daudioLink);
        return episodeDetailsItem;
    }

    public void insertPodcastFeed(PodcastFeedDetails podcastFeedDetails) {
         podcastRepository.insertPodcastFeed(podcastFeedDetails);
    }

    public void updatePodcastFeed(PodcastFeedDetails podcastFeedDetails) {

        podcastRepository.updatePodcastFeed(podcastFeedDetails);
    }

    public void deletePodcastFeed(PodcastFeedDetails podcastFeedDetails) {

        podcastRepository.deletePodcastFeed(podcastFeedDetails);
    }

    public void deletePodcast(String did) {
         podcastRepository.deletePodcast(did);
    }

    public PodcastFeedDetails GetPodcastFeed(String did) {

        podcastFeedDetailsItem = podcastRepository.GetPodcastFeed(did);
        return podcastFeedDetailsItem;
    }

    //   PodcastFeed Part


    public LiveData<List<PodcastEpisodeDetails>> getAllEpisodeDetails() {
        return podcastEpisodeDetailsList;
    }
    public LiveData<List<PodcastFeedDetails>> getAllFeedDetails() {
        return podcastFeedDetailsList;
    }

    }

