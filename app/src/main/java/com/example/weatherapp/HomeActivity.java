// پکیج مربوط به برنامه آب و هوا
package com.example.weatherapp;

// ایمپورت‌های مورد نیاز برای اکتیویتی، ویوها، اینترنت، پیامک و ...
import androidx.appcompat.app.AppCompatActivity; // اکتیویتی پایه اندروید
import android.annotation.SuppressLint; // برای هشدارهای کامپایلر
import android.content.Intent; // برای رفتن به صفحات دیگر
import android.content.IntentFilter; // فیلتر اینتنت
import android.net.ConnectivityManager; // مدیریت اتصال شبکه
import android.os.Bundle; // مدیریت وضعیت اکتیویتی
import android.view.View; // مدیریت رویداد کلیک
import android.widget.Button; // دکمه
import android.widget.EditText; // ورودی متن
import android.widget.ImageView; // آیکون
import android.widget.TextView; // متن
import android.widget.Toast; // پیام کوتاه
import org.json.JSONException; // مدیریت خطاهای JSON
import org.json.JSONObject; // کار با داده‌های JSON
import okhttp3.OkHttpClient; // کلاینت HTTP
import okhttp3.Request; // درخواست HTTP
import okhttp3.Response; // پاسخ HTTP
import java.io.IOException; // مدیریت خطاهای ورودی/خروجی
import java.util.concurrent.ExecutorService; // اجرای غیرهمزمان
import java.util.concurrent.Executors; // ساخت thread pool
import android.Manifest; // مجوزها
import android.app.AlertDialog; // دیالوگ
import android.content.DialogInterface; // مدیریت دکمه‌های دیالوگ
import android.content.pm.PackageManager; // بررسی مجوزها
import android.telephony.SmsManager; // ارسال پیامک
import androidx.core.app.ActivityCompat; // درخواست مجوز
import androidx.core.content.ContextCompat; // بررسی مجوز

// اکتیویتی اصلی برنامه (نمایش آب و هوا)
public class HomeActivity extends AppCompatActivity {
    // متغیرهای نمایشی
    private TextView cityNameText, temperatureText, humidityText, descriptionText, windText;
    private ImageView weatherIcon;
    private EditText cityNameInput;
    private SystemReceiver internetReceiver; // دریافت‌کننده وضعیت اینترنت
    private static final String API_KEY = "93e2322d3103f8d17414b56c90b592fb"; // کلید API

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) { // متد اجرا هنگام ساخت اکتیویتی
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home); // ست کردن layout صفحه اصلی

        // مقداردهی ویوها
        cityNameText = findViewById(R.id.cityNameText);
        temperatureText = findViewById(R.id.temperatureText);
        humidityText = findViewById(R.id.humidityText);
        windText = findViewById(R.id.windText);
        descriptionText = findViewById(R.id.descriptionText);
        weatherIcon = findViewById(R.id.weatherIcon);
        Button refreshButton = findViewById(R.id.fetchWeatherButton);
        cityNameInput = findViewById(R.id.cityNameInput);
        Button aboutButton = findViewById(R.id.aboutButton);
        Button smsButton = findViewById(R.id.smsButton);
        Button sendWeatherSmsButton = findViewById(R.id.sendWeatherSmsButton);

        // رویداد کلیک روی دکمه جستجوی شهر
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cityName = cityNameInput.getText().toString(); // گرفتن نام شهر
                if (!cityName.isEmpty()) {
                    FetchWeatherData(cityName); // دریافت آب و هوا
                } else {
                    cityNameInput.setError("لطفاً نام شهر را وارد کنید"); // خطا اگر خالی باشد
                }
            }
        });

        // رویداد کلیک روی دکمه درباره برنامه
        aboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, AboutActivity.class); // رفتن به صفحه درباره
                startActivity(intent);
            }
        });

        // رویداد کلیک روی دکمه نمایش پیامک‌ها
        smsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, SmsViewerActivity.class); // رفتن به صفحه پیامک‌ها
                startActivity(intent);
            }
        });

        // رویداد کلیک روی دکمه ارسال آب‌وهوا با پیامک
        sendWeatherSmsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(HomeActivity.this, new String[]{Manifest.permission.SEND_SMS}, 2); // درخواست مجوز
                } else {
                    showSendSmsDialog(); // نمایش دیالوگ ارسال پیامک
                }
            }
        });

        FetchWeatherData("London"); // دریافت پیش‌فرض آب و هوای لندن

        // ثبت دریافت‌کننده اینترنت
        internetReceiver = new SystemReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(internetReceiver, filter);
    }

    @Override
    protected void onDestroy() { // متد اجرا هنگام نابودی اکتیویتی
        super.onDestroy();
        if (internetReceiver != null) {
            unregisterReceiver(internetReceiver); // لغو ثبت دریافت‌کننده
        }
    }

    // متد دریافت اطلاعات آب و هوا از API
    private void FetchWeatherData(String cityName) {
        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + cityName + "&appid=" + API_KEY + "&units=metric";
        ExecutorService executorService = Executors.newSingleThreadExecutor(); // اجرای غیرهمزمان
        executorService.execute(() -> {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).build();
            try {
                Response response = client.newCall(request).execute();
                String result = response.body().string();
                runOnUiThread(() -> updateUI(result)); // به‌روزرسانی رابط کاربری
            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(HomeActivity.this, "دریافت اطلاعات ناموفق بود. دوباره تلاش کنید.", Toast.LENGTH_SHORT).show());
            }
        });
    }

    // متد به‌روزرسانی رابط کاربری با داده‌های دریافتی
    @SuppressLint("DefaultLocale")
    private void updateUI(String result) {
        if (result != null) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONObject main = jsonObject.getJSONObject("main");
                double temperature = main.getDouble("temp");
                double humidity = main.getDouble("humidity");
                double windSpeed = jsonObject.getJSONObject("wind").getDouble("speed");

                String description = jsonObject.getJSONArray("weather").getJSONObject(0).getString("description");
                String iconCode = jsonObject.getJSONArray("weather").getJSONObject(0).getString("icon");
                String resourceName = "ic_" + iconCode;
                int resId = getResources().getIdentifier(resourceName, "drawable", getPackageName());
                weatherIcon.setImageResource(resId);

                cityNameText.setText(jsonObject.getString("name"));
                temperatureText.setText(String.format("%.0f°", temperature));
                humidityText.setText(String.format("%.0f%%", humidity));
                windText.setText(String.format("%.0f km/h", windSpeed));
                descriptionText.setText(description);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    // متد نمایش دیالوگ ارسال پیامک
    private void showSendSmsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("شماره گیرنده را وارد کنید");
        final EditText input = new EditText(this);
        input.setHint("مثلاً 09123456789");
        builder.setView(input);
        builder.setPositiveButton("ارسال", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String phoneNumber = input.getText().toString();
                String weatherInfo = getWeatherInfoForSms();
                try {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phoneNumber, null, weatherInfo, null, null);
                    Toast.makeText(HomeActivity.this, "پیامک ارسال شد!", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(HomeActivity.this, "خطا در ارسال پیامک: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
        builder.setNegativeButton("لغو", null);
        builder.show();
    }

    // متد ساخت متن پیامک آب و هوا
    private String getWeatherInfoForSms() {
        String city = cityNameText.getText().toString();
        String temp = temperatureText.getText().toString();
        String humidity = humidityText.getText().toString();
        String wind = windText.getText().toString();
        String desc = descriptionText.getText().toString();
        return "آب‌وهوای شهر " + city + ":\n" + desc + "\nدما: " + temp + "\nرطوبت: " + humidity + "\nباد: " + wind;
    }

    // متد مدیریت نتیجه درخواست مجوزها
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 2 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            showSendSmsDialog();
        }
    }
}
