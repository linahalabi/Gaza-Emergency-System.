package com.example.gazaemergencysystem.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.gazaemergencysystem.data.model.Room;
import java.util.List;

@Dao
public interface RoomDao {

    @Insert
    long insert(Room room);

    @Update
    void update(Room room);

    @Delete
    void delete(Room room);

    @Query("SELECT * FROM rooms")
    List<Room> getAll();

    @Query("SELECT * FROM rooms WHERE room_id = :id")
    Room getById(int id);

    @Query("DELETE FROM rooms WHERE room_id = :id")
    void deleteById(int id);

    // ✅ جلب الغرف حسب النوع
    @Query("SELECT * FROM rooms WHERE room_type = :type")
    List<Room> getByType(String type);
}
