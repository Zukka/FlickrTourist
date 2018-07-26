package com.zukkadev.it.flickrtourist.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "pin")
public class Pin {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "pin_id")
    private long PinID;

    @ColumnInfo(name = "longitude")
    private double PinLongitude;

    @ColumnInfo(name = "latitude")
    private double PinLatitude;

    public Pin(int id, long PinID, double PinLatitude, double PinLongitude) {
        this.id = id;
        this.PinID = PinID;
        this.PinLatitude = PinLatitude;
        this.PinLongitude = PinLongitude;
    }

    @Ignore
    public Pin(long PinID, double PinLatitude, double PinLongitude) {
        this.PinID = PinID;
        this.PinLatitude = PinLatitude;
        this.PinLongitude = PinLongitude;
    }

    public int getId() { return id; }
    public long getPinID() { return PinID; }
    public double getPinLatitude() { return PinLatitude; }
    public double getPinLongitude() { return PinLongitude; }

    public void setId(int id) { this.id = id; }
    public void setPinID(long pinID) { this.PinID = pinID; }
    public void setPinLatitude(double PinLatitude) { this.PinLatitude = PinLatitude; }
    public void setPinLongitude(double PinLongitude) { this.PinLongitude = PinLongitude; }

    }
