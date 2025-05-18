// پکیج مربوط به برنامه آب و هوا
package com.example.weatherapp;

// ایمپورت‌های مورد نیاز برای کار با اکتیویتی، ویوها و توست
import android.content.Intent; // برای رفتن به صفحه دیگر
import android.os.Bundle; // برای مدیریت وضعیت اکتیویتی
import android.text.TextUtils; // برای بررسی خالی بودن رشته‌ها
import android.view.View; // برای مدیریت رویداد کلیک
import android.widget.Button; // دکمه
import android.widget.EditText; // ورودی متن
import android.widget.Toast; // نمایش پیام کوتاه

import androidx.appcompat.app.AppCompatActivity; // اکتیویتی پایه اندروید

// کلاس اکتیویتی ورود
public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) { // متد اجرا هنگام ساخت اکتیویتی
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // ست کردن layout صفحه ورود

        EditText usernameInput = findViewById(R.id.usernameInput); // ورودی نام کاربری
        EditText passwordInput = findViewById(R.id.passwordInput); // ورودی رمز عبور
        Button loginButton = findViewById(R.id.loginButton); // دکمه ورود

        // رویداد کلیک روی دکمه ورود
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameInput.getText().toString(); // گرفتن مقدار نام کاربری
                String password = passwordInput.getText().toString(); // گرفتن مقدار رمز عبور
                // اگر یکی از فیلدها خالی بود پیام خطا بده
                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                    Toast.makeText(LoginActivity.this, "لطفاً نام کاربری و رمز عبور را وارد کنید", Toast.LENGTH_SHORT).show();
                } else if (username.equals("admin") && password.equals("admin")) { // اگر اطلاعات درست بود
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class); // رفتن به صفحه اصلی
                    startActivity(intent);
                    finish(); // بستن صفحه ورود
                } else {
                    Toast.makeText(LoginActivity.this, "نام کاربری یا رمز عبور اشتباه است", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
