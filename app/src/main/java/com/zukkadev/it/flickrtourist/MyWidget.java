package com.zukkadev.it.flickrtourist;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.RemoteViews;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;

/**
 * Implementation of App Widget functionality.
 */
public class MyWidget extends AppWidgetProvider {

    static void updateAppWidget(final Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, String title, String imageURL) {

        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.my_widget);
        views.setTextViewText(R.id.widget_title, title);
        Picasso.with(context).load(imageURL).into(new Target() {

            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom arg1) {
                views.setImageViewBitmap(R.id.widget_image, bitmap);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                errorDrawable = context.getDrawable(R.drawable.warning);
                Bitmap placeholderIcon = ((BitmapDrawable) errorDrawable).getBitmap();
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                placeHolderDrawable = context.getDrawable(R.drawable.photo);
                Bitmap placeholderIcon = ((BitmapDrawable) placeHolderDrawable).getBitmap();

            }

        });
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.widget_title, pendingIntent);
        views.setOnClickPendingIntent(R.id.widget_image, pendingIntent);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

    }

    public static void updateTextWidgets(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds, String title, String imageURL) {

        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, title, imageURL);
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
}

