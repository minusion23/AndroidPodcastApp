package com.example.android.yourpodcastapp.Database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Szymon on 07.02.2019.
 */
//Pojo for the podcast feed details list
@Entity(tableName = "podcast_details_table")
public class PodcastFeedDetails implements Parcelable {
    public static final Parcelable.Creator<PodcastFeedDetails> CREATOR = new Parcelable.Creator<PodcastFeedDetails>() {
        @Override
        public PodcastFeedDetails createFromParcel(Parcel in) {
            return new PodcastFeedDetails(in);
        }

        @Override
        public PodcastFeedDetails[] newArray(int size) {
            return new PodcastFeedDetails[size];
        }

    };
    @PrimaryKey(autoGenerate = true)
    private int tabId;
    private String id; //id of the pocast to be used for the feed
    private String title;
    private String artWork;
    private String description;
    @TypeConverters(PodcastEpisodeDetailsTypeConverter.class)
    private List<PodcastEpisodeDetails> episodeList;

    public PodcastFeedDetails(int tabId, String id, String title, String description, List<PodcastEpisodeDetails> episodeList, String artWork) {
        this.tabId = tabId;
        this.id = id;
        this.title = title;
        this.description = description;
        this.episodeList = episodeList;
        this.artWork = artWork;

    }

    @Ignore
    public PodcastFeedDetails(String id, String title, String description, List<PodcastEpisodeDetails> episodeDetails, String artWork) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.episodeList = episodeDetails;
        this.artWork = artWork;

    }

    @Ignore
    public PodcastFeedDetails(String title, String description, List<PodcastEpisodeDetails> episodeDetails, String artWork) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.episodeList = episodeDetails;
        this.artWork = artWork;

    }

    @Ignore
    private PodcastFeedDetails(Parcel in) {
        id = in.readString();
        title = in.readString();
        description = in.readString();
        episodeList = new ArrayList<PodcastEpisodeDetails>();
        in.readTypedList(episodeList, PodcastEpisodeDetails.CREATOR);
        artWork = in.readString();


    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


//    Set constructor for the custom object

    public List<PodcastEpisodeDetails> getEpisodeList() {
        return episodeList;
    }

    public void setEspisodeList(List<PodcastEpisodeDetails> espisodeList) {
        this.episodeList = espisodeList;
    }

    public String getArtWork() {
        return artWork;
    }

    public void setArtWork(String artWork) {
        this.artWork = artWork;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(title);
        parcel.writeString(description);
        parcel.writeTypedList(episodeList);
        parcel.writeString(artWork);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public int getTabId() {
        return tabId;
    }

    public void setTabId(int tabId) {
        this.tabId = tabId;
    }
}

