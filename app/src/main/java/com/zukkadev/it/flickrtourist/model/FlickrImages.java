package com.zukkadev.it.flickrtourist.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity (tableName = "flickr_images")
public class FlickrImages {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "image_id")
    private long ImageID;

    @ColumnInfo(name = "image_url")
    private String ImageURL;

    public FlickrImages(int id, long ImageID, String ImageURL) {
        this.id = id;
        this.ImageID = ImageID;
        this.ImageURL = ImageURL;
    }

    @Ignore
    public FlickrImages(long ImageID, String ImageURL) {
        this.ImageID = ImageID;
        this.ImageURL = ImageURL;
    }

    public int getId() { return id; }
    public long getImageID() { return ImageID; }
    public String getImageURL() { return ImageURL; }

    public void setId(int id) { this.id = id; }
    public void setImageID(long imageID) { this.ImageID = imageID; }
    public void setImageURL(String imageURL) { this.ImageURL = imageURL; }

}