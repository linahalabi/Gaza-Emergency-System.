package com.example.gazaemergencysystem.data.utils;

import android.content.Context;
import com.example.gazaemergencysystem.data.AppDatabase;
import com.example.gazaemergencysystem.data.model.Bed;
import com.example.gazaemergencysystem.data.model.Patient;
import com.example.gazaemergencysystem.data.model.Room;
import java.util.concurrent.Executors;

public class DataSeeder {

    public static void seedDummyData(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);

        Executors.newSingleThreadExecutor().execute(() -> {
            // 1. Seed Rooms if empty
            if (db.roomDao().getAll().isEmpty()) {
                Room room1 = new Room();
                room1.room_id = 1;
                room1.room_number = "ER-A";
                room1.room_type = Room.TYPE_EMERGENCY;
                db.roomDao().insert(room1);

                Room room2 = new Room();
                room2.room_id = 2;
                room2.room_number = "ICU-01";
                room2.room_type = Room.TYPE_ICU;
                db.roomDao().insert(room2);
            }

            // 2. Seed Beds if empty
            if (db.bedDao().getByRoom(1).isEmpty()) {
                for (int i = 1; i <= 12; i++) {
                    Bed bed = new Bed();
                    bed.room_id = 1;
                    bed.bedNumber = "A-" + i;
                    bed.is_occupied = false;
                    db.bedDao().insert(bed);
                }
            }

            // 3. Seed some dummy Patients if empty
            if (db.patientDao().getAll().isEmpty()) {
                createPatient(db, "رائد أحمد محمد", 35, "ذكر", "إصابة شظايا في الصدر", "Red");
                createPatient(db, "منى يوسف علي", 28, "انثى", "كسر مضاعف في الساق", "Yellow");
                createPatient(db, "سارة إبراهيم", 12, "انثى", "حروق طفيفة", "Green");
                createPatient(db, "محمد كمال", 50, "ذكر", "ضيق تنفس حاد", "Red");
                createPatient(db, "مجهول الهوية", 0, "ذكر", "إصابة رأس خطيرة", "Black");
                createPatient(db, "فاطمة حسن", 65, "انثى", "جروح سطحية", "Green");
                createPatient(db, "ياسين علي", 42, "ذكر", "آلام في الظهر", "Yellow");
            }
        });
    }

    private static void createPatient(AppDatabase db, String name, int age, String gender, String injury, String triage) {
        Patient p = new Patient();
        p.name = name;
        p.age = age;
        p.gender = gender;
        p.injury_description = injury;
        p.triage_category = triage;
        p.arrival_time = System.currentTimeMillis();
        db.patientDao().insert(p);
    }
}
