package com.zukkadev.it.flickrtourist.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.zukkadev.it.flickrtourist.model.FlickrImages;

import java.util.List;

@Dao
public interface FlickrImagesDao {

    @Query("SELECT * FROM flickr_images")
    List<FlickrImages> getAllImages();

    @Query("SELECT * FROM flickr_images WHERE image_id = :imageID")
    List<FlickrImages> retrieveImages(long imageID);

    @Query("SELECT * FROM flickr_images WHERE image_id = :imageID")
    List<FlickrImages> retrieveImage(int imageID);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertImage(FlickrImages step);

    @Query("DELETE FROM flickr_images")
    void nukeTable();
}
