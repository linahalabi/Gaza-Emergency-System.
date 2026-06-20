package com.example.gazaemergencysystem.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.gazaemergencysystem.data.model.Bed;
import java.util.List;
@Dao
public interface BedDao {

    @Insert
    long insert(Bed bed);

    @Update
    void update(Bed bed);

    @Delete
    void delete(Bed bed);

    @Query("SELECT * FROM beds WHERE room_id = :roomId")
    List<Bed> getByRoom(int roomId);

    // ✅ تغيير من 0 إلى false — لأن غيرنا is_occupied لـ boolean
    @Query("SELECT * FROM beds WHERE is_occupied = 0")
    List<Bed> getAvailable();

    @Query("SELECT * FROM beds WHERE bed_id = :bedId")
    Bed getById(int bedId);

    // ✅ تعيين سرير لمريض
    @Query("UPDATE beds SET is_occupied = 1, patient_id = :patientId WHERE bed_id = :bedId")
    void assignBed(int bedId, int patientId);

    // ✅ تفريغ السرير
    @Query("UPDATE beds SET is_occupied = 0, patient_id = NULL WHERE bed_id = :bedId")
    void freeBed(int bedId);

    // ✅ عدد الأسرة الفارغة — للتقرير اليومي
    @Query("SELECT COUNT(*) FROM beds WHERE is_occupied = 0")
    int countAvailable();

    @Query("SELECT COUNT(*) FROM beds WHERE is_occupied = 1")
    int countOccupied();

    @Query("UPDATE beds SET is_occupied = 0, patient_id = NULL")
    void resetAllBeds();
}
