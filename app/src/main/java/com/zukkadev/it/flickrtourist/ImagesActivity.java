package com.zukkadev.it.flickrtourist;

import android.content.res.Configuration;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.zukkadev.it.flickrtourist.data.AppDatabase;
import com.zukkadev.it.flickrtourist.model.FlickrImages;
import com.zukkadev.it.flickrtourist.network.FlickrJsonUtils;
import com.zukkadev.it.flickrtourist.network.NetworkUtils;
import com.zukkadev.it.flickrtourist.utils.Constants;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ImagesActivity extends AppCompatActivity {

    private RecyclerView imagesRecyclerView;
    private ImagesRecyclerViewAdapter imagesRecyclerViewAdapter;
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

        Bundle bundle = getIntent().getExtras();
        new FlickrRequest().execute(bundle);

        imagesRecyclerViewAdapter = new ImagesRecyclerViewAdapter(this);
        imagesRecyclerView.setAdapter(imagesRecyclerViewAdapter);
    }

    private void isProgressBarVisible(boolean isVisible) {
        if (isVisible)
            loadingDataProgressBar.setVisibility(View.VISIBLE);
        else
            loadingDataProgressBar.setVisibility((View.GONE));
    }

    public class FlickrRequest extends AsyncTask<Bundle, Void, List<FlickrImages>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            isProgressBarVisible(true);
        }

        @Override
        protected List<FlickrImages> doInBackground(Bundle... bundles) {
            long marker = bundles[0].getLong(Constants.Pin);
            List<FlickrImages> imagesData = mDb.flickrImagesDao().retrieveImages(marker);
            if (imagesData != null && imagesData.size() > 0)
               return imagesData;
            else {
                URL imagesRequestURL = NetworkUtils.buildRequestPhotosUrl(bundles[0].getDouble(Constants.Latitude), bundles[0].getDouble(Constants.Longitude));
                try {
                    String jsonResponse = NetworkUtils.getResponseFromHttpUrl(imagesRequestURL);
                    List<FlickrImages> flickrImagesList = FlickrJsonUtils.getFlickrImagesFromJson(jsonResponse);
                    for (FlickrImages image: flickrImagesList) {
                        mDb.flickrImagesDao().insertImage(image);
                    }
                    return flickrImagesList;
                } catch (Exception e) {
                    return null;
                }
            }
        }

        @Override
        protected void onPostExecute(List<FlickrImages> flickrImages) {
            isProgressBarVisible(false);
            if (flickrImages != null && flickrImages.size() > 0) {
                imagesRecyclerView.setVisibility(View.VISIBLE);
                imagesRecyclerViewAdapter.setFilmData(flickrImages);
                imagesRecyclerView.setAdapter(imagesRecyclerViewAdapter);
            }
            else
                Toast.makeText(ImagesActivity.this, getString(R.string.loadImagesFailed), Toast.LENGTH_LONG).show();
        }
    }
}
