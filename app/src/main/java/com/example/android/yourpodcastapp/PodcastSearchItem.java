package com.example.android.yourpodcastapp;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Szymon on 07.02.2019.
 */
//Search item retrieved information POJO
public class PodcastSearchItem implements Parcelable {

    public static final Parcelable.Creator<PodcastSearchItem> CREATOR = new Parcelable.Creator<PodcastSearchItem>() {
        @Override
        public PodcastSearchItem createFromParcel(Parcel in) {
            return new PodcastSearchItem(in);
        }

        @Override
        public PodcastSearchItem[] newArray(int size) {
            return new PodcastSearchItem[size];
        }

    };
    private String id; //id of the pocast to be used for the feed
    private String name;
    private String artwork;
    private String feedUrl;

    public PodcastSearchItem(String id, String name, String artwork, String feedUrl) {
        this.id = id;
        this.name = name;
        this.artwork = artwork;
        this.feedUrl = feedUrl;

    }

    private PodcastSearchItem(Parcel in) {
        id = in.readString();
        name = in.readString();
        artwork = in.readString();
        feedUrl = in.readString();

    }

    public String getFeedUrl() {
        return feedUrl;
    }

    public void setFeedUrl(String feedUrl) {
        this.feedUrl = feedUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


//    Set constructor for the custom object

    public String getArtwork() {
        return artwork;
    }

    public void setArtwork(String artwork) {
        this.artwork = artwork;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(name);
        parcel.writeString(artwork);
        parcel.writeString(feedUrl);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}

