package com.zukkadev.it.flickrtourist.data;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.zukkadev.it.flickrtourist.model.Pins;

import java.util.List;

@Dao
public interface PinsDao {

    @Query("SELECT * FROM pins")
    List<Pins> getAllPins();

    @Query("SELECT * FROM pins WHERE pin_id = :pinID")
    List<Pins> retrievePin(int pinID);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPin(Pins step);

    @Query("DELETE FROM flickr_images")
    void nukeTable();
}