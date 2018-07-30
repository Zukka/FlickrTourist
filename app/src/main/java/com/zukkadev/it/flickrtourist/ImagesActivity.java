package com.zukkadev.it.flickrtourist;

import android.appwidget.AppWidgetManager;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.ComponentName;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.zukkadev.it.flickrtourist.data.AppDatabase;
import com.zukkadev.it.flickrtourist.model.FlickrImages;
import com.zukkadev.it.flickrtourist.network.FlickrJsonUtils;
import com.zukkadev.it.flickrtourist.network.NetworkUtils;
import com.zukkadev.it.flickrtourist.utils.Constants;

import java.net.URL;
import java.util.List;

public class ImagesActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView imagesRecyclerView;
    private ImagesRecyclerViewAdapter imagesRecyclerViewAdapter;
    private GridLayoutManager gridLayoutManager;
    private AppDatabase mDb;
    private Bundle bundle;
    public static String downloadedPage = "0";
    private boolean isRefreshing = false;
    private List<FlickrImages> storedImages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);

        mDb = AppDatabase.getInstance(getApplicationContext());
        imagesRecyclerView = findViewById(R.id.images_recycled_view);
        imagesRecyclerView.setHasFixedSize(true);
        gridLayoutManager = new GridLayoutManager(this, (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) ? 3 : 6);
        imagesRecyclerView.setLayoutManager(gridLayoutManager);
        imagesRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mSwipeRefreshLayout = findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        bundle = getIntent().getExtras();
        mSwipeRefreshLayout.post(new Runnable() {

            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
                new FlickrRequest().execute(bundle);
            }
        });

        imagesRecyclerViewAdapter = new ImagesRecyclerViewAdapter(this);
        imagesRecyclerView.setAdapter(imagesRecyclerViewAdapter);
        retrieveFlickrImages();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBundle(Constants.BundleInstance, bundle);
        outState.putString(Constants.DownloadPageInstance, downloadedPage);
        outState.putBoolean(Constants.IsRefreshingInstance, isRefreshing);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        bundle = savedInstanceState.getBundle(Constants.BundleInstance);
        downloadedPage = savedInstanceState.getString(Constants.DownloadPageInstance);
        isRefreshing = savedInstanceState.getBoolean(Constants.IsRefreshingInstance);
    }

    @Override
    public void onRefresh() {
        isRefreshing = true;
        new FlickrRequest().execute(bundle);
    }


    private void retrieveFlickrImages() {
        LiveData<List<FlickrImages>> retrieveImages = mDb.flickrImagesDao().retrieveImages(Long.valueOf(bundle.getString(Constants.Pin)));
        retrieveImages.observe(this, new Observer<List<FlickrImages>>() {
            @Override
            public void onChanged(@Nullable List<FlickrImages> retrieveImagesList) {
                storedImages = retrieveImagesList;
            }
        });
    }

    public class FlickrRequest extends AsyncTask<Bundle, Void, List<FlickrImages>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mSwipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected List<FlickrImages> doInBackground(Bundle... bundles) {
            String marker = bundles[0].getString(Constants.Pin);
            if (storedImages != null && storedImages.size() > 0 && !isRefreshing)
               return storedImages;
            else {
                URL imagesRequestURL = NetworkUtils.buildRequestPhotosUrl(bundles[0].getDouble(Constants.Latitude), bundles[0].getDouble(Constants.Longitude), downloadedPage);
                try {
                    String jsonResponse = NetworkUtils.getResponseFromHttpUrl(imagesRequestURL);
                    List<FlickrImages> flickrImagesList = FlickrJsonUtils.getFlickrImagesFromJson(ImagesActivity.this, jsonResponse, Long.valueOf(marker));

                    return flickrImagesList;
                } catch (Exception e) {
                    return null;
                }
            }
        }

        @Override
        protected void onPostExecute(List<FlickrImages> flickrImages) {
            mSwipeRefreshLayout.setRefreshing(false);
            isRefreshing = false;
            if (flickrImages != null && flickrImages.size() > 0) {
                imagesRecyclerViewAdapter = new ImagesRecyclerViewAdapter(ImagesActivity.this);
                imagesRecyclerView.setVisibility(View.VISIBLE);
                imagesRecyclerViewAdapter.setImageData(flickrImages);
                imagesRecyclerView.setAdapter(imagesRecyclerViewAdapter);
            }
            else
                Toast.makeText(ImagesActivity.this, getString(R.string.loadImagesFailed), Toast.LENGTH_LONG).show();
        }
    }
}
