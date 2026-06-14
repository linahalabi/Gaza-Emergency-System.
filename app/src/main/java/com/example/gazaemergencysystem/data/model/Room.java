package com.example.gazaemergencysystem.data.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

// ✅ indices لضمان أن room_number فريد
@Entity(tableName = "rooms",
        indices = {@Index(value = "room_number", unique = true)})
public class Room {

    @PrimaryKey(autoGenerate = true)
    public int room_id;

    public String room_number; // فريد — مضمون بالـ Index فوق

    // ✅ Constants للـ room_type
    public static final String TYPE_EMERGENCY = "Emergency";
    public static final String TYPE_ICU       = "ICU";
    public static final String TYPE_GENERAL   = "General";
    public static final String TYPE_OPERATING = "Operating";

    public String room_type;
    public int capacity;

    // ✅ إضافة حقل لمعرفة كم سرير مشغول حالياً (يُحدَّث برمجياً)
    @ColumnInfo(defaultValue = "0")
    public int occupied_count = 0;
}
