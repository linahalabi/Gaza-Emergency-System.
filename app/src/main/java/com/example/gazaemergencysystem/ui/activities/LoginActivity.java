package com.example.gazaemergencysystem.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.gazaemergencysystem.R;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        View loginBtn = findViewById(R.id.btn_login);
        if (loginBtn != null) {
            loginBtn.setOnClickListener(v -> {
                Toast.makeText(LoginActivity.this, "تم تسجيل الدخول بنجاح!", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish();
            });
        }
    }
}