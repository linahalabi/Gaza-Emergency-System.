package com.example.gazaemergencysystem.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.gazaemergencysystem.R;
import com.example.gazaemergencysystem.data.AppDatabase;
import com.example.gazaemergencysystem.data.model.Bed;
import com.example.gazaemergencysystem.data.model.Patient;
import com.example.gazaemergencysystem.data.model.Room;
import com.example.gazaemergencysystem.ui.adapters.BedAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class BedManagementActivity extends AppCompatActivity {

    private GridView gvBeds;
    private TextView tvAvailable, tvOccupied;
    private AppDatabase db;
    private final List<Bed> bedList = new ArrayList<>();
    private BedAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bed_management);

        db = AppDatabase.getInstance(this);
        gvBeds = findViewById(R.id.gv_beds);
        tvAvailable = findViewById(R.id.tv_available_count);
        tvOccupied = findViewById(R.id.tv_occupied_count);
        
        setupBottomNavigation();

        if (gvBeds != null) {
            gvBeds.setOnItemClickListener((parent, view, position, id) -> {
                Bed selectedBed = bedList.get(position);
                handleBedClick(selectedBed);
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadBeds(); // تحديث البيانات في كل مرة تظهر فيها الشاشة
    }

    private void setupBottomNavigation() {
        com.google.android.material.bottomnavigation.BottomNavigationView nav = findViewById(R.id.bottom_navigation);
        nav.setSelectedItemId(R.id.nav_beds);
        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_triage) {
                Intent intent = new Intent(this, RegistrationActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                return true;
            } else if (id == R.id.nav_reports) {
                Intent intent = new Intent(this, DashboardActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                return true;
            }
            return id == R.id.nav_beds;
        });
    }

    private void loadBeds() {
        Executors.newSingleThreadExecutor().execute(() -> {
            // Check if Room 1 exists, create if not (Foreign Key requirement)
            if (db.roomDao().getAll().isEmpty()) {
                Room room = new Room();
                room.room_id = 1;
                room.room_number = "ER-01";
                room.room_type = Room.TYPE_EMERGENCY;
                db.roomDao().insert(room);
            }

            // 1. Fetch beds from Database
            List<Bed> bedsFromDb = db.bedDao().getByRoom(1); 
            int availableCount = db.bedDao().countAvailable();
            int occupiedCount = db.bedDao().countOccupied();
            
            // If no beds exist, initialize some for the demo
            if (bedsFromDb.isEmpty()) {
                for (int i = 1; i <= 20; i++) {
                    Bed newBed = new Bed();
                    newBed.room_id = 1;
                    newBed.bedNumber = "B" + i;
                    newBed.is_occupied = false;
                    db.bedDao().insert(newBed);
                }
                bedsFromDb = db.bedDao().getByRoom(1);
                availableCount = 20;
                occupiedCount = 0;
            }

            final List<Bed> finalBeds = bedsFromDb;
            final int finalAvailable = availableCount;
            final int finalOccupied = occupiedCount;

            runOnUiThread(() -> {
                if (tvAvailable != null) tvAvailable.setText(getString(R.string.available_count_format, finalAvailable));
                if (tvOccupied != null) tvOccupied.setText(getString(R.string.occupied_count_format, finalOccupied));

                bedList.clear();
                bedList.addAll(finalBeds);
                if (adapter == null) {
                    adapter = new BedAdapter(this, bedList);
                    gvBeds.setAdapter(adapter);
                } else {
                    adapter.notifyDataSetChanged();
                }
            });
        });
    }

    private void handleBedClick(Bed bed) {
        Executors.newSingleThreadExecutor().execute(() -> {
            if (!bed.is_occupied) {
                // 2. Allocate to an empty bed (Linking to the most recent waiting patient)
                List<Patient> waitingPatients = db.patientDao().getWaitingList();
                if (!waitingPatients.isEmpty()) {
                    Patient patient = waitingPatients.get(0);
                    
                    // Update Bed status
                    bed.is_occupied = true;
                    bed.patient_id = patient.id;
                    db.bedDao().update(bed);

                    // Update Patient record
                    patient.bed_id = bed.bed_id;
                    db.patientDao().update(patient);

                    runOnUiThread(() -> {
                        Toast.makeText(this, "Bed " + bed.bedNumber + " assigned to " + patient.name, Toast.LENGTH_SHORT).show();
                        loadBeds(); // 3. Refresh UI
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(BedManagementActivity.this, "No patients in waiting list", Toast.LENGTH_SHORT).show());
                }
            } else {
                // 4. If occupied, show Patient Details or Discharge
                int patientId = bed.patient_id;
                
                runOnUiThread(() -> {
                    new android.app.AlertDialog.Builder(this)
                        .setTitle("Bed " + bed.bedNumber)
                        .setMessage("Bed is occupied. What would you like to do?")
                        .setPositiveButton("View Details", (dialog, which) -> {
                            Intent intent = new Intent(this, PatientDetailsActivity.class);
                            intent.putExtra("patient_id", patientId);
                            startActivity(intent);
                        })
                        .setNegativeButton("Discharge Patient", (dialog, which) -> Executors.newSingleThreadExecutor().execute(() -> {
                            db.bedDao().freeBed(bed.bed_id);
                            runOnUiThread(() -> {
                                Toast.makeText(this, "Patient discharged", Toast.LENGTH_SHORT).show();
                                loadBeds();
                            });
                        }))
                        .setNeutralButton("Cancel", null)
                        .show();
                });
            }
        });
    }
}
