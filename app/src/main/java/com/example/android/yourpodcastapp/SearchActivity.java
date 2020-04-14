package com.example.android.yourpodcastapp;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.android.yourpodcastapp.Adapters.PodcastSearchItemAdapter;

import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Szymon on 07.02.2019.
 */

public class SearchActivity extends AppCompatActivity implements PodcastSearchItemAdapter.GridViewClickListener {

    private ArrayList<PodcastSearchItem> searchItems;
    private RecyclerView mRecyclerView;
    private PodcastSearchItemAdapter adapter;
    private String searchString;
    private Boolean userInitiated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);
//      Check orientation for a proper Grid Layout
        int orientation = getResources().getConfiguration().orientation;

        //        Prepare to check internet connection

        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connManager != null;
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();

//        Load differently if there was a saved instance state

        if (savedInstanceState == null || !savedInstanceState.containsKey("searchItems")) {
            setContentView(R.layout.search_activity);
            if (networkInfo != null && networkInfo.isConnected()) {
                URL url = QueryUtilities.createUrl(Constants.INITIAL_MOST_POPULAR_SEARCH);
                new podcastQueryTask(this).execute(url);
            } else {
                Toast toast = Toast.makeText(this,getString(R.string.no_internet_toast), Toast.LENGTH_LONG);
                toast.show();
                setContentView(R.layout.internet_error);
            }

        } else {
//        Recreate the search if nothing has changed
            setContentView(R.layout.search_activity);
            searchItems = savedInstanceState.getParcelableArrayList("searchItems");
            mRecyclerView = (RecyclerView) findViewById(R.id.search_rv);

            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                mRecyclerView.setLayoutManager(new GridLayoutManager(mRecyclerView.getContext(), 3));
            } else {
                mRecyclerView.setLayoutManager(new GridLayoutManager(mRecyclerView.getContext(), 2));
            }

            adapter = new PodcastSearchItemAdapter(searchItems, getApplicationContext(), SearchActivity.this);
            mRecyclerView.setAdapter(adapter);
        }

//        Set up a search function to query
        final EditText searchEditText = findViewById(R.id.searchPodcastEditText);
        ImageButton mUpButton = findViewById(R.id.action_up);
        mUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSupportNavigateUp();
            }
        });
        ImageButton searchImageButton = findViewById(R.id.searchImageButton);

        searchImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchString = searchEditText.getText().toString();
                userInitiated = true;
                updateUi();

            }
        });

    }
//    Update UI
    public void updateUi() {
        URL url = QueryUtilities.createUrl(Constants.ITUNES_Search_base_url.concat(searchString).concat(Constants.ITUNES_SEARCH_PODCAST).concat(Constants.SEARCH_RESULT_LIMIT));
        Log.v("What URL", String.valueOf(url));
        new podcastQueryTask(this).execute(url);
    }

//    Once item clicked open details
    @Override
    public void onGridItemClick(int clickedItemIndex) {
        PodcastSearchItem currentPodcastSearchItem = searchItems.get(clickedItemIndex);
        Intent intent = new Intent(getApplicationContext(), PodcastDetails.class);
        intent.putExtra("searchItemClickedDetails", searchItems.get(clickedItemIndex));
        startActivity(intent);
    }
//Save state
    @Override
    public void onSaveInstanceState(Bundle OutState) {
        OutState.putParcelableArrayList("searchItems", searchItems);
        super.onSaveInstanceState(OutState);
    }

//    Query the database
    private static class podcastQueryTask extends AsyncTask<URL, Void, List<PodcastSearchItem>> {
        private WeakReference<SearchActivity> activityReference;

        podcastQueryTask(SearchActivity context) {
            activityReference = new WeakReference<SearchActivity>(context);
        }

        @Override
        protected List<PodcastSearchItem> doInBackground(URL... urls) {
            URL searchURL = urls[0];
            SearchActivity activity = activityReference.get();
            if (activity.userInitiated == true) {
                return UserSearchPodcastUtilities.fetchSearchItems(searchURL);
            } else {
                return QueryUtilities.fetchSearchItems(searchURL);
            }
        }


        @Override
        protected void onPostExecute(List<PodcastSearchItem> searchItemList) {
            SearchActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }
            Log.v("SearchItemList", String.valueOf(searchItemList));
            activity.searchItems = new ArrayList<PodcastSearchItem>(searchItemList);

            activity.mRecyclerView = (RecyclerView) activity.findViewById(R.id.search_rv);
            int orientation = activity.getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                activity.mRecyclerView.setLayoutManager(new GridLayoutManager(activity.mRecyclerView.getContext(), 3));

            } else {
                activity.mRecyclerView.setLayoutManager(new GridLayoutManager(activity.mRecyclerView.getContext(), 2));

            }
            activity.adapter = new PodcastSearchItemAdapter(activity.searchItems, activity.getApplicationContext(), activity);
            activity.mRecyclerView.setAdapter(activity.adapter);
            activity.userInitiated = false;
        }

    }
}




