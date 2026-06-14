package com.example.gazaemergencysystem.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.gazaemergencysystem.data.model.Appointment;
import java.util.List;
@Dao
public interface AppointmentDao {

    @Insert
    long insert(Appointment app);

    @Update
    void update(Appointment app);

    @Delete
    void delete(Appointment app);

    @Query("SELECT * FROM appointments WHERE patient_id = :pId ORDER BY appointment_date ASC")
    List<Appointment> getByPatient(int pId);

    @Query("DELETE FROM appointments WHERE appointment_id = :id")
    void deleteById(int id);

    // ✅ المواعيد القادمة في نطاق زمني معين
    @Query("SELECT * FROM appointments WHERE appointment_date BETWEEN :from AND :to AND status = 'Scheduled'")
    List<Appointment> getUpcoming(long from, long to);

    // ✅ تحديث حالة الموعد بدون جلب الكائن كامل
    @Query("UPDATE appointments SET status = 'Completed' WHERE appointment_id = :id")
    void markCompleted(int id);

    @Query("UPDATE appointments SET status = 'Cancelled' WHERE appointment_id = :id")
    void markCancelled(int id);
}
