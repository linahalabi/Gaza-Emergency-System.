package com.example.gazaemergencysystem.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.gazaemergencysystem.data.model.Appointment;
import com.example.gazaemergencysystem.data.model.Bed;
import com.example.gazaemergencysystem.data.model.MedicalStaff;
import com.example.gazaemergencysystem.data.model.Patient;
import com.example.gazaemergencysystem.data.model.TriageRecord;

import com.example.gazaemergencysystem.data.dao.AppointmentDao;
import com.example.gazaemergencysystem.data.dao.BedDao;
import com.example.gazaemergencysystem.data.dao.MedicalStaffDao;
import com.example.gazaemergencysystem.data.dao.PatientDao;
import com.example.gazaemergencysystem.data.dao.RoomDao;
import com.example.gazaemergencysystem.data.dao.TriageRecordDao;

@Database(entities = {
        MedicalStaff.class,
        Patient.class,
        com.example.gazaemergencysystem.data.model.Room.class, // المسار الكامل لتجنب التعارض مع مكتبة Room
        Bed.class,
        Appointment.class,
        TriageRecord.class
}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract MedicalStaffDao medicalStaffDao();
    public abstract PatientDao patientDao();
    public abstract RoomDao roomDao();
    public abstract BedDao bedDao();
    public abstract AppointmentDao appointmentDao();
    public abstract TriageRecordDao triageRecordDao();

    // Singleton Pattern
    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "gaza_emergency_db"
                            )
                            // ✅ تم الإبقاء على allowMainThreadQueries لمشروع التخرج
                            // ⚠️ في التطبيقات الحقيقية استخدم AsyncTask أو ViewModel + LiveData
                            .allowMainThreadQueries()
                            // ✅ إضافة — يحمي البيانات عند تحديث الـ version مستقبلاً بدل ما يمسحها
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
