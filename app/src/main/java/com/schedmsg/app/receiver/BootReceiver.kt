package com.schedmsg.app.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.schedmsg.app.widget.RefreshWidgetWorker

/**
 * يُستدعى تلقائيًا بعد إعادة تشغيل الهاتف (أو بعد تحديث التطبيق).
 * يعيد جدولة كل التنبيهات ويحدّث الودجت فورًا حتى يعمل كل شيء
 * من جديد دون الحاجة لفتح التطبيق يدويًا.
 */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == Intent.ACTION_MY_PACKAGE_REPLACED
        ) {
            val request = OneTimeWorkRequestBuilder<RefreshWidgetWorker>().build()
            WorkManager.getInstance(context).enqueue(request)
        }
    }
}
