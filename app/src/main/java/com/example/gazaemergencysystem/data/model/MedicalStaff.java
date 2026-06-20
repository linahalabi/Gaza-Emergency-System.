package com.example.gazaemergencysystem.data.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "medical_staff")
public class MedicalStaff {

    @PrimaryKey(autoGenerate = true)
    public int staff_id;

    public String name;

    // ✅ Constants للـ role — يمنع أخطاء الكتابة
    public static final String ROLE_DOCTOR    = "Doctor";
    public static final String ROLE_NURSE     = "Nurse";
    public static final String ROLE_ADMIN     = "Admin";
    public static final String ROLE_DATAENTRY = "DataEntry";

    public String role;
    public String username;

    // ✅ لا تخزن الباسورد نص عادي — خزّن الـ hash فقط
    // استخدم: BCrypt أو MessageDigest (SHA-256) قبل الحفظ
    @ColumnInfo(name = "password_hash")
    public String passwordHash;
}