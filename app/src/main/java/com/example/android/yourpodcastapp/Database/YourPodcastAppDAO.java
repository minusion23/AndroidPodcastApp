package com.example.android.yourpodcastapp.Database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import android.graphics.Movie;

import java.util.List;

/**
 * Created by Szymon on 09.02.2019.
 */
//Dao for opertaions on the set up tables
@Dao
public interface YourPodcastAppDAO {

    @Insert
    void insertPodcastFeed(PodcastFeedDetails podcastFeedDetails);

    @Insert
    void insertPodcastEpisodeDetails(PodcastEpisodeDetails podcastEpisodeDetails);


    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updatePodcastFeed(PodcastFeedDetails podcastFeedDetails);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updatePodcastEpisodeDetails(PodcastEpisodeDetails podcastEpisodeDetails);

    @Delete
    void deletePodcastFeed(PodcastFeedDetails podcastFeedDetails);

    @Delete
    void deletePodcastEpisodeDetails(PodcastEpisodeDetails podcastEpisodeDetails);

    @Query("SELECT * FROM podcast_details_table")
    LiveData<List<PodcastFeedDetails>> getAllPodcasts();

    @Query("DELETE FROM podcast_details_table WHERE id = :did")
    void deletePodcast(String did);

    @Query("SELECT * FROM podcast_details_table WHERE id = :did")
    PodcastFeedDetails getPodcast(String did);

    @Query("SELECT * FROM episode_details_table")
    LiveData<List<PodcastEpisodeDetails>> getAllEpisodes();

    @Query("DELETE FROM episode_details_table WHERE audioLink = :daudioLink")
    void deleteEpisode(String daudioLink);

    @Query("SELECT * FROM episode_details_table WHERE audioLink = :daudioLink")
    PodcastEpisodeDetails getEpisode(String daudioLink);


}

