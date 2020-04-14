package com.example.android.yourpodcastapp;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
//import android.support.v4.app.NotificationCompat;

//import android.support.v4.content.ContextCompat;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v7.app.AppCompatActivity;
//import android.support.v7.app.NotificationCompat;

import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.yourpodcastapp.Database.PodcastEpisodeDetails;
import com.example.android.yourpodcastapp.Database.YourPodcastAppDAO;
import com.example.android.yourpodcastapp.Database.YourPodcastAppDatabase;
import com.example.android.yourpodcastapp.Database.YourPodcastAppMainRepository;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.util.Util;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;

/**
 * Created by Szymon on 09.02.2019.
 */
//Activity used for the player
public class PlayerActivity extends AppCompatActivity implements ExoPlayer.EventListener {

    private FirebaseAnalytics mFirebaseAnalytics;
    private static final String TAG = PlayerActivity.class.getSimpleName();
    private String podcastName;
    private boolean isFavorited = false;
    private boolean isDownloaded = false;
    private PodcastEpisodeDetails currentlyPlayedEpisode;
    private ImageView playerFavoriteButton;
    private Uri myUri;
    private YourPodcastAppMainRepository yourPodcastAppMainRepository;
    private YourPodcastAppDAO podcastDao;
    private MediaBrowserCompat mMediaBrowser;
    private ImageView artworkImage;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.podcast_player);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        ImageButton mUpButton = findViewById(R.id.action_up_player);
        mUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSupportNavigateUp();
            }
        });

//        Create the dataase instance
        YourPodcastAppDatabase mDB = YourPodcastAppDatabase.getInstance(getApplicationContext());
        podcastDao = mDB.yourPodcastAppDAO();
        YourPodcastAppMainRepository podcastRepository = new YourPodcastAppMainRepository(getApplication());
        yourPodcastAppMainRepository = new YourPodcastAppMainRepository(getApplication());
//  Check intent for different information dependig from which workflow the user comes and chagne the UI accordingly
        Intent intent = getIntent();

        if (intent.hasExtra("episodeDetails")){
            currentlyPlayedEpisode = intent.getParcelableExtra("episodeDetails");
            new getifFavoritedTask(this, podcastDao).execute(currentlyPlayedEpisode.getAudioLink());

        }

        if(intent.hasExtra("FavoritedEpisode")){
            currentlyPlayedEpisode = intent.getParcelableExtra("FavoritedEpisode");
            isFavorited = true;

        }

        if (intent.hasExtra("CurrentlyPlayedEpisodeInService")){
            podcastName = intent.getStringExtra("Podcast Name");
            currentlyPlayedEpisode = intent.getParcelableExtra("CurrentlyPlayedEpisodeInService");
            new getifFavoritedTask(this, podcastDao).execute(currentlyPlayedEpisode.getAudioLink());
        }

//        Picasso.with(this).load(currentlyPlayedEpisode.getEpisodeArt()).fit().into(this.artworkImage);

//Prepare the UI
        artworkImage = findViewById(R.id.player_artwork);
        Picasso.with(this).load(currentlyPlayedEpisode.getEpisodeArt()).into(artworkImage);
        TextView playerEpisodeTitle = findViewById(R.id.player_episode_title_tv);
        TextView playerPodcastTitle = findViewById(R.id.player_podcast_title_tv);
        playerEpisodeTitle.setText(currentlyPlayedEpisode.getTitle());
        playerPodcastTitle.setText(podcastName);
//        Allow the user to share the podcast info
        ImageView playerShareButton = findViewById(R.id.player_share_button);
        playerShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                share();

                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "Share button");
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME,"user decided to share from the app");
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
            }
        });


//Audio playback is handled by a service, the windows is to launch a new podcast and favorite, share

        Intent audiotIntent = new Intent(this, AudioPlayerService.class);
        audiotIntent.putExtra("currentEpisodeService", currentlyPlayedEpisode);
        Util.startForegroundService(this, audiotIntent);

        mUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSupportNavigateUp();
            }
        });
//Favorite the itme if it has not been previously favorited
        playerFavoriteButton = findViewById(R.id.player_favorite_button);
        if (isFavorited == true){
            playerFavoriteButton.setImageResource(R.drawable.outline_favorite_white_24);
        }
        else{
            playerFavoriteButton.setImageResource(R.drawable.baseline_favorite_border_white_24);
        }
        playerFavoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFavorited){

                    yourPodcastAppMainRepository.deletePodcastEpisodeDetailsItem(currentlyPlayedEpisode.getAudioLink());
                    isFavorited = false;
                    playerFavoriteButton.setImageResource(R.drawable.baseline_favorite_border_white_24);

                }

                else {
                    yourPodcastAppMainRepository.insertPodcastEpisodeDetails(currentlyPlayedEpisode);
                    isFavorited = true;
                    playerFavoriteButton.setImageResource(R.drawable.outline_favorite_white_24);
                }
            }

        });
    }

//The user can share the details
    public void share(){
        Intent sendIntent = new Intent();
        String textToSend = getString(R.string.default_share_string).concat(currentlyPlayedEpisode.getAudioLink());
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, textToSend);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);}

//Check if item has been favorited by the user
    private static class getifFavoritedTask extends AsyncTask<String, Void, PodcastEpisodeDetails> {
        private WeakReference<PlayerActivity> activityReference;
        private YourPodcastAppDAO podcastDao;

        getifFavoritedTask(PlayerActivity context, YourPodcastAppDAO yourPodcastAppDAO) {
            activityReference = new WeakReference<PlayerActivity>(context);
            podcastDao = yourPodcastAppDAO;
        }

        @Override
        protected PodcastEpisodeDetails doInBackground(String... strings) {
            String id = strings[0];
            Log.v("searchUrl", id);
            PlayerActivity activity = activityReference.get();
            return podcastDao.getEpisode(id);
        }

        @Override
        protected void onPostExecute(PodcastEpisodeDetails podcastFeedDetails) {
            PlayerActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }
            if (podcastFeedDetails != null) {
                activity.isFavorited = true;

                activity.playerFavoriteButton.setImageResource(R.drawable.outline_favorite_white_24);
            }
        }
    }

}


