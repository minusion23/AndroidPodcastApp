package com.example.android.yourpodcastapp;

import java.net.URL;

/**
 * Created by Szymon on 07.02.2019.
 */

public class Constants {



// URL for basic search request getting the original 25 items*/

    public static final String INITIAL_MOST_POPULAR_SEARCH = "https://rss.itunes.apple.com/api/v1/us/podcasts/top-podcasts/all/25/explicit.json";


    public static final String ITUNES_Search_base_url = "https://itunes.apple.com/search?term=";
//    Search will always be for podcast
    public static final String ITUNES_SEARCH_PODCAST = "&media=podcast";

    public static final String ITUNES_LOOKUP  = "https://itunes.apple.com/lookup?id=";

//Basic search used for the initial screen
    public static final String SEARCH_RESULT_LIMIT = "&limit=25";

    // URL for a basic lookup request, artist can be found with a specific unique id  */
    public static final String ITUNES_BASE = "https://itunes.apple.com/lookup";

    /** Base URL for iTunes rss API */
    public static final String I_TUNES_BASE_URL = "https://rss.itunes.apple.com/api/v1/";

}
