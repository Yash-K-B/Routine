package com.project.yash.storage;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ScheduleDao {

    @Insert
    void insert(ScheduleEntity entity);

    @Update
    void update(ScheduleEntity entity);

    @Query("select * from ScheduleEntity order by time asc")
    List<ScheduleEntity> getAll();

    @Query("delete from ScheduleEntity where 1")
    void clear();
}
