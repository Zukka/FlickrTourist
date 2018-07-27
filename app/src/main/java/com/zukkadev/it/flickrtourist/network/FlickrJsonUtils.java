package com.zukkadev.it.flickrtourist.network;

import android.content.Context;

import com.zukkadev.it.flickrtourist.ImagesActivity;
import com.zukkadev.it.flickrtourist.data.AppDatabase;
import com.zukkadev.it.flickrtourist.model.FlickrImages;
import com.zukkadev.it.flickrtourist.utils.FlickrConstants;
import com.zukkadev.it.flickrtourist.utils.FlickrResponseKeys;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FlickrJsonUtils {

    public static List<FlickrImages> getFlickrImagesFromJson(Context context, String jsonResponse, long pinID) throws JSONException {

        final String OWM_MESSAGE_CODE = "cod";
        AppDatabase mDb = AppDatabase.getInstance(context.getApplicationContext());

        JSONObject imageJson = new JSONObject(jsonResponse);
        List<FlickrImages> parsedImageData;
        if (imageJson.has(OWM_MESSAGE_CODE)) {
            int errorCode = imageJson.getInt(OWM_MESSAGE_CODE);

            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    /* Location invalid */
                    return null;
                default:
                    /* Server probably down */
                    return null;
            }
        }

        mDb.flickrImagesDao().nukeTable();
        JSONObject jsonPhotos = imageJson.getJSONObject(FlickrResponseKeys.Photos);

        int totalpages =Integer.valueOf(jsonPhotos.getString(FlickrResponseKeys.Pages));
        Random randomPage = new Random();

        ImagesActivity.downloadedPage = String.valueOf(randomPage.nextInt(totalpages));
        JSONArray imageArray = jsonPhotos.getJSONArray(FlickrResponseKeys.Photo);

        parsedImageData = new ArrayList<>();
        for (int i = 0; i < imageArray.length(); i++) {
            JSONObject imageObject = imageArray.getJSONObject(i);

            String ImageTitle = imageObject.getString(FlickrResponseKeys.Title);
            String ImageURL = imageObject.getString(FlickrResponseKeys.MediumURL);
            FlickrImages flickrImage = new FlickrImages(pinID, ImageTitle, ImageURL);
            mDb.flickrImagesDao().insertImage(flickrImage);
            parsedImageData.add(flickrImage);
        }
        return parsedImageData;
    }
}
