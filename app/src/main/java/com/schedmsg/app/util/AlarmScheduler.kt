package com.schedmsg.app.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.schedmsg.app.data.ScheduledMessage
import com.schedmsg.app.receiver.MessageAlarmReceiver
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * يجدول تنبيهات دقيقة (AlarmManager) عند كل "نقطة انتقال" محتملة:
 * أوقات بداية ونهاية كل رسالة، للأيام السبعة القادمة.
 * عند إطلاق أي تنبيه، يقوم MessageAlarmReceiver بتحديث الودجت والحالة.
 *
 * هذا يضمن أن التغيير من رسالة لأخرى يحدث تلقائيًا دون فتح التطبيق،
 * مع استهلاك بطارية منخفض لأننا لا نستخدم أي استقصاء دوري متكرر.
 */
object AlarmScheduler {

    private const val REQUEST_CODE_BASE = 5000

    fun rescheduleAll(context: Context, messages: List<ScheduledMessage>) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        cancelAll(context)

        val now = LocalDateTime.now()
        val triggerPoints = sortedSetOf<LocalDateTime>()

        // ننظر إلى الأيام السبعة القادمة فقط (كافٍ لتغطية أي جدول أسبوعي متكرر،
        // وسنعيد الجدولة تلقائيًا بعد كل تنبيه لتغطية الأسبوع التالي)
        for (dayOffset in 0..7) {
            val date = LocalDate.now().plusDays(dayOffset.toLong())
            val dayCode = DayOfWeekMapper.fromJavaDayOfWeek(date.dayOfWeek)
            for (msg in messages) {
                if (!msg.isEnabled) continue
                if (dayCode !in msg.daysList()) continue

                val startDateTime = date.atTime(LocalTime.of(msg.startMinutes / 60, msg.startMinutes % 60))
                if (startDateTime.isAfter(now)) triggerPoints.add(startDateTime)

                val endMinutesNormalized = msg.endMinutes % 1440
                var endDate = date
                if (msg.startMinutes >= msg.endMinutes) {
                    endDate = date.plusDays(1) // يعبر منتصف الليل
                }
                val endDateTime = endDate.atTime(LocalTime.of(endMinutesNormalized / 60, endMinutesNormalized % 60))
                if (endDateTime.isAfter(now)) triggerPoints.add(endDateTime)
            }
        }

        // أيضًا: نضيف تنبيه عند منتصف كل ليلة لضمان تحديث "لا توجد رسالة" بشكل دقيق
        // حتى لو لم يوجد أي جدول يبدأ/ينتهي بالضبط في تلك اللحظة.
        for (dayOffset in 1..7) {
            triggerPoints.add(LocalDate.now().plusDays(dayOffset.toLong()).atStartOfDay())
        }

        var requestCode = REQUEST_CODE_BASE
        for (point in triggerPoints) {
            val triggerMillis = point.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
            val intent = Intent(context, MessageAlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            requestCode++

            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (alarmManager.canScheduleExactAlarms()) {
                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerMillis, pendingIntent)
                    } else {
                        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerMillis, pendingIntent)
                    }
                } else {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerMillis, pendingIntent)
                }
            } catch (_: SecurityException) {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerMillis, pendingIntent)
            }
        }

        // نحتفظ بعدد التنبيهات المستخدمة لكي نستطيع إلغاءها لاحقًا بدقة
        context.getSharedPreferences("alarm_prefs", Context.MODE_PRIVATE)
            .edit()
            .putInt("last_request_code_count", requestCode - REQUEST_CODE_BASE)
            .apply()
    }

    fun cancelAll(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val prevCount = context.getSharedPreferences("alarm_prefs", Context.MODE_PRIVATE)
            .getInt("last_request_code_count", 0)
        for (i in 0 until (prevCount + 50)) {
            val intent = Intent(context, MessageAlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                REQUEST_CODE_BASE + i,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.cancel(pendingIntent)
        }
    }
}
