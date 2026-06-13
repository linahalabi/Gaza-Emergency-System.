package com.example.gazaemergencysystem.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.gazaemergencysystem.data.model.Patient; // Import للجدول
import java.util.List;

@Dao
public interface PatientDao {

    @Insert
    long insert(Patient patient);

    @Update
    void update(Patient patient);

    @Delete
    void delete(Patient patient);

    @Query("SELECT * FROM patients ORDER BY arrival_time DESC")
    List<Patient> getAll();

    @Query("SELECT * FROM patients WHERE id = :id")
    Patient getById(int id);

    @Query("SELECT * FROM patients WHERE name LIKE '%' || :name || '%'")
    List<Patient> searchByName(String name);

    @Query("DELETE FROM patients WHERE id = :id")
    void deleteById(int id);

    // ✅ جلب المرضى حسب الـ triage — مهم لمشروعك
    @Query("SELECT * FROM patients WHERE triage_category = :category AND current_status = 'Active'")
    List<Patient> getByTriage(String category);

    @Query("SELECT * FROM patients WHERE current_status = 'Deceased'")
    List<Patient> getAllDeceased();

    // ✅ قائمة الانتظار — المرضى بدون سرير
    @Query("SELECT * FROM patients WHERE bed_id IS NULL AND current_status = 'Active'")
    List<Patient> getWaitingList();

    // ✅ للتقرير اليومي
    @Query("SELECT COUNT(*) FROM patients WHERE arrival_time BETWEEN :from AND :to")
    int countNewPatients(long from, long to);

    @Query("SELECT COUNT(*) FROM patients WHERE current_status = 'Active'")
    int countActive();

    @Query("SELECT COUNT(*) FROM patients WHERE current_status = 'Deceased' AND arrival_time BETWEEN :from AND :to")
    int countDeceased(long from, long to);

    // ✅ للـ offline sync
    @Query("SELECT * FROM patients WHERE is_synced = 0")
    List<Patient> getUnsynced();

    @Query("UPDATE patients SET is_synced = 1 WHERE id = :id")
    void markSynced(int id);

    @Query("DELETE FROM patients")
    void deleteAll();
}
