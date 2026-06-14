package com.example.gazaemergencysystem.data.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "beds",
        foreignKeys = {
                @ForeignKey(entity = Room.class,
                        parentColumns = "room_id",
                        childColumns = "room_id",
                        onDelete = ForeignKey.CASCADE),

                @ForeignKey(entity = Patient.class,
                        parentColumns = "id",
                        childColumns = "patient_id",
                        onDelete = ForeignKey.SET_NULL)
        },
        // ✅ إضافة indices للـ foreign keys — ضرورية لـ Room وإلا warning
        indices = {
                @Index("room_id"),
                @Index("patient_id")
        })
public class Bed {

    @PrimaryKey(autoGenerate = true)
    public int bed_id;

    public int room_id;

    // ✅ تغيير من int إلى boolean — أوضح وأصح
    public boolean is_occupied = false;

    public Integer patient_id; // null = السرير فارغ

    // ✅ إضافة bed_number لتمييز الأسرة داخل الغرفة (A1, A2, ...)
    @ColumnInfo(name = "bed_number")
    public String bedNumber;
}
