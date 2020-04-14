package com.example.android.yourpodcastapp;

import android.text.Html;
import android.util.Log;
import android.util.Xml;

import com.example.android.yourpodcastapp.Database.PodcastEpisodeDetails;
import com.example.android.yourpodcastapp.Database.PodcastFeedDetails;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Szymon on 08.02.2019.
 */
//https://developer.android.com/training/basics/network-ops/xml taken from
//    Parse the RSS feed response to get the podcast information

public class RSSQueryUtilitiesXMLParser {

    private static final String ns = null;

    public static PodcastFeedDetails parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            Log.v("isparser 1 working", String.valueOf(parser));
            return readFeed(parser);
        } finally {
            in.close();
        }
    }


    private static PodcastFeedDetails readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        PodcastFeedDetails podcastDet = null;

        parser.require(XmlPullParser.START_TAG, ns, "rss");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            Log.v("Parser string name", name);

            // Starts by looking for the entry tag
            if (name.equals("channel")) {
                podcastDet =readEntry(parser);
                Log.v("PodcastDetError", String.valueOf(podcastDet));
            } else {
                skip(parser);
            }
        }
        return podcastDet;
    }

    // Parses the contents of an entry. If it encounters a title, summary, or link tag, hands them off
// to their respective "read" methods for processing. Otherwise, skips the tag.
    private static PodcastFeedDetails readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "channel");

        String podcastTitle = null;
        String podcastDescription = null;
        String podcastImage = null;

        PodcastEpisodeDetails podcastEpisodeDetailsItem;


        List<PodcastEpisodeDetails> podcastEpisodeDetails = new ArrayList<>();

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            Log.v("parserNameReadEntry", name);
            if (name.equals("title")) {
                podcastTitle = readTitle(parser);
                Log.v("What's the podcast title", podcastTitle);
            } else if (name.equals("itunes:summary")) {
                podcastDescription = readDescription(parser);
                Log.v("What's the podcast title", podcastDescription);
            }
            else if (name.equals("itunes:image")) {
                podcastImage = readPodcastImage(parser);
                Log.v("What's the podcast title", podcastImage);
            }
            else if (name.equals("item")) {
                podcastEpisodeDetailsItem = readItemEntry(parser, podcastImage);
                podcastEpisodeDetails.add(podcastEpisodeDetailsItem);

            } else {
                skip(parser);
            }
        }
        return new PodcastFeedDetails(podcastTitle, podcastDescription, podcastEpisodeDetails, podcastImage);
    }


    private static PodcastEpisodeDetails readItemEntry(XmlPullParser parser, String artwork) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "item");

        String episodeTitle = null;
        String seasonNumber = null;
        String episodeNumber = null;
        String episodeDescription = null;
        String episodeDate = null;
        String episodeDuration = null;
        String episodeAudioLink = null;
        String episodeArt = null;
        PodcastEpisodeDetails podcastEpisodeDetailsItem;


        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String nameItem = parser.getName();
            Log.v("parserNameReadEntry", nameItem);
            if (nameItem.equals("title")) {
                episodeTitle = readEpisodeTitle(parser);
                Log.v("What's the item title", episodeTitle);
            } else if (nameItem.equals("description")) {
                episodeDescription = readEpisodeDescription(parser);
                Log.v("What's the item title", episodeDescription);
            } else if (nameItem.equals("pubDate")) {
                episodeDate = readEpisodeDate(parser);
                Log.v("What's the item title", episodeDate);
            } else if (nameItem.equals("itunes:season")) {
                seasonNumber = readSeasonNumber(parser);
                Log.v("What's the item title", seasonNumber);
            } else if (nameItem.equals("itunes:episode")) {
                episodeNumber = readEpisodeNumber(parser);
                Log.v("What's the item title", episodeNumber);
            } else if (nameItem.equals("itunes:duration")) {
                episodeDuration = readEpisodeDuration(parser);
                Log.v("What's the item title", episodeDuration);
            }
            else if (nameItem.equals("enclosure")) {
                episodeAudioLink = readEpisodeAudioLink(parser);

                Log.v("What's the item title", episodeAudioLink);
            }

         else {
                skip(parser);
            }
        }
        podcastEpisodeDetailsItem = new PodcastEpisodeDetails(episodeTitle, episodeDate, episodeDescription, episodeDuration, episodeAudioLink, seasonNumber, episodeNumber, artwork);
        return podcastEpisodeDetailsItem;
    }


    private static String readImage(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "itunes:image");

        String podcastImage = null;

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            Log.v("parserNameReadEntry", name);
            if (name.equals("itunes:image")) {
                podcastImage = readPodcastImage(parser);

            } else {
                skip(parser);
            }
        }
        return podcastImage;
    }




    // Processes title tags in the feed.
    private static String readTitle(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "title");
        String title = readText(parser);
        title = Html.fromHtml(title).toString();
        parser.require(XmlPullParser.END_TAG, ns, "title");
        return title;
    }





    private static String readDescription(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "itunes:summary");
        String title = readText(parser);
        title = Html.fromHtml(title).toString();
        parser.require(XmlPullParser.END_TAG, ns, "itunes:summary");
        return title;
    }


    private static String readPodcastImage(XmlPullParser parser) throws IOException, XmlPullParserException {
        String link = "";
        parser.require(XmlPullParser.START_TAG, ns, "itunes:image");
        String tag = parser.getName();
        link = parser.getAttributeValue(null, "href");
        parser.nextTag();
        String relType = parser.getAttributeValue(null, "rel");
        parser.require(XmlPullParser.END_TAG, ns, "itunes:image");
        return link;
    }


    private static String readEpisodeTitle(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "title");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "title");
        return title;
    }
    private  static String readEpisodeDescription(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "description");
        String title = readText(parser);
        title = Html.fromHtml(title).toString();
        parser.require(XmlPullParser.END_TAG, ns, "description");
        return title;
    }
    private static String readEpisodeDate(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "pubDate");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "pubDate");
        return title;
    }
    private static String readSeasonNumber(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "itunes:season");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "itunes:season");
        return title;
    }
    private static String readEpisodeNumber(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "itunes:episode");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "itunes:episode");
        return title;
    }
    private static String readEpisodeDuration(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "itunes:duration");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "itunes:duration");
        return title;
    }


    // Processes link tags in the feed.
    private static String readEpisodeAudioLink(XmlPullParser parser) throws IOException, XmlPullParserException {
        String link = "";
        parser.require(XmlPullParser.START_TAG, ns, "enclosure");
        String tag = parser.getName();
        link = parser.getAttributeValue(null, "url");
        parser.nextTag();
        parser.require(XmlPullParser.END_TAG, ns, "enclosure");
        return link;
    }


    // For the tags title and summary, extracts their text values.
    private static  String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }
    private static void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }

    }

    public static PodcastFeedDetails loadXmlFromNetwork(String urlString) throws XmlPullParserException, IOException {
        InputStream stream = null;
        // Instantiate the parser
//        RSSQueryUtilitiesXMLParser rssQueryUtilitiesXMLParser = new RSSQueryUtilitiesXMLParser();
        PodcastFeedDetails entries = null;

        try {
            stream = downloadUrl(urlString);
            Log.v("Stream works", String.valueOf(stream));
            entries = parse(stream);
            Log.v("Entries work", String.valueOf(entries));
            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (stream != null) {
                stream.close();
            }
        }

        return entries;
    }


    private static InputStream downloadUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Starts the query
        conn.connect();
        return conn.getInputStream();
    }
}

