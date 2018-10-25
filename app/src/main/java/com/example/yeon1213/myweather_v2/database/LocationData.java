package com.example.yeon1213.myweather_v2.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class LocationData {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "Id")
    private int mId;
    @NonNull
    @ColumnInfo(name = "Latitude")
    private double mLatitude;
    @NonNull
    @ColumnInfo(name = "Longitude")
    private double mLongitude;

    @ColumnInfo(name = "Location_name")
    @NonNull
    private String mLocation_name;

    @ColumnInfo(name="Location_address")
    @NonNull
    private String mLocation_address;

    @NonNull
    @ColumnInfo(name = "Time")
    private String mTime;

    @NonNull
    @ColumnInfo(name = "Day_of_week")
    private int mDayOfWeek;

    @ColumnInfo(name = "AlarmCheck")
    private boolean mAlarmCheck;

    public boolean getMAlarmCheck() {
        return mAlarmCheck;
    }

    public void setMAlarmCheck(boolean mAlarmCheck) {
        this.mAlarmCheck = mAlarmCheck;
    }

    @NonNull
    public int getMId() {
        return mId;
    }

    public void setMId(@NonNull int mId) {
        this.mId = mId;
    }
    @NonNull
    public double getMLatitude() {
        return mLatitude;
    }

    public void setMLatitude(@NonNull double mLatitude) {
        this.mLatitude = mLatitude;
    }
    @NonNull
    public double getMLongitude() {
        return mLongitude;
    }

    public void setMLongitude(@NonNull double mLongitude) {
        this.mLongitude = mLongitude;
    }

    @NonNull
    public String getMTime() {
        return mTime;
    }

    public void setMTime(@NonNull String mTime) {
        this.mTime = mTime;
    }

    @NonNull
    public String getMLocation_name() {
        return mLocation_name;
    }

    public void setMLocation_name(@NonNull String mLocation_name) {
        this.mLocation_name = mLocation_name;
    }

    @NonNull
    public int getMDayOfWeek() {
        return mDayOfWeek;
    }

    public void setMDayOfWeek(@NonNull int mDay_of_week) {
        this.mDayOfWeek = mDay_of_week;
    }

    @NonNull
    public String getMLocation_address() {
        return mLocation_address;
    }

    public void setMLocation_address(@NonNull String mLocation_address) {
        this.mLocation_address = mLocation_address;
    }
}
