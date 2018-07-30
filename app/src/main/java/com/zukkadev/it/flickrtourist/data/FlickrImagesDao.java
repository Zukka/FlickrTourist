package com.zukkadev.it.flickrtourist.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.zukkadev.it.flickrtourist.model.FlickrImages;

import java.util.List;

@Dao
public interface FlickrImagesDao {

    @Query("SELECT * FROM flickr_images")
    LiveData<List<FlickrImages>> getAllImages();

    @Query("SELECT * FROM flickr_images WHERE image_id = :imageID")
    LiveData<List<FlickrImages>> retrieveImages(long imageID);

    @Query("SELECT * FROM flickr_images WHERE image_id = :imageID")
    List<FlickrImages> retrieveImage(int imageID);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertImage(FlickrImages flickrImage);

    @Query("DELETE FROM flickr_images")
    void nukeTable();
}
