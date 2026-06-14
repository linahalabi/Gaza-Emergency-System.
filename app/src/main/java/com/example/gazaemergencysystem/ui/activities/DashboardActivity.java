package com.example.gazaemergencysystem.ui.activities;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.gazaemergencysystem.R;
import com.example.gazaemergencysystem.data.AppDatabase;
import com.example.gazaemergencysystem.data.utils.DataSeeder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executors;

public class DashboardActivity extends AppCompatActivity {

    private TextView tvTotal, tvUrgent, tvOccupancy, tvDeceased;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        db = AppDatabase.getInstance(this);
        
        // 1. Seed dummy data for testing
        DataSeeder.seedDummyData(this);

        // Initialize UI
        tvTotal = findViewById(R.id.tv_total_patients);
        tvUrgent = findViewById(R.id.tv_count_urgent);
        tvOccupancy = findViewById(R.id.tv_bed_occupancy);
        tvDeceased = findViewById(R.id.tv_count_deceased);

        findViewById(R.id.btn_seed_data).setOnClickListener(v -> {
            DataSeeder.seedDummyData(this);
            updateStatistics();
        });

        // 🛡️ حذف كل السجلات (تنظيف السجل القديم) عند الضغط المطول
        findViewById(R.id.btn_seed_data).setOnLongClickListener(v -> {
            Executors.newSingleThreadExecutor().execute(() -> {
                db.patientDao().deleteAll(); // حذف كل المرضى
                db.bedDao().resetAllBeds(); // تفريغ كل الأسرة
                runOnUiThread(() -> {
                    Toast.makeText(this, "تم تنظيف كافة السجلات القديمة بنجاح", Toast.LENGTH_LONG).show();
                    updateStatistics();
                });
            });
            return true;
        });

        // 🚀 PDF Export Logic
        findViewById(R.id.btn_export_pdf).setOnClickListener(v -> generateDailyReportPDF());

        // 🚀 CSV Export Logic
        findViewById(R.id.btn_export_csv).setOnClickListener(v -> generatePatientsCSV());
        
        // Logout logic
        View btnLogout = findViewById(R.id.btn_logout);
        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            });
        }

        setupNavigation();
        setupBottomNavigation();
    }

    private void generatePatientsCSV() {
        Executors.newSingleThreadExecutor().execute(() -> {
            java.util.List<com.example.gazaemergencysystem.data.model.Patient> patients = db.patientDao().getAll();
            File file = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "PatientsList.csv");
            
            try (java.io.FileOutputStream fos = new java.io.FileOutputStream(file);
                 java.io.OutputStreamWriter osw = new java.io.OutputStreamWriter(fos, java.nio.charset.StandardCharsets.UTF_8)) {
                
                // BOM لدعم العربية في Excel
                fos.write(0xef); fos.write(0xbb); fos.write(0xbf);

                osw.append("المعرف,الاسم,العمر,الجنس,درجة الخطورة,الحالة الحالية\n");
                for (com.example.gazaemergencysystem.data.model.Patient p : patients) {
                    osw.append(String.valueOf(p.id)).append(",")
                          .append("\"").append(p.name).append("\",")
                          .append(String.valueOf(p.age)).append(",")
                          .append("\"").append(p.gender != null ? p.gender : "غير محدد").append("\",")
                          .append("\"").append(translateTriage(p.triage_category)).append("\",")
                          .append("\"").append(p.current_status).append("\"\n");
                }
                
                runOnUiThread(() -> {
                    Toast.makeText(this, "تم تصدير الكشف بنجاح", Toast.LENGTH_SHORT).show();
                    openGeneratedFile(file, "text/csv");
                });
            } catch (IOException e) {
                runOnUiThread(() -> Toast.makeText(this, "خطأ: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show());
            }
        });
    }

    private String translateTriage(String triage) {
        if (triage == null) return "غير محدد";
        switch (triage) {
            case "Red": return "فوري (أحمر)";
            case "Yellow": return "مؤجل (أصفر)";
            case "Green": return "طفيف (أخضر)";
            case "Black": return "وفاة (أسود)";
            default: return triage;
        }
    }

    private void openGeneratedFile(File file, String mimeType) {
        Uri path = FileProvider.getUriForFile(this, getPackageName() + ".provider", file);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(path, mimeType);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "No compatible app found to open this file", Toast.LENGTH_SHORT).show();
        }
    }

    private void generateDailyReportPDF() {
        PdfDocument pdfDocument = new PdfDocument();
        Paint paint = new Paint();
        Paint titlePaint = new Paint();

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        // Title
        titlePaint.setTextSize(24);
        titlePaint.setFakeBoldText(true);
        titlePaint.setColor(Color.BLACK);
        canvas.drawText("FieldClinic MS - Daily Report", 50, 50, titlePaint);

        // Date
        paint.setTextSize(14);
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());
        canvas.drawText("Generated on: " + date, 50, 80, paint);

        // Stats from UI/DB
        paint.setTextSize(16);
        paint.setFakeBoldText(true);
        canvas.drawText("Clinic Statistics Overview:", 50, 130, paint);

        paint.setFakeBoldText(false);
        canvas.drawText("- Total Registered Patients: " + tvTotal.getText(), 70, 160, paint);
        canvas.drawText("- Critical (Red) Cases: " + tvUrgent.getText(), 70, 190, paint);
        canvas.drawText("- Bed Occupancy Rate: " + tvOccupancy.getText(), 70, 220, paint);
        canvas.drawText("- Deceased Recorded: " + tvDeceased.getText(), 70, 250, paint);

        // Separator
        canvas.drawLine(50, 280, 545, 280, paint);
        
        paint.setTextSize(12);
        paint.setColor(Color.GRAY);
        canvas.drawText("End of Official Crisis Response Report", 200, 310, paint);

        pdfDocument.finishPage(page);

        // Save File
        File file = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "DailyReport.pdf");
        try (FileOutputStream fos = new FileOutputStream(file)) {
            pdfDocument.writeTo(fos);
            Toast.makeText(this, "PDF Generated Successfully", Toast.LENGTH_SHORT).show();
            openGeneratedFile(file, "application/pdf");
        } catch (IOException e) {
            Toast.makeText(this, "Error: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        } finally {
            pdfDocument.close();
        }
    }

    private void setupBottomNavigation() {
        com.google.android.material.bottomnavigation.BottomNavigationView nav = findViewById(R.id.bottom_navigation);
        nav.setSelectedItemId(R.id.nav_reports);
        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_triage) {
                Intent intent = new Intent(this, RegistrationActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                return true;
            } else if (id == R.id.nav_beds) {
                Intent intent = new Intent(this, BedManagementActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                return true;
            }
            return id == R.id.nav_reports;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateStatistics();
    }

    private void updateStatistics() {
        Executors.newSingleThreadExecutor().execute(() -> {
            int total = db.patientDao().getAll().size();
            int urgent = db.patientDao().getByTriage("Red").size();
            int deceased = db.patientDao().getAllDeceased().size();
            
            int totalBeds = db.bedDao().countAvailable() + db.bedDao().countOccupied();
            int occupiedBeds = db.bedDao().countOccupied();
            int occupancyPercent = totalBeds > 0 ? (occupiedBeds * 100 / totalBeds) : 0;
            String occupancyStr = occupancyPercent + "% (" + occupiedBeds + "/" + totalBeds + ")";

            runOnUiThread(() -> {
                if (tvTotal != null) tvTotal.setText(String.valueOf(total));
                if (tvUrgent != null) tvUrgent.setText(String.valueOf(urgent));
                if (tvOccupancy != null) tvOccupancy.setText(occupancyStr);
                if (tvDeceased != null) tvDeceased.setText(String.valueOf(deceased));
            });
        });
    }

    private void setupNavigation() {
        // Quick access to Bed Management by clicking any card
        if (tvTotal != null) tvTotal.setOnClickListener(v -> startActivity(new Intent(this, BedManagementActivity.class)));
        
        getWindow().getDecorView().setOnLongClickListener(v -> {
            startActivity(new Intent(this, RegistrationActivity.class));
            return true;
        });
    }
}
