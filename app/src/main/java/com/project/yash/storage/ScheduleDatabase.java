package com.project.yash.storage;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {ScheduleEntity.class}, version = 1,exportSchema = false)
public abstract class ScheduleDatabase extends RoomDatabase {

    public abstract ScheduleDao getScheduleDao();

    private static ScheduleDatabase instance;

    public static synchronized ScheduleDatabase getInstance(Context context) {
        if (instance == null)
            instance = Room.databaseBuilder(context, ScheduleDatabase.class, "ClassSchedule")
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        return instance;
    }


}
