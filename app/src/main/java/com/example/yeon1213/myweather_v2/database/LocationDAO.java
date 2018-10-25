package com.example.yeon1213.myweather_v2.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface LocationDAO{

    @Insert
    void insert(LocationData locationdata);

    @Query("SELECT * FROM LocationData")
    List<LocationData> getLocation();

    @Query("SELECT * FROM LocationData Where Id =:ID")
    LocationData getData(int ID);

    @Update(onConflict = OnConflictStrategy.IGNORE)
    void update(LocationData locationData);

    @Delete
    void delete(LocationData locationData);
}
