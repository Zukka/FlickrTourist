package com.zukkadev.it.flickrtourist.network;

import com.zukkadev.it.flickrtourist.model.FlickrImages;
import com.zukkadev.it.flickrtourist.utils.FlickrConstants;
import com.zukkadev.it.flickrtourist.utils.FlickrResponseKeys;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

public class FlickrJsonUtils {
    public static List<FlickrImages> getFlickrImagesFromJson(String jsonResponse) throws JSONException {

        final String OWM_MESSAGE_CODE = "cod";
        final String IMAGE_LIST = "photo";

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

        JSONArray imageArray = imageJson.getJSONArray(IMAGE_LIST);
        parsedImageData = new ArrayList<>();
        for (int i = 0; i < imageArray.length(); i++) {
            JSONObject imageObject = imageArray.getJSONObject(i);

            long ImageId = (long) imageObject.get(FlickrResponseKeys.Id);
            String ImageTitle = imageObject.get(FlickrResponseKeys.Title).toString();
            String ImageURL = imageObject.get(FlickrResponseKeys.MediumURL).toString();
            FlickrImages flickrImage = new FlickrImages(ImageId, ImageTitle, ImageURL);
            parsedImageData.add(flickrImage);
        }
        return parsedImageData;
    }
}
