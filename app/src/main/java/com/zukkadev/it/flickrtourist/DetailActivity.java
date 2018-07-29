package com.zukkadev.it.flickrtourist;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.squareup.picasso.Picasso;
import com.zukkadev.it.flickrtourist.utils.Constants;

public class DetailActivity extends AppCompatActivity {
    private static final String TAG = "DetailsActivity";
    private AdView mAdView;
    Bundle imageBundle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        imageBundle = getIntent().getExtras();
        TextView title = findViewById(R.id.flickr_title_detail);
        ImageView imageView = findViewById(R.id.flickr_image_detail);

        title.setText(imageBundle.getString(Constants.Title));
        Picasso.with( DetailActivity.this ).load( imageBundle.getString(Constants.URL)).into(imageView);

        FloatingActionButton favoriteFab = findViewById(R.id.favorite_fab);
        favoriteFab.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               AskForIsFavoriteImage();
           }
        });
    }

    private void AskForIsFavoriteImage() {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle(getString(R.string.app_name))
                .setMessage(getString(R.string.ask_favorite_body))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(DetailActivity.this);
                        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(DetailActivity.this, MyWidget.class));
                        MyWidget.updateTextWidgets(
                                DetailActivity.this,
                                appWidgetManager,appWidgetIds,
                                imageBundle.getString(Constants.Title),
                                imageBundle.getString(Constants.URL)
                        );

                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
