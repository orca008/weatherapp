// پکیج مربوط به برنامه آب و هوا
package com.example.weatherapp;

// ایمپورت‌های مورد نیاز برای کار با پیامک، اکتیویتی و ویوها
import android.Manifest; // مجوز پیامک
import android.content.Intent; // برای رفتن به صفحه دیگر
import android.content.pm.PackageManager; // بررسی مجوزها
import android.database.Cursor; // کار با دیتابیس پیامک
import android.net.Uri; // آدرس‌دهی به پیامک‌ها
import android.os.Bundle; // مدیریت وضعیت اکتیویتی
import android.view.View; // مدیریت رویداد کلیک
import android.widget.Button; // دکمه
import android.widget.LinearLayout; // لایه خطی
import android.widget.TextView; // متن
import android.widget.Toast; // پیام کوتاه

import androidx.appcompat.app.AppCompatActivity; // اکتیویتی پایه اندروید
import androidx.core.app.ActivityCompat; // درخواست مجوز
import androidx.core.content.ContextCompat; // بررسی مجوز

// اکتیویتی نمایش پیامک‌ها
public class SmsViewerActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) { // متد اجرا هنگام ساخت اکتیویتی
        super.onCreate(savedInstanceState);
        LinearLayout layout = new LinearLayout(this); // ساخت لایه خطی عمودی
        layout.setOrientation(LinearLayout.VERTICAL); // چیدمان عمودی
        layout.setPadding(32, 32, 32, 32); // فاصله داخلی

        TextView textView = new TextView(this); // ساخت متن برای نمایش پیامک
        textView.setTextSize(18); // اندازه فونت
        layout.addView(textView); // افزودن به لایه

        Button backButton = new Button(this); // ساخت دکمه بازگشت
        backButton.setText("برگشت به خانه"); // متن دکمه
        layout.addView(backButton); // افزودن دکمه به لایه

        setContentView(layout); // نمایش لایه ساخته شده

        // رویداد کلیک روی دکمه بازگشت
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SmsViewerActivity.this, HomeActivity.class); // رفتن به صفحه اصلی
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP); // تنظیم فلگ‌ها
                startActivity(intent);
                finish(); // بستن صفحه پیامک
            }
        });

        // بررسی مجوز خواندن پیامک
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS}, 1); // درخواست مجوز
        } else {
            showLastSms(textView); // نمایش آخرین پیامک
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            showLastSms((TextView) ((LinearLayout) findViewById(android.R.id.content).getRootView()).getChildAt(0)); // اگر مجوز داده شد، پیامک را نمایش بده
        } else {
            Toast.makeText(this, "دسترسی رد شد", Toast.LENGTH_SHORT).show(); // اگر مجوز رد شد
        }
    }

    // متد نمایش آخرین پیامک دریافتی
    private void showLastSms(TextView textView) {
        try {
            Uri uriSms = Uri.parse("content://sms/inbox"); // آدرس پیامک‌های دریافتی
            Cursor cursor = getContentResolver().query(uriSms, null, null, null, "date DESC"); // گرفتن پیامک‌ها به ترتیب جدیدترین
            if (cursor != null && cursor.moveToFirst()) {
                String address = cursor.getString(cursor.getColumnIndexOrThrow("address")); // شماره فرستنده
                String body = cursor.getString(cursor.getColumnIndexOrThrow("body")); // متن پیامک
                textView.setText("آخرین پیامک:\nاز: " + address + "\nمتن: " + body); // نمایش پیامک
                cursor.close();
            } else {
                textView.setText("پیامکی پیدا نشد."); // اگر پیامکی نبود
            }
        } catch (Exception e) {
            textView.setText("خطا در خواندن پیامک: " + e.getMessage()); // خطا در خواندن پیامک
        }
    }
} 