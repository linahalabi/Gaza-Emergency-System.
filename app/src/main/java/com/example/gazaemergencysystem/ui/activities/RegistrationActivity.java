package com.example.gazaemergencysystem.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.gazaemergencysystem.R;
import com.example.gazaemergencysystem.data.AppDatabase;
import com.example.gazaemergencysystem.data.model.Patient;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executors;

public class RegistrationActivity extends AppCompatActivity {

    private EditText etName, etAge, etInjury;
    private Spinner spinnerGender;
    private TextView tvPatientId, tvEntryTime;
    private com.google.android.material.card.MaterialCardView cardRed, cardYellow, cardGreen, cardBlack;
    private String selectedTriage = "Green"; 
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        db = AppDatabase.getInstance(this);

        // ربط العناصر
        tvPatientId = findViewById(R.id.tv_patient_id);
        tvEntryTime = findViewById(R.id.tv_entry_time);
        etName = findViewById(R.id.et_patient_name);
        etAge = findViewById(R.id.et_age);
        spinnerGender = findViewById(R.id.spinner_gender);
        etInjury = findViewById(R.id.et_injury_description);
        
        cardRed = findViewById(R.id.card_red);
        cardYellow = findViewById(R.id.card_yellow);
        cardGreen = findViewById(R.id.card_green);
        cardBlack = findViewById(R.id.card_black);

        setupTriageSelection();
        setupGenderSpinner();
        generatePatientInfo();
        setupBottomNavigation();

        // ربط الأزرار الجديدة
        findViewById(R.id.btn_save_bed).setOnClickListener(v -> savePatient(false));
        findViewById(R.id.btn_save_deceased).setOnClickListener(v -> savePatient(true));
    }

    private void setupTriageSelection() {
        cardRed.setOnClickListener(v -> selectTriage("Red", cardRed));
        cardYellow.setOnClickListener(v -> selectTriage("Yellow", cardYellow));
        cardGreen.setOnClickListener(v -> selectTriage("Green", cardGreen));
        cardBlack.setOnClickListener(v -> selectTriage("Black", cardBlack));
        selectTriage("Green", cardGreen);
    }

    private void selectTriage(String triage, com.google.android.material.card.MaterialCardView selectedCard) {
        selectedTriage = triage;

        com.google.android.material.card.MaterialCardView[] cards = {cardRed, cardYellow, cardGreen, cardBlack};
        for (com.google.android.material.card.MaterialCardView card : cards) {
            card.setStrokeWidth(0);
        }

        selectedCard.setStrokeWidth(6);
        int color;
        switch (triage) {
            case "Red": color = R.color.triage_red; break;
            case "Yellow": color = R.color.triage_yellow; break;
            case "Black": color = R.color.triage_black; break;
            default: color = R.color.triage_green; break;
        }
        selectedCard.setStrokeColor(ContextCompat.getColor(this, color));
    }

    private void savePatient(boolean isDeceasedAction) {
        String name = etName.getText().toString().trim();
        String ageStr = etAge.getText().toString().trim();
        String gender = spinnerGender.getSelectedItem().toString();
        String injury = etInjury.getText().toString().trim();

        if (name.isEmpty() && ageStr.isEmpty()) {
            Toast.makeText(this, "الرجاء إدخال بيانات المريض", Toast.LENGTH_SHORT).show();
            return;
        }

        // تحديد الحالة النهائية بناءً على الزر المضغوط أو اللون المختار
        final boolean finalIsDeceased = isDeceasedAction || "Black".equalsIgnoreCase(selectedTriage);

        Patient patient = new Patient();
        patient.name = name.isEmpty() ? "مجهول الهوية" : name;
        patient.age = ageStr.isEmpty() ? 0 : Integer.parseInt(ageStr);
        patient.gender = gender;
        patient.injury_description = injury;
        patient.triage_category = finalIsDeceased ? "Black" : selectedTriage;
        patient.arrival_time = System.currentTimeMillis();
        
        if (finalIsDeceased) {
            patient.current_status = Patient.STATUS_DECEASED;
        }

        Executors.newSingleThreadExecutor().execute(() -> {
            long pId = db.patientDao().insert(patient);
            String msg;
            Intent intent;

            if (finalIsDeceased) {
                msg = "تم تسجيل حالة الوفاة في السجل الرسمي بنجاح";
                intent = new Intent(RegistrationActivity.this, DashboardActivity.class);
            } else {
                java.util.List<com.example.gazaemergencysystem.data.model.Bed> beds = db.bedDao().getAvailable();
                if (!beds.isEmpty()) {
                    com.example.gazaemergencysystem.data.model.Bed bed = beds.get(0);
                    bed.is_occupied = true;
                    bed.patient_id = (int) pId;
                    db.bedDao().update(bed);
                    msg = "تم تسجيل المريض وحجز سرير رقم: " + bed.bedNumber;
                } else {
                    msg = "تم الحفظ في قائمة الانتظار (الأسرّة ممتلئة)";
                }
                intent = new Intent(RegistrationActivity.this, BedManagementActivity.class);
            }

            runOnUiThread(() -> {
                Toast.makeText(RegistrationActivity.this, msg, Toast.LENGTH_LONG).show();
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                finish();
            });
        });
    }

    private void setupGenderSpinner() {
        String[] genders = {"ذكر", "انثى"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, genders);
        spinnerGender.setAdapter(adapter);
    }

    private void generatePatientInfo() {
        int randomNumber = 1000 + (int)(Math.random() * 9000);
        tvPatientId.setText(getString(R.string.patient_id_format, randomNumber));
        tvEntryTime.setText(new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date()));
    }

    private void setupBottomNavigation() {
        com.google.android.material.bottomnavigation.BottomNavigationView nav = findViewById(R.id.bottom_navigation);
        nav.setSelectedItemId(R.id.nav_triage);
        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_triage) return true;
            
            Intent intent;
            if (id == R.id.nav_beds) intent = new Intent(this, BedManagementActivity.class);
            else if (id == R.id.nav_reports) intent = new Intent(this, DashboardActivity.class);
            else return false;

            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            return true;
        });
    }
}
