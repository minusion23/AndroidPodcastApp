package com.example.android.yourpodcastapp;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Szymon on 08.02.2019.
 */


/**
 * Created by Szymon on 07.02.2019.
 */

public class UserSearchPodcastUtilities {


    private static final int readTimeout = 10000;
    private static final int connectionTimeout = 15000;
    private static final int isOk = 200;

    //    Run through JSON file informaiton and retrieve all the details
    private static List<PodcastSearchItem> extractFeatureFromJson(String jsonResponse) {
        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }

        List<PodcastSearchItem> podcastItems = new ArrayList<>();


        try {

            JSONObject baseJSONResponse = new JSONObject(jsonResponse);
            JSONArray baseJSONResponseArrray = baseJSONResponse.getJSONArray("results");

            Log.v("baseJSON", String.valueOf(baseJSONResponseArrray.length()));
            for (int i = 0; i < baseJSONResponseArrray.length(); i++) {

                JSONObject currentResult = baseJSONResponseArrray.getJSONObject(i);

                String id; //id of the podcast to be used for the feed
                String name;
                String artwork;
                String feedUrl;


                List<PodcastSearchItem> PodcastSearchItemsList;
                PodcastSearchItemsList = new ArrayList<>();
                id = currentResult.optString("collectionId");
                name = currentResult.optString("collectionName");
                artwork = currentResult.optString("artworkUrl100");
                feedUrl = currentResult.optString("feedUrl");

                Log.v("Namesear", name);
                Log.v("Namesear", id);
                Log.v("Namesear", artwork);
                Log.v("Namesear", feedUrl);

                PodcastSearchItem searchItem = new PodcastSearchItem(id, name, artwork, feedUrl);
                Log.v("searchItem", String.valueOf(searchItem));
                podcastItems.add(searchItem);

            }

        } catch (JSONException e) {
            Log.e("Query Utils", "Problem parsing JSON Response", e);
        }
        Log.v("podcastItems", podcastItems.toString());
        return podcastItems;
    }

    //Make an http request to get the details from the web
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        if (url == null) {
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(readTimeout);
            urlConnection.setConnectTimeout(connectionTimeout);
            urlConnection.connect();
            if (urlConnection.getResponseCode() == isOk) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e("Log_TAG", "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e("Exception", "An Exception was thrown", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    //    Read from the Http response
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader bufferReader = new BufferedReader(inputStreamReader);
            String line = bufferReader.readLine();
            while (line != null) {
                output.append(line);
                line = bufferReader.readLine();
            }
        }
        return output.toString();
    }

    //    Query API return recipies
    public static List<PodcastSearchItem> fetchSearchItems(URL url) {

        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e("Query utils", "Problem making the HTTP request.", e);
        }
        // Extract relevant fields from the JSON response and create a list of {@link Recipes}
        Log.e("LOG e", jsonResponse);
        return extractFeatureFromJson(jsonResponse);
    }

    public static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException exception) {
            Log.e("LOG_TAG", String.valueOf(R.string.URL_error_string), exception);

            return null;
        }
        return url;
    }
}
