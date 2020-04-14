package com.example.android.yourpodcastapp;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

import com.squareup.picasso.Picasso;

/**
 * Implementation of App Widget functionality.
 */
//App widget to show last favorited item
public class NewAppWidget extends AppWidgetProvider {
//    Keep the information in a string variable to update remoteViews
    private String last_favorited = "";
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);
        views.setTextViewText(R.id.appwidget_text, widgetText);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int i = 0; i < appWidgetIds.length; i++) {

            Log.v("TestonUpdateisCalled", "It's called");
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
//Refer to remote views and prepare to update the details
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);
            views.setOnClickPendingIntent(R.id.app_widget, pendingIntent);
            String finText = " You have favorited that podcast lately. Would you like to listen to it some more?";
//           Update only if there is a valid item not the originally blank String
            if (last_favorited.length() >2)
            {
            views.setTextViewText(R.id.widgetText, String.valueOf(last_favorited + finText));}

            appWidgetManager.updateAppWidget(appWidgetIds[i], views);

        }
    }
    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        String action = intent.getAction();
// Get the additional information on onReceive
        if (action.equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
            if (intent.hasExtra("Last favorited podcast")) {
                String last_favorite = intent.getExtras().getString("Last favorited podcast", "");
                if (last_favorite != null || last_favorite.equals("No Image")){
                    last_favorited = last_favorite;
                }
                AppWidgetManager mgr = AppWidgetManager.getInstance(context);
                ComponentName cn = new ComponentName(context, NewAppWidget.class);

            }
            super.onReceive(context, intent);
        }
    }

}

