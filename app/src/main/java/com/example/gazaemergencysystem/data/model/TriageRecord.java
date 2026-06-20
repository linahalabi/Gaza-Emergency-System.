package com.example.gazaemergencysystem.data.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "triage_records",
        foreignKeys = {
                @ForeignKey(entity = Patient.class,
                        parentColumns = "id",
                        childColumns = "patient_id",
                        onDelete = ForeignKey.CASCADE),

                @ForeignKey(entity = MedicalStaff.class,
                        parentColumns = "staff_id",
                        childColumns = "doctor_id",
                        onDelete = ForeignKey.SET_NULL)
        },
        // ✅ إضافة indices للـ foreign keys
        indices = {
                @Index("patient_id"),
                @Index("doctor_id")
        })
public class TriageRecord {

    @PrimaryKey(autoGenerate = true)
    public int triage_id;

    public int patient_id;

    // ✅ Constants للألوان — يمنع أخطاء الكتابة
    public static final String COLOR_RED    = "Red";
    public static final String COLOR_YELLOW = "Yellow";
    public static final String COLOR_GREEN  = "Green";
    public static final String COLOR_BLACK  = "Black";

    public String status_color;

    // ✅ تغيير من String إلى long — أسرع وأدق
    public long update_time = System.currentTimeMillis();

    public Integer doctor_id; // null مسموح — الطبيب قد يكون محذوفاً

    // ✅ إضافة notes — مهم لتوثيق سبب تغيير الـ triage
    public String notes;

    // ✅ إضافة old_color — لتتبع تاريخ التغييرات
    public String old_color;
}
