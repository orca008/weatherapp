// پکیج مربوط به برنامه آب و هوا
package com.example.weatherapp;

// ایمپورت‌های مورد نیاز برای اکتیویتی و ویوها
import android.content.Intent; // برای رفتن به صفحه دیگر
import android.os.Bundle; // برای مدیریت وضعیت اکتیویتی
import android.view.View; // برای مدیریت رویداد کلیک
import android.widget.Button; // دکمه

import androidx.appcompat.app.AppCompatActivity; // اکتیویتی پایه اندروید

// کلاس اکتیویتی درباره برنامه
public class AboutActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) { // متد اجرا هنگام ساخت اکتیویتی
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about); // ست کردن layout صفحه درباره

        Button backToHomeButton = findViewById(R.id.backToHomeButton); // دکمه بازگشت به خانه
        // رویداد کلیک روی دکمه بازگشت
        backToHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AboutActivity.this, HomeActivity.class); // رفتن به صفحه اصلی
                startActivity(intent);
                finish(); // بستن صفحه درباره
            }
        });
    }
} 