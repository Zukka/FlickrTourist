package com.zukkadev.it.flickrtourist.network;

import android.net.Uri;

import com.zukkadev.it.flickrtourist.utils.FlickrConstants;
import com.zukkadev.it.flickrtourist.utils.FlickrParameterKeys;

import java.net.MalformedURLException;
import java.net.URL;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class NetworkUtils {
    public static URL buildRequestImagesUrl(long latitude, long longitude) {
        String baseURL = FlickrConstants.APIScheme + FlickrConstants.APIHost + FlickrConstants.APIPath;
        Uri builtUri = Uri.parse(baseURL).buildUpon()
                .appendQueryParameter(FlickrParameterKeys.APIKey, FlickrParameterKeys.APIKey)
                .appendQueryParameter(FlickrParameterKeys.BoundingBox, bboxString(latitude, longitude))
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    private static String bboxString(long latitude, long longitude) {
        String minLon = String.valueOf(max(longitude - FlickrConstants.SearchBBoxHalfWidth, FlickrConstants.SearchLonRange[0]));
        String minLat = String.valueOf(max(latitude - FlickrConstants.SearchBBoxHalfHeight, FlickrConstants.SearchLatRange[0]));
        String maxLon = String.valueOf(min(longitude + FlickrConstants.SearchBBoxHalfWidth, FlickrConstants.SearchLonRange[1]));
        String maxLat = String.valueOf(min(latitude + FlickrConstants.SearchBBoxHalfHeight, FlickrConstants.SearchLatRange[1]));
        return minLon + "," + minLat + "," + maxLon + "," + maxLat;
    }
}
