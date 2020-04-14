package com.example.android.yourpodcastapp.Database;

import android.arch.persistence.room.TypeConverter;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Szymon on 09.02.2019.
 */
//Type converter had to be set up since Room has issued with saving Lists of objects
public class PodcastEpisodeDetailsTypeConverter {

//    inspired by https://stackoverflow.com/questions/18320562/deserialise-a-generic-list-in-gson
    static Gson gson = new Gson();

    @TypeConverter
    public static List<PodcastEpisodeDetails> stringToSomeObjectList(String data) {
        if (data == null) {
            return Collections.emptyList();
        }

        Type listType = new TypeToken<List<PodcastEpisodeDetails>>() {}.getType();

        return gson.fromJson(data, listType);
    }

    @TypeConverter
    public static String someObjectListToString(List<PodcastEpisodeDetails> podcastEpisodeDetails) {
        return gson.toJson(podcastEpisodeDetails);
    }
}