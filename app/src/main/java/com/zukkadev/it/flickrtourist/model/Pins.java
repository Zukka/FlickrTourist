package com.zukkadev.it.flickrtourist.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "pins")
public class Pins {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "pin_id")
    private int PinID;

    @ColumnInfo(name = "longitude")
    private String PinLongitude;

    @ColumnInfo(name = "latitude")
    private String PinLatitude;

    public Pins(int id, int PinID, String PinLatitude, String PinLongitude) {
        this.id = id;
        this.PinID = PinID;
        this.PinLatitude = PinLatitude;
        this.PinLongitude = PinLongitude;
    }

    @Ignore
    public Pins(int PinID, String PinLatitude, String PinLongitude) {
        this.PinID = PinID;
        this.PinLatitude = PinLatitude;
        this.PinLongitude = PinLongitude;
    }

    public int getId() { return id; }
    public int getPinID() { return PinID; }
    public String getPinLatitude() { return PinLatitude; }
    public String getPinLongitude() { return PinLongitude; }

    public void setId(int id) { this.id = id; }
    public void setPinID(int pinID) { this.PinID = pinID; }
    public void setPinLatitude(String PinLatitude) { this.PinLatitude = PinLatitude; }
    public void setPinLongitude(String PinLongitude) { this.PinLongitude = PinLongitude; }

    }
