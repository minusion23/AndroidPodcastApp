package com.example.android.yourpodcastapp.Database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.graphics.Movie;
import android.util.Log;

import com.example.android.yourpodcastapp.PodcastDetails;

import java.lang.reflect.Array;

/**
 * Created by Szymon on 09.02.2019.
 */

@Database(entities = {PodcastEpisodeDetails.class, PodcastFeedDetails.class}, version = 2, exportSchema = false)
public abstract class YourPodcastAppDatabase extends RoomDatabase {

    private static final String DATBASE_NAME = "podcast_app_database";
    private static YourPodcastAppDatabase instance;

    public static synchronized YourPodcastAppDatabase getInstance(Context context) {

        if (instance == null) {

            instance = Room.databaseBuilder(context.getApplicationContext(),
                    YourPodcastAppDatabase.class, DATBASE_NAME)
                    .fallbackToDestructiveMigration()
                    .build();
        }
        Log.v("Do we have an instance", String.valueOf(instance));
        return instance;
    }

    //Abstract constructor to be used throughout the code
    public abstract YourPodcastAppDAO yourPodcastAppDAO();
}
