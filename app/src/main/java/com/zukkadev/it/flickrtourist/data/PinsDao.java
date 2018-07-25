package com.zukkadev.it.flickrtourist.data;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.zukkadev.it.flickrtourist.model.Pin;

import java.util.List;

@Dao
public interface PinsDao {

    @Query("SELECT * FROM Pin")
    List<Pin> getAllPins();

    @Query("SELECT * FROM Pin WHERE pin_id = :pinID")
    List<Pin> retrievePin(int pinID);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPin(Pin pin);

    @Query("DELETE FROM flickr_images")
    void nukeTable();
}