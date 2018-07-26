package com.zukkadev.it.flickrtourist;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.zukkadev.it.flickrtourist.data.AppDatabase;
import com.zukkadev.it.flickrtourist.model.FlickrImages;
import com.zukkadev.it.flickrtourist.network.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ImagesActivity extends AppCompatActivity {

    private RecyclerView imagesRecyclerView;
  //  private ImageRecycleViewAdapter imageRecycleViewAdapter;
    private GridLayoutManager gridLayoutManager;
    private ProgressBar loadingDataProgressBar;
    private AppDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);

        mDb = AppDatabase.getInstance(getApplicationContext());
        loadingDataProgressBar = findViewById(R.id.progressBar);
        imagesRecyclerView = findViewById(R.id.images_recycled_view);
        imagesRecyclerView.setHasFixedSize(true);
        gridLayoutManager = new GridLayoutManager(this, (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) ? 3 : 6);
        imagesRecyclerView.setLayoutManager(gridLayoutManager);
        imagesRecyclerView.setItemAnimator(new DefaultItemAnimator());

        List<Long> imageStartData = new ArrayList<>();

        Intent intent = new Intent();
        imageStartData.add(intent.getLongExtra(getString(R.string.marker_title), 0));
        imageStartData.add(intent.getLongExtra(getString(R.string.latitude), 0));
        imageStartData.add(intent.getLongExtra(getString(R.string.longitude), 0));
        new FlickrRequest().execute(imageStartData);
    }

    public class FlickrRequest extends AsyncTask<List<Long>, Void, List<FlickrImages>> {

        @Override
        protected List<FlickrImages> doInBackground(List<Long>... lists) {
            long marker = lists[0].get(0);
            List<FlickrImages> imagesData = mDb.flickrImagesDao().retrieveImages(marker);
            if (imagesData != null && imagesData.size() > 0) {
               return imagesData;
            } else {
                URL imagesRequestURL = NetworkUtils.buildRequestImagesUrl(lists[0].get(1), lists[0].get(2));
            }
            return null;
        }
    }
}
