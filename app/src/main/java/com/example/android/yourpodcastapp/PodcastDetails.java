package com.example.android.yourpodcastapp;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.yourpodcastapp.Adapters.PodcastDetailsAdapter;
import com.example.android.yourpodcastapp.Database.PodcastEpisodeDetails;
import com.example.android.yourpodcastapp.Database.PodcastFeedDetails;
import com.example.android.yourpodcastapp.Database.YourPodcastAppDAO;
import com.example.android.yourpodcastapp.Database.YourPodcastAppDatabase;
import com.example.android.yourpodcastapp.Database.YourPodcastAppMainRepository;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.squareup.picasso.Picasso;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Szymon on 07.02.2019.
 */

public class PodcastDetails extends AppCompatActivity implements PodcastDetailsAdapter.GridViewClickListener {

    private PodcastSearchItem passedPodcastSearchItemWithUrl;
    private PodcastFeedDetails podcastFeedDetails;
    private PodcastFeedDetails podcastFeedDetailsMock;
    private Boolean isFavorited = false;
    private List<PodcastEpisodeDetails> podcastEpisodeDetails;
    private String id;
    private Button subscribeButton;
    private YourPodcastAppMainRepository podcastRepository;
    private YourPodcastAppDAO podcastDao;
    private FirebaseAnalytics mFirebaseAnalytics;


    public static void updateWidget(Context context, PodcastFeedDetails podcastFavorited) {
        Intent intent = new Intent(context, NewAppWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.putExtra("Last favorited podcast", podcastFavorited.getTitle());
        int[] ids = AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, NewAppWidget.class));
        if (ids != null && ids.length > 0) {
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
            context.sendBroadcast(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Prepare the necessary instances


        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        YourPodcastAppDatabase mDB = YourPodcastAppDatabase.getInstance(getApplicationContext());
        podcastDao = mDB.yourPodcastAppDAO();
        podcastRepository = new YourPodcastAppMainRepository(getApplication());
//        Check for intent, different workflows dpeending on the information sent. At times a requery is necessary through another search function at the iTunes API

        if (savedInstanceState == null || !savedInstanceState.containsKey("searchItems")){

            Intent passedIntent = getIntent();
    //        Get additional information
            if (passedIntent.hasExtra("searchItemClickedDetails")) {
                PodcastSearchItem passedPodcastSearchItem = passedIntent.getParcelableExtra("searchItemClickedDetails");
                if (passedPodcastSearchItem.getFeedUrl() == "No data" || passedPodcastSearchItem.getFeedUrl().contentEquals("No data")) {
                    Log.v("It's no data", "It's no data");
                    URL url = QueryUtilities.createUrl(Constants.ITUNES_LOOKUP.concat(passedPodcastSearchItem.getId()));
                    new getURLFeedTask(this).execute(url);

                }

    //  Can go straight to the information needed
                else {
                    passedPodcastSearchItemWithUrl = passedPodcastSearchItem;
                    Log.v("Passed podcast ", passedPodcastSearchItemWithUrl.getFeedUrl());
                    id = passedPodcastSearchItemWithUrl.getId();
                    String searchURLFeed = passedPodcastSearchItemWithUrl.getFeedUrl();
                    new getPodcastDetailsTask(this).execute(searchURLFeed);
                }
            }

            if (passedIntent.hasExtra("FavoriteFeed")) {
                podcastFeedDetails = passedIntent.getParcelableExtra("FavoriteFeed");
                podcastEpisodeDetails = podcastFeedDetails.getEpisodeList();
                isFavorited = true;
                Log.v("Is there a favorite feed", String.valueOf(podcastFeedDetails));
                loadUI();
            }
        }
        else {
            podcastFeedDetails = savedInstanceState.getParcelable("podcastFeedDetails");
            isFavorited = savedInstanceState.getBoolean("isFavorited");
            loadUI();
        }
    }

//    Launch intent via a RV clicked item
    @Override
    public void onGridItemClick(int clickedItemIndex) {
        PodcastEpisodeDetails podcastDetals = podcastEpisodeDetails.get(clickedItemIndex);
        Intent intent = new Intent(getApplicationContext(), PlayerActivity.class);
        intent.putExtra("episodeDetails", podcastDetals);
        intent.putExtra("podcastName", podcastFeedDetails.getTitle());
        startActivity(intent);

    }
//    Rebind with the details used for the fuller constructor
    private PodcastFeedDetails rebindFeedDetails(PodcastFeedDetails feedDetails, String id) {
        String podcastId = id;
        String title = feedDetails.getTitle();
        String artWork = feedDetails.getArtWork();
        String description = feedDetails.getDescription();
        List<PodcastEpisodeDetails> episodesToRebind = feedDetails.getEpisodeList();
        return new PodcastFeedDetails(podcastId, title, description, episodesToRebind, artWork);

    }

    private void loadUI() {

//        Set the favorited sign depending on the isFavorited value
        setContentView(R.layout.podcast_details);
        subscribeButton = findViewById(R.id.podcast_details_subscribe_button);
        if (isFavorited == true) {
            subscribeButton.setText("Unsubscribe");
        } else {
            subscribeButton.setText("Subscribe");
        }
//Change the subbscribed button and related is favorited boolean on client action
        subscribeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFavorited == true) {
                    podcastRepository.deletePodcast(podcastFeedDetails.getId());
                    subscribeButton.setText("Subscribe");
                    isFavorited = false;

                } else {
                    updateWidget(getBaseContext(), podcastFeedDetails);
                    podcastRepository.insertPodcastFeed(podcastFeedDetails);
                    isFavorited = true;
                    subscribeButton.setText("Unsubscribe");
                    Bundle bundle = new Bundle();
                    bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "User decided to subscribed to a podcast");
                    bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Subscription ID");
                    bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "text");
                    mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);


                }
            }
        });

//Continue setting up the UI
        ImageView podcastDetailsImage = findViewById(R.id.podcast_details_image);
        TextView podcastNameTv = findViewById(R.id.podcast_name_tv);
        TextView podcast_details_brief_descriptionTV = findViewById(R.id.podcast_details_brief_description);
        ImageButton mUpButton = findViewById(R.id.action_up);
        mUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSupportNavigateUp();
            }
        });
        RecyclerView rv = findViewById(R.id.podcast_details_episodes_rv);
        Picasso.with(this).load(podcastFeedDetails.getArtWork()).fit().into(podcastDetailsImage);
        podcastNameTv.setText(podcastFeedDetails.getTitle());
        podcast_details_brief_descriptionTV.setText(podcastFeedDetails.getDescription());
        podcast_details_brief_descriptionTV.setMovementMethod(new ScrollingMovementMethod());
        rv.setLayoutManager(new LinearLayoutManager(this));
        PodcastDetailsAdapter adapter = new PodcastDetailsAdapter(podcastEpisodeDetails, this, this);
        rv.setAdapter(adapter);

    }
//Get the missing URL from the top 25 search / not the lcient search
    private static class getURLFeedTask extends AsyncTask<URL, Void, List<PodcastSearchItem>> {
        private WeakReference<PodcastDetails> activityReference;

        getURLFeedTask(PodcastDetails context) {
            activityReference = new WeakReference<PodcastDetails>(context);
        }

        @Override
        protected List<PodcastSearchItem> doInBackground(URL... urls) {
            URL searchURL = urls[0];
            PodcastDetails activity = activityReference.get();
            return UserSearchPodcastUtilities.fetchSearchItems(searchURL);

        }


        @Override
        protected void onPostExecute(List<PodcastSearchItem> searchItemList) {
            PodcastDetails activity = activityReference.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }
            Log.v("SearchItemList", String.valueOf(searchItemList));
            activity.passedPodcastSearchItemWithUrl = searchItemList.get(0);
            activity.id = activity.passedPodcastSearchItemWithUrl.getId();
            String searchURLFeed = activity.passedPodcastSearchItemWithUrl.getFeedUrl();
            new getPodcastDetailsTask(activity).execute(searchURLFeed);
            Log.v("Podcast item feed", activity.passedPodcastSearchItemWithUrl.getFeedUrl());

        }

    }

//    Get full podcast detalis - url needed so queried before
    private static class getPodcastDetailsTask extends AsyncTask<String, Void, PodcastFeedDetails> {
        private WeakReference<PodcastDetails> activityReference;

        getPodcastDetailsTask(PodcastDetails context) {
            activityReference = new WeakReference<PodcastDetails>(context);
        }

        @Override
        protected PodcastFeedDetails doInBackground(String... strings) {
            String searchURL = strings[0];
            Log.v("searchUrl", searchURL);
            PodcastDetails activity = activityReference.get();
            try {
                return RSSQueryUtilitiesXMLParser.loadXmlFromNetwork(searchURL);
            } catch (IOException e) {
                Log.e("Exception RSS", "Rss exception");
            } catch (XmlPullParserException e) {
                Log.e("XMLPull", "XML Pull exception");
            }
            return activity.podcastFeedDetailsMock;
        }

        @Override
        protected void onPostExecute(PodcastFeedDetails podcastFeedDetails) {
            PodcastDetails activity = activityReference.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }
            Log.v("PodcastDetails", String.valueOf(podcastFeedDetails));
            Log.v("PodcastDetails", String.valueOf(podcastFeedDetails.getEpisodeList().size()));

            activity.podcastFeedDetails = activity.rebindFeedDetails(podcastFeedDetails, activity.id);
            activity.podcastEpisodeDetails = new ArrayList<PodcastEpisodeDetails>();
            activity.podcastEpisodeDetails = podcastFeedDetails.getEpisodeList();

            new getifFavoritedTask(activity, activity.podcastDao).execute(activity.passedPodcastSearchItemWithUrl.getId());
        }

    }
//Check if item is in the favorited database
    private static class getifFavoritedTask extends AsyncTask<String, Void, PodcastFeedDetails> {
        private WeakReference<PodcastDetails> activityReference;
        private YourPodcastAppDAO podcastDao;

        getifFavoritedTask(PodcastDetails context, YourPodcastAppDAO yourPodcastAppDAO) {
            activityReference = new WeakReference<PodcastDetails>(context);
            podcastDao = yourPodcastAppDAO;
        }

        @Override
        protected PodcastFeedDetails doInBackground(String... strings) {
            String id = strings[0];
            Log.v("searchUrl", id);
            PodcastDetails activity = activityReference.get();
            return podcastDao.getPodcast(id);
        }

        @Override
        protected void onPostExecute(PodcastFeedDetails podcastFeedDetails) {
            PodcastDetails activity = activityReference.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }
            if (podcastFeedDetails != null) {
                activity.isFavorited = true;
            }

            activity.loadUI();
        }
    }
    //Save state
    @Override
    public void onSaveInstanceState(Bundle OutState) {
        OutState.putParcelable("podcastFeedDetails", podcastFeedDetails);
        OutState.putBoolean("isFavorited", isFavorited);
        super.onSaveInstanceState(OutState);
    }
}



