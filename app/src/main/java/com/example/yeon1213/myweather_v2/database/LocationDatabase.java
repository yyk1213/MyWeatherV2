package com.example.yeon1213.myweather_v2.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

@Database(entities={LocationData.class},version=1)
public abstract class LocationDatabase extends RoomDatabase{

    private static LocationDatabase INSTANCES;

    public abstract LocationDAO getLocationDAO();

    public static LocationDatabase getDataBase(final Context context) {

        if (INSTANCES == null) {

            INSTANCES = Room.databaseBuilder(context.getApplicationContext(), LocationDatabase.class, "location.db")
                    .allowMainThreadQueries()
                    .build();
        }
            return INSTANCES;
    }
}
