package com.example.android.yourpodcastapp.Database;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.graphics.Movie;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;

import com.example.android.yourpodcastapp.MainActivity;


import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.List;

/**
 * Created by Szymon on 09.02.2019.
 */
//Main repository for the app
public class YourPodcastAppMainRepository {

    private PodcastEpisodeDetails episodeDetailsItem;

    private PodcastFeedDetails podcastFeedDetailsItem;

    private YourPodcastAppDAO podcastDAO;

    private YourPodcastAppDatabase database;

    private LiveData<List<PodcastEpisodeDetails>> podcastEpisodeDetailsList;

    private LiveData<List<PodcastFeedDetails>> podcastFeedDetailsList;

    public YourPodcastAppMainRepository(Application application) {
        database = YourPodcastAppDatabase.getInstance(application);
        podcastDAO = database.yourPodcastAppDAO();
        podcastEpisodeDetailsList = podcastDAO.getAllEpisodes();
        podcastFeedDetailsList = podcastDAO.getAllPodcasts();

    }

    public LiveData<List<PodcastEpisodeDetails>> getpodcastEpisodeDetailsList() {
        return podcastEpisodeDetailsList;
    }

    public LiveData<List<PodcastFeedDetails>> getpodcastFeedDetailsList() {
        return podcastFeedDetailsList;
    }


//   PodcastEpisodeDetails Part

    public void insertPodcastEpisodeDetails(PodcastEpisodeDetails podcastEpisodeDetails) {
        new InsertPodcastEpisodeDetailsAsyncTask(podcastDAO).execute(podcastEpisodeDetails);
    }

    public void updatePodcastEpisodeDetails(PodcastEpisodeDetails podcastEpisodeDetails) {

        new UpdatePodcastEpisodeDetailsAsyncTask(podcastDAO).execute(podcastEpisodeDetails);
    }

    public void deletePodcastEpisodeDetails(PodcastEpisodeDetails podcastEpisodeDetails) {

        new DeletePodcastEpisodeDetailsAsyncTask(podcastDAO).execute(podcastEpisodeDetails);
    }

    public void deletePodcastEpisodeDetailsItem(String daudioLink) {
        new DeletePodcastEpisodeDetailsItemAsyncTask(podcastDAO).execute(daudioLink);
    }

    public PodcastEpisodeDetails GetPodcastEpisodeDetailsItem(String daudioLink) {

        GetPodcastEpisodeDetailsItemAsyncTask task = new GetPodcastEpisodeDetailsItemAsyncTask(podcastDAO);
        task.execute(daudioLink);

        return episodeDetailsItem;
    }

    public LiveData<List<PodcastEpisodeDetails>> getAllPodcastEpisodeDetails() {
        Log.v("What is the List", String.valueOf(podcastEpisodeDetailsList));
        return podcastEpisodeDetailsList;
    }

    public void insertPodcastFeed(PodcastFeedDetails podcastFeedDetails) {
        new InsertPodcastFeedDetailsAsyncTask(podcastDAO).execute(podcastFeedDetails);
    }

    public void updatePodcastFeed(PodcastFeedDetails podcastFeedDetails) {

        new UpdatePodcastFeedDetailsAsyncTask(podcastDAO).execute(podcastFeedDetails);
    }

    public void deletePodcastFeed(PodcastFeedDetails podcastFeedDetails) {

        new DeletePodcastFeedDetailsAsyncTask(podcastDAO).execute(podcastFeedDetails);
    }

    public void deletePodcast(String did) {
        new DeletePodcastFeedDetailsItemAsyncTask(podcastDAO).execute(did);
    }

    public PodcastFeedDetails GetPodcastFeed(String did) {


        GetPodcastFeedDetailsItemAsyncTask task = new GetPodcastFeedDetailsItemAsyncTask(podcastDAO, this);
        task.execute(did);
        return podcastFeedDetailsItem;
    }

    //   PodcastFeed Part

    public LiveData<List<PodcastFeedDetails>> getAllPodcastFeedDetails() {
        return podcastFeedDetailsList;
    }


    private static class InsertPodcastEpisodeDetailsAsyncTask extends AsyncTask<PodcastEpisodeDetails, Void, Void> {

        private YourPodcastAppDAO podcastDao;

        private InsertPodcastEpisodeDetailsAsyncTask(YourPodcastAppDAO podcastDao) {
            this.podcastDao = podcastDao;
        }

        @Override
        protected Void doInBackground(PodcastEpisodeDetails... PodcastEpisodeDetails) {
            podcastDao.insertPodcastEpisodeDetails(PodcastEpisodeDetails[0]);
            return null;
        }
    }

    private static class UpdatePodcastEpisodeDetailsAsyncTask extends AsyncTask<PodcastEpisodeDetails, Void, Void> {

        private YourPodcastAppDAO podcastDao;

        private UpdatePodcastEpisodeDetailsAsyncTask(YourPodcastAppDAO podcastDao) {
            this.podcastDao = podcastDao;
        }

        @Override
        protected Void doInBackground(PodcastEpisodeDetails... PodcastEpisodeDetails) {
            podcastDao.updatePodcastEpisodeDetails(PodcastEpisodeDetails[0]);
            return null;
        }
    }

    private static class DeletePodcastEpisodeDetailsAsyncTask extends AsyncTask<PodcastEpisodeDetails, Void, Void> {

        private YourPodcastAppDAO podcastDao;

        private DeletePodcastEpisodeDetailsAsyncTask(YourPodcastAppDAO podcastDao) {
            this.podcastDao = podcastDao;
        }

        @Override
        protected Void doInBackground(PodcastEpisodeDetails... PodcastEpisodeDetails) {
            podcastDao.deletePodcastEpisodeDetails(PodcastEpisodeDetails[0]);
            return null;
        }
    }

    private static class DeletePodcastEpisodeDetailsItemAsyncTask extends AsyncTask<String, Void, Void> {

        private YourPodcastAppDAO podcastDao;

        private DeletePodcastEpisodeDetailsItemAsyncTask(YourPodcastAppDAO podcastDao) {
            this.podcastDao = podcastDao;
        }

        @Override
        protected Void doInBackground(String... strings) {
            podcastDao.deleteEpisode(strings[0]);
            return null;
        }
    }
    //   Downloaded episode Part

    public static class GetPodcastEpisodeDetailsItemAsyncTask extends AsyncTask<String, Void, PodcastEpisodeDetails> {

        private YourPodcastAppDAO podcastDao;


        public GetPodcastEpisodeDetailsItemAsyncTask(YourPodcastAppDAO podcastDao) {
            this.podcastDao = podcastDao;

        }

        @Override
        protected PodcastEpisodeDetails doInBackground(String... strings) {
            Log.v("integers", strings[0]);
            return podcastDao.getEpisode(strings[0]);

        }

    }

    private static class InsertPodcastFeedDetailsAsyncTask extends AsyncTask<PodcastFeedDetails, Void, Void> {

        private YourPodcastAppDAO podcastDao;

        private InsertPodcastFeedDetailsAsyncTask(YourPodcastAppDAO podcastDao) {
            this.podcastDao = podcastDao;
        }

        @Override
        protected Void doInBackground(PodcastFeedDetails... PodcastFeedDetails) {
            podcastDao.insertPodcastFeed(PodcastFeedDetails[0]);
            return null;
        }
    }

    private static class UpdatePodcastFeedDetailsAsyncTask extends AsyncTask<PodcastFeedDetails, Void, Void> {

        private YourPodcastAppDAO podcastDao;

        private UpdatePodcastFeedDetailsAsyncTask(YourPodcastAppDAO podcastDao) {
            this.podcastDao = podcastDao;
        }

        @Override
        protected Void doInBackground(PodcastFeedDetails... PodcastFeedDetails) {
            podcastDao.updatePodcastFeed(PodcastFeedDetails[0]);
            return null;
        }
    }

    private static class DeletePodcastFeedDetailsAsyncTask extends AsyncTask<PodcastFeedDetails, Void, Void> {
        private WeakReference<YourPodcastAppMainRepository> activityReference;

        private YourPodcastAppDAO podcastDao;

        private DeletePodcastFeedDetailsAsyncTask(YourPodcastAppDAO podcastDao) {
            this.podcastDao = podcastDao;
        }

        @Override
        protected Void doInBackground(PodcastFeedDetails... PodcastFeedDetails) {
            podcastDao.deletePodcastFeed(PodcastFeedDetails[0]);
            return null;
        }
    }

    private static class DeletePodcastFeedDetailsItemAsyncTask extends AsyncTask<String, Void, Void> {

        private YourPodcastAppDAO podcastDao;

        private DeletePodcastFeedDetailsItemAsyncTask(YourPodcastAppDAO podcastDao) {
            this.podcastDao = podcastDao;
        }

        @Override
        protected Void doInBackground(String... strings) {
            podcastDao.deletePodcast(strings[0]);
            return null;
        }
    }


    public static class GetPodcastFeedDetailsItemAsyncTask extends AsyncTask<String, Void, PodcastFeedDetails> {
        private WeakReference<YourPodcastAppMainRepository> activityReference;
        private YourPodcastAppDAO podcastDao;


        public GetPodcastFeedDetailsItemAsyncTask(YourPodcastAppDAO podcastDao, YourPodcastAppMainRepository context) {
            this.podcastDao = podcastDao;
            activityReference = new WeakReference<YourPodcastAppMainRepository>(context);
        }

        @Override
        protected PodcastFeedDetails doInBackground(String... strings) {
            Log.v("integers", strings[0]);
            return podcastDao.getPodcast(strings[0]);
        }

        @Override
        protected void onPostExecute(PodcastFeedDetails podcastFeedDetails) {
            YourPodcastAppMainRepository activity = activityReference.get();
            activity.podcastFeedDetailsItem = podcastFeedDetails;
            Log.v("What is PodcastFeed?", String.valueOf(podcastFeedDetails));
            Log.v("What is PodcastFeed?", String.valueOf(activity.podcastFeedDetailsItem));
        }
    }
}



