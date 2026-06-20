package com.example.gazaemergencysystem.data.model;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "patients")
public class Patient {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(defaultValue = "Unknown")
    public String name = "Unknown";

    public int age;
    public String gender;
    public String injury_description;

    // ✅ تم التغيير من String إلى long — أسرع في الفرز والمقارنة
    public long arrival_time = System.currentTimeMillis();

    // ✅ إضافة triage_category — أساسي لمشروعك
    public String triage_category; // "Red" / "Yellow" / "Green" / "Black"

    // ✅ إضافة bed_id — لمعرفة إذا المريض معين له سرير
    public Integer bed_id; // null = في قائمة الانتظار

    // ✅ إضافة is_synced — ضروري للـ offline sync
    public boolean is_synced = false;

    // ✅ Constants بدل String عشوائي — يمنع أخطاء الكتابة
    public static final String STATUS_ACTIVE     = "Active";
    public static final String STATUS_DISCHARGED = "Discharged";
    public static final String STATUS_DECEASED   = "Deceased";

    @ColumnInfo(defaultValue = "Active")
    public String current_status = STATUS_ACTIVE;
}
