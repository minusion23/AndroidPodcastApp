
//Inspired by https://www.youtube.com/watch?v=jAZn-J1I8Eg
// https://www.youtube.com/watch?v=svdq1BWl4r8; https://stackoverflow.com/questions/52957208/exoplayer-play-sound-in-the-background-with-a-video-file;
//https://medium.com/google-exoplayer/playback-notifications-with-exoplayer-a2f1a18cf93b https://github.com/sooshin/android-candy-pod

package com.example.android.yourpodcastapp;

import android.app.Notification;
import android.app.PendingIntent;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaBrowserServiceCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import com.example.android.yourpodcastapp.Database.PodcastEpisodeDetails;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector;

import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;
import java.util.List;


//Service to play the audio in the system limiting the risk of resources being withdrawn by Android
public class AudioPlayerService extends MediaBrowserServiceCompat implements Player.EventListener{
    private static final String TAG = AudioPlayerService.class.getSimpleName();
    public static final String PLAYBACK_CHANNEL_ID = "playback_channel";
    public static final int PLAYBACK_NOTIFICATION_ID = 2;
    public String podcastName;
    private Uri uri;
    private SimpleExoPlayer mExoPlayer;
    private PodcastEpisodeDetails currentlyPlayedEpisode;
    private PlayerNotificationManager playerNotificationManager;
    private static MediaSessionCompat mediaSession;
    private MediaSessionConnector mediaSessionConnector;
    private final Context context = this;
    private PlaybackStateCompat.Builder mStateBuilder;


    private static final String CANDY_POD_ROOT_ID = "pod_root_id";
    private static final String CANDY_POD_EMPTY_ROOT_ID = "empty_root_id";

    @Override
    public void onCreate() {
        super.onCreate();


        initializeMediaSession();


    }
//    Handle the on destroyh
    @Override
    public void onDestroy() {

        mediaSession.release();
        if (playerNotificationManager != null) {
            playerNotificationManager.setPlayer(null);
        }
        releasePlayer();
        super.onDestroy();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Returns the root node of the content hierarchy. This method controls access to the service.
     */

    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        if(TextUtils.equals(clientPackageName, getPackageName())) {
            return new BrowserRoot(getString(R.string.app_name), null);
        }

        return null;
    }

    /**
     * This method provides the ability for a client to build and display a menu of the
     * MediaBrowserService's content hierarchy.
//     */
    @Override
    public void onLoadChildren(@NonNull String parentMediaId,
                               @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {

        // Browsing not allowed
        if (TextUtils.equals(getString(R.string.app_name), parentMediaId)) {
            result.sendResult(null);
            return;
        }

        List<MediaBrowserCompat.MediaItem> mediaItems = new ArrayList<>();

        // Check if this is the root menu:
        if (getString(R.string.app_name).equals(parentMediaId)) {
            // Build the MediaItem objects for the top level,
            // and put them in the mediaItems list...

        } else {
            // Examine the passed parentMediaId to see which submenu we're at,
            // and put the children of that menu in the mediaItems list...
        }
        result.sendResult(mediaItems);
    }

//    Override to get the URI needed to launch the sound
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // Check if the old player should be released
        if (intent.getAction() != null && intent.getAction().equals("Release Old Player")) {
            if (mExoPlayer != null) {
                mExoPlayer.stop();
                mExoPlayer.release();
            }
        }

//        retrieve the episode information to start playback

        if (intent.hasExtra("currentEpisodeService")){
            currentlyPlayedEpisode = intent.getParcelableExtra("currentEpisodeService");
            podcastName = intent.getStringExtra("Podcast Name");
            uri = Uri.parse(currentlyPlayedEpisode.getAudioLink());}
        initializePlayer(uri);
        initializeNotificationManager(currentlyPlayedEpisode);

        return START_STICKY;
    }


    private void initializePlayer(Uri mediaUri) {
        if (mExoPlayer == null) {
            // Create an instance of the ExoPlayer.
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl);

            mExoPlayer.addListener(this);

            String userAgent = Util.getUserAgent(this, "Your Podcast App");
            MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(
                    this, userAgent), new DefaultExtractorsFactory(), null, null);


            DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(
                    this, Util.getUserAgent(this, getString(R.string.application_name)));

            mExoPlayer.prepare(mediaSource);
            mExoPlayer.setPlayWhenReady(true);
        }
    }
//Start the notification manager to run the audio in the background

    private void initializeNotificationManager(final PodcastEpisodeDetails currentlyPlayedEpisode){
        playerNotificationManager = PlayerNotificationManager.createWithNotificationChannel(
                this,
                PLAYBACK_CHANNEL_ID,
                R.string.download_channel,
                PLAYBACK_NOTIFICATION_ID,
                new PlayerNotificationManager.MediaDescriptionAdapter() {
                    @Override
                    public String getCurrentContentTitle(Player player) {
                        return currentlyPlayedEpisode.getTitle();
                    }

                    @Nullable
                    @Override
                    public PendingIntent createCurrentContentIntent(Player player) {
                        // Create a pending intent that relaunches the NowPlayingActivity
                        return createContentPendingIntent(currentlyPlayedEpisode);
                    }

                    @Nullable
                    @Override
                    public String getCurrentContentText(Player player) {
                        return podcastName;
                    }

                    @Nullable
                    @Override
                    public Bitmap getCurrentLargeIcon(Player player, PlayerNotificationManager.BitmapCallback callback) {
//
                        return null;
                    }
                }
        );

        playerNotificationManager.setNotificationListener(new PlayerNotificationManager.NotificationListener() {
            @Override
            public void onNotificationStarted(int notificationId, Notification notification) {
                startForeground(notificationId, notification);
            }

            @Override
            public void onNotificationCancelled(int notificationId) {
                stopSelf();
            }
        });
        playerNotificationManager.setPlayer(mExoPlayer);
        playerNotificationManager.setMediaSessionToken(mediaSession.getSessionToken());
        playerNotificationManager.setUseNavigationActions(true);
        // Set the fast forward increment by 30 sec
        playerNotificationManager.setFastForwardIncrementMs(30);
        // Set the rewind increment by 10sec
        playerNotificationManager.setRewindIncrementMs(30);
        // Omit the stop action
//        playerNotificationManager.setStopAction(null);
        // Make the notification not ongoing
        playerNotificationManager.setOngoing(false);
    }
//Get media sessions for the given channel
    private void initializeMediaSession() {
        mediaSession = new MediaSessionCompat(AudioPlayerService.this, TAG);

        // Enable callbacks from MediaButtons and TransportControls
        mediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player
        mStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_REWIND |
                                PlaybackStateCompat.ACTION_FAST_FORWARD |
                                PlaybackStateCompat.ACTION_PLAY_PAUSE);
        mediaSession.setPlaybackState(mStateBuilder.build());

        // MySessionCallback() has methods that handle callbacks from a media controller
        mediaSession.setCallback(new MySessionCallback());

        // Set the session's token so that client activities can communicate with it.
        setSessionToken(mediaSession.getSessionToken());

        mediaSession.setSessionActivity(PendingIntent.getActivity(this,
                11,
                new Intent(this, PlayerActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT));
    }

//Create an intent allowing to go back to the screen with the details
    private PendingIntent createContentPendingIntent(PodcastEpisodeDetails currentlyPlayedEpisode) {
        Intent reopenIntent = new Intent(AudioPlayerService.this, PlayerActivity.class);
        // Pass data via intent
        reopenIntent.putExtra("CurrentlyPlayedEpisodeInService", currentlyPlayedEpisode);
        reopenIntent.putExtra("Podcast Name", podcastName);
        return PendingIntent.getActivity(
                AudioPlayerService.this, 0,
                reopenIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

//Set up callback options for the player to react to different states
    private class MySessionCallback extends MediaSessionCompat.Callback {

        @Override
        public void onPlay() {
            // onPlay() callback should include code that calls startService().
            startService(new Intent(getApplicationContext(), AudioPlayerService.class));

            // Set the session active
            mediaSession.setActive(true);

            // Start the player
            if (mExoPlayer != null) {
                mExoPlayer.setPlayWhenReady(true);
            }
        }

        @Override
        public void onPause() {
            mExoPlayer.setPlayWhenReady(false);

            stopForeground(false);
        }

        @Override
        public void onRewind() {
            mExoPlayer.seekTo(Math.max(mExoPlayer.getCurrentPosition() - 30, 0));
        }

        @Override
        public void onFastForward() {
            long duration = mExoPlayer.getDuration();
            mExoPlayer.seekTo(Math.min(mExoPlayer.getCurrentPosition() +
                    30, duration));
        }

        @Override
        public void onStop() {
            // onStop() callback should call stopSelf().
            stopSelf();

            // Set the session inactive
            mediaSession.setActive(false);
            // Sto  p the player
            mExoPlayer.stop();

            // Take the service out of the foreground
            stopForeground(true);
        }
    }

    // Player Event Listeners

    @Override
    public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
           Toast.makeText(this, "Player error", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if (playbackState == Player.STATE_READY && playWhenReady) {
            // When ExoPlayer is playing, update the PlaybackState
            mStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                    mExoPlayer.getCurrentPosition(), 1f);

        } else if (playbackState == Player.STATE_READY) {
            // When ExoPlayer is paused, update the PlaybackState
            mStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                    mExoPlayer.getCurrentPosition(), 1f);

        }
        mediaSession.setPlaybackState(mStateBuilder.build());
    }


    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        if (mExoPlayer != null) {
            mExoPlayer.stop(true);
        }
        stopSelf();
        // Allow the notification to be swipe dismissed when paused
        // Reference: @see "https://stackoverflow.com/questions/26496670/dismissing-mediastyle-notifications"
        playerNotificationManager.setOngoing(false);
    }
    private void releasePlayer() {
        mExoPlayer.release();
        mExoPlayer = null;
    }
}