package com.example.android.yourpodcastapp.Database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Szymon on 07.02.2019.
 */
//Pojo fpor podcastEpisode details

@Entity(tableName = "episode_details_table")
public class PodcastEpisodeDetails implements Parcelable {

    public static final Parcelable.Creator<PodcastEpisodeDetails> CREATOR = new Parcelable.Creator<PodcastEpisodeDetails>() {
        @Override
        public PodcastEpisodeDetails createFromParcel(Parcel in) {
            return new PodcastEpisodeDetails(in);
        }

        @Override
        public PodcastEpisodeDetails[] newArray(int size) {
            return new PodcastEpisodeDetails[size];
        }

    };
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String date;
    private String description;
    private String duration;
    private String audioLink;
    private String episodeArt;
    private String seasonNumber;
    private String episodeNumber;

    public PodcastEpisodeDetails(int id, String title, String date, String description, String duration, String audioLink, String seasonNumber, String episodeNumber, String episodeArt) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.description = description;
        this.duration = duration;
        this.audioLink = audioLink;
        this.seasonNumber = seasonNumber;
        this.episodeNumber = episodeNumber;
        this.episodeArt = episodeArt;

    }

    @Ignore
    public PodcastEpisodeDetails(String title, String date, String description, String duration, String audioLink, String seasonNumber, String episodeNumber, String episodeArt) {
        this.title = title;
        this.date = date;
        this.description = description;
        this.duration = duration;
        this.audioLink = audioLink;
        this.seasonNumber = seasonNumber;
        this.episodeNumber = episodeNumber;
        this.episodeArt = episodeArt;

    }

    private PodcastEpisodeDetails(Parcel in) {
        title = in.readString();
        date = in.readString();
        description = in.readString();
        duration = in.readString();
        audioLink = in.readString();
        seasonNumber = in.readString();
        episodeNumber = in.readString();
        episodeArt = in.readString();

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getAudioLink() {
        return audioLink;
    }

    public void setAudioLink(String audioLink) {
        this.audioLink = audioLink;
    }

    public String getEpisodeArt() {
        return episodeArt;
    }

    public void setEpisodeArt(String episodeArt) {
        this.episodeArt = episodeArt;
    }

    public String getSeasonNumber() {
        return seasonNumber;
    }


//    Set constructor for the custom object

    public void setSeasonNumber(String seasonNumber) {
        this.seasonNumber = seasonNumber;
    }

    public String getEpisodeNumber() {
        return episodeNumber;
    }

    public void setEpisodeNumber(String episodeNumber) {
        this.episodeNumber = episodeNumber;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(date);
        parcel.writeString(description);
        parcel.writeString(duration);
        parcel.writeString(audioLink);
        parcel.writeString(seasonNumber);
        parcel.writeString(episodeNumber);
        parcel.writeString(episodeArt);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}

