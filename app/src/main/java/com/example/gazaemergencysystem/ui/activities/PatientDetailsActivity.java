package com.example.gazaemergencysystem.ui.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.gazaemergencysystem.R;
import com.example.gazaemergencysystem.data.AppDatabase;
import com.example.gazaemergencysystem.data.model.Patient;

import java.util.concurrent.Executors;

public class PatientDetailsActivity extends AppCompatActivity {

    private TextView tvName, tvStatus;
    private EditText etDiagnosis;
    private AppDatabase db;
    private int patientId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_details);

        db = AppDatabase.getInstance(this);

        // 1. Get patientId from Intent
        patientId = getIntent().getIntExtra("patient_id", -1);

        // Initialize UI
        tvName = findViewById(R.id.tv_detail_name);
        tvStatus = findViewById(R.id.tv_detail_status);
        etDiagnosis = findViewById(R.id.et_diagnosis);

        if (patientId != -1) {
            loadPatientData();
        } else {
            Toast.makeText(this, "Error: Patient not found", Toast.LENGTH_SHORT).show();
            finish();
        }

        Button btnUpdate = findViewById(R.id.btn_update_file);
        if (btnUpdate != null) {
            btnUpdate.setOnClickListener(v -> updatePatientDiagnosis());
        }
    }

    private void loadPatientData() {
        // 2. Fetch specific patient from database in background
        Executors.newSingleThreadExecutor().execute(() -> {
            Patient patient = db.patientDao().getById(patientId);
            
            if (patient != null) {
                runOnUiThread(() -> {
                    // 3. Bind retrieved data to TextViews
                    tvName.setText(getString(R.string.patient_name_prefix, patient.name));
                    tvStatus.setText(getString(R.string.patient_status_prefix, patient.triage_category));
                    
                    // Set color based on triage
                    if ("Red".equalsIgnoreCase(patient.triage_category)) {
                        tvStatus.setTextColor(ContextCompat.getColor(this, R.color.triage_red));
                    } else if ("Yellow".equalsIgnoreCase(patient.triage_category)) {
                        tvStatus.setTextColor(ContextCompat.getColor(this, R.color.triage_yellow));
                    } else {
                        tvStatus.setTextColor(ContextCompat.getColor(this, R.color.triage_green));
                    }

                    if (patient.injury_description != null) {
                        etDiagnosis.setText(patient.injury_description);
                    }
                });
            }
        });
    }

    private void updatePatientDiagnosis() {
        String diagnosis = etDiagnosis.getText().toString().trim();
        if (diagnosis.isEmpty()) return;

        Executors.newSingleThreadExecutor().execute(() -> {
            Patient patient = db.patientDao().getById(patientId);
            if (patient != null) {
                patient.injury_description = diagnosis;
                db.patientDao().update(patient);
                runOnUiThread(() -> {
                    Toast.makeText(this, "Medical File Updated", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
        });
    }
}
