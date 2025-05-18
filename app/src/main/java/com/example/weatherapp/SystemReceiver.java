// پکیج مربوط به برنامه آب و هوا
package com.example.weatherapp;

// ایمپورت‌های مورد نیاز برای بررسی وضعیت اینترنت و نمایش پیام
import android.content.BroadcastReceiver; // دریافت‌کننده broadcast
import android.content.Context; // کانتکست برنامه
import android.content.Intent; // اینتنت
import android.net.ConnectivityManager; // مدیریت اتصال شبکه
import android.net.NetworkInfo; // اطلاعات شبکه
import android.widget.Toast; // پیام کوتاه

// کلاس دریافت‌کننده وضعیت سیستم (اینترنت)
public class SystemReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) { // متد اجرا هنگام دریافت broadcast
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE); // گرفتن سرویس اینترنت
        if (connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo(); // گرفتن اطلاعات شبکه فعال
            if (networkInfo != null && networkInfo.isConnected()) {
                Toast.makeText(context, "اینترنت متصل است", Toast.LENGTH_SHORT).show(); // اگر اینترنت وصل بود
            } else {
                Toast.makeText(context, "اتصال اینترنت برقرار نیست", Toast.LENGTH_SHORT).show(); // اگر اینترنت قطع بود
            }
        }
    }
}
