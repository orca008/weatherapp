// پکیج مربوط به برنامه آب و هوا
package com.example.weatherapp;

// ایمپورت‌های مورد نیاز برای دریافت پیامک و نمایش پیام
import android.content.BroadcastReceiver; // دریافت‌کننده broadcast
import android.content.Context; // کانتکست برنامه
import android.content.Intent; // اینتنت
import android.os.Bundle; // باندل برای داده‌های اضافی
import android.telephony.SmsMessage; // پیامک
import android.widget.Toast; // پیام کوتاه

// کلاس دریافت‌کننده پیامک
public class SmsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) { // متد اجرا هنگام دریافت پیامک
        Bundle bundle = intent.getExtras(); // گرفتن داده‌های اضافی
        if (bundle != null) {
            Object[] pdus = (Object[]) bundle.get("pdus"); // گرفتن آرایه پیامک‌ها
            if (pdus != null) {
                for (Object pdu : pdus) { // پیمایش همه پیامک‌ها
                    SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdu); // ساخت شی پیامک
                    String message = sms.getMessageBody(); // متن پیامک
                    String sender = sms.getOriginatingAddress(); // شماره فرستنده
                    Toast.makeText(context, "پیامک از: " + sender + "\n" + message, Toast.LENGTH_LONG).show(); // نمایش پیامک
                }
            }
        }
    }
} 