package com.example.gazaemergencysystem.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.gazaemergencysystem.data.model.TriageRecord;
import java.util.List;
@Dao
public interface TriageRecordDao {

    @Insert
    long insert(TriageRecord record);

    @Update
    void update(TriageRecord record);

    @Delete
    void delete(TriageRecord record);

    // ✅ تاريخ الـ triage لمريض معين
    @Query("SELECT * FROM triage_records WHERE patient_id = :pId ORDER BY update_time DESC")
    List<TriageRecord> getHistory(int pId);

    // ✅ آخر triage للمريض
    @Query("SELECT * FROM triage_records WHERE patient_id = :pId ORDER BY update_time DESC LIMIT 1")
    TriageRecord getLatest(int pId);

    // ✅ للتقرير — عدد كل لون في يوم معين
    @Query("SELECT COUNT(*) FROM triage_records WHERE status_color = :color AND update_time BETWEEN :from AND :to")
    int countByColor(String color, long from, long to);
}
