package com.example.gazaemergencysystem.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.gazaemergencysystem.data.model.MedicalStaff; // Import entity

import java.util.List;

@Dao
public interface MedicalStaffDao {

    @Insert
    long insert(MedicalStaff staff);

    @Update
    void update(MedicalStaff staff);

    @Delete
    void delete(MedicalStaff staff);

    @Query("SELECT * FROM medical_staff")
    List<MedicalStaff> getAll();

    @Query("SELECT * FROM medical_staff WHERE staff_id = :id")
    MedicalStaff getById(int id);

    // ✅ تغيير login — لا تقارن الباسورد نص عادي في الـ Query
    // المقارنة تصير في الكود بعد جلب الـ staff ومقارنة الـ hash
    @Query("SELECT * FROM medical_staff WHERE username = :username LIMIT 1")
    MedicalStaff getByUsername(String username);

    // ✅ جلب الكادر حسب الدور
    @Query("SELECT * FROM medical_staff WHERE role = :role")
    List<MedicalStaff> getByRole(String role);
}