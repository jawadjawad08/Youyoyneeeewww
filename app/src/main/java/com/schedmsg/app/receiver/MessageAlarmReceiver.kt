package com.schedmsg.app.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.schedmsg.app.widget.RefreshWidgetWorker

/**
 * يُستدعى بدقة في لحظة بداية/نهاية أي رسالة مجدولة.
 * مهمّته: تحديث الـ Widget فورًا، وإعادة جدولة التنبيهات القادمة
 * (لأن كل تشغيل يجدول فقط الأسبوع القادم من هذه اللحظة).
 */
class MessageAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val request = OneTimeWorkRequestBuilder<RefreshWidgetWorker>()
            .addTag(RefreshWidgetWorker.TAG_RESCHEDULE)
            .build()
        WorkManager.getInstance(context).enqueue(request)
    }
}
