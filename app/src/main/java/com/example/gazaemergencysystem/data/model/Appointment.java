package com.example.gazaemergencysystem.data.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "appointments",
        foreignKeys = {
                @ForeignKey(entity = Patient.class,
                        parentColumns = "id",
                        childColumns = "patient_id",
                        onDelete = ForeignKey.CASCADE),

                @ForeignKey(entity = MedicalStaff.class,
                        parentColumns = "staff_id",
                        childColumns = "staff_id",
                        // ✅ تغيير من CASCADE إلى SET_NULL — لا تحذف المواعيد إذا حذف الطاقم
                        onDelete = ForeignKey.SET_NULL)
        },
        // ✅ إضافة indices للـ foreign keys
        indices = {
                @Index("patient_id"),
                @Index("staff_id")
        })
public class Appointment {

    @PrimaryKey(autoGenerate = true)
    public int appointment_id;

    public int patient_id;
    public Integer staff_id; // ✅ Integer بدل int — يسمح بـ null بعد SET_NULL

    // ✅ تغيير من String إلى long — أسهل للمقارنة والفرز
    public long appointment_date;

    public String reason;

    // ✅ Constants للـ status
    public static final String STATUS_SCHEDULED  = "Scheduled";
    public static final String STATUS_COMPLETED  = "Completed";
    public static final String STATUS_CANCELLED  = "Cancelled";

    @ColumnInfo(defaultValue = "Scheduled")
    public String status = STATUS_SCHEDULED;

    // ✅ إضافة notes — لأي ملاحظات إضافية
    public String notes;
}
