package com.zukkadev.it.flickrtourist.network;

import android.net.Uri;

import com.zukkadev.it.flickrtourist.utils.FlickrConstants;
import com.zukkadev.it.flickrtourist.utils.FlickrParameterKeys;
import com.zukkadev.it.flickrtourist.utils.FlickrParameterValues;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class NetworkUtils {
    public static URL buildRequestPhotosUrl(double latitude, double longitude, String page) {
        System.out.println("**** PAGE: " + page);
        String baseURL = FlickrConstants.APIScheme + FlickrConstants.APIHost + FlickrConstants.APIPath;
        Uri builtUri = Uri.parse(baseURL).buildUpon()
                .appendQueryParameter(FlickrParameterKeys.Method, FlickrParameterValues.SearchMethod)
                .appendQueryParameter(FlickrParameterKeys.APIKey, FlickrParameterValues.APIKey)
                .appendQueryParameter(FlickrParameterKeys.Extras, FlickrParameterValues.MediumURL)
                .appendQueryParameter(FlickrParameterKeys.BoundingBox, bboxString(latitude, longitude))
                .appendQueryParameter(FlickrParameterKeys.Format, FlickrParameterValues.ResponseFormat)
                .appendQueryParameter(FlickrParameterKeys.NoJSONCallback, FlickrParameterValues.DisableJSONCallback)
                .appendQueryParameter(FlickrParameterKeys.PerPages, FlickrParameterValues.PerPages)
                .appendQueryParameter(FlickrParameterKeys.Page, page)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    private static String bboxString(double latitude, double longitude) {
        String minLon = String.valueOf(max(longitude - FlickrConstants.SearchBBoxHalfWidth, FlickrConstants.SearchLonRange[0]));
        String minLat = String.valueOf(max(latitude - FlickrConstants.SearchBBoxHalfHeight, FlickrConstants.SearchLatRange[0]));
        String maxLon = String.valueOf(min(longitude + FlickrConstants.SearchBBoxHalfWidth, FlickrConstants.SearchLonRange[1]));
        String maxLat = String.valueOf(min(latitude + FlickrConstants.SearchBBoxHalfHeight, FlickrConstants.SearchLatRange[1]));
        return minLon + "," + minLat + "," + maxLon + "," + maxLat;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream inputStream = urlConnection.getInputStream();
            Scanner scanner = new Scanner(inputStream);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
