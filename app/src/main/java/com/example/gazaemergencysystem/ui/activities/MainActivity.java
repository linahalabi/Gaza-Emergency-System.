package com.example.gazaemergencysystem.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.gazaemergencysystem.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // رسالة ترحيبية سريعة بنظام الطوارئ
        Toast.makeText(this, "مرحباً بك في نظام طوارئ غزة الرقمي", Toast.LENGTH_SHORT).show();

        // برمجة مؤقت (Handler) لينقل المستخدم تلقائياً لشاشة الـ Login بعد 2.5 ثانية
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // إغلاق هذه الشاشة حتى لا يعود إليها المستخدم عند الضغط على زر الرجوع
        }, 2500); // 2500 ميلي ثانية تعني ثانيتين ونصف
    }
}