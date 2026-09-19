package com.schedmsg.app.widget

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.schedmsg.app.data.MessageRepository
import com.schedmsg.app.util.AlarmScheduler

/**
 * عملية خفيفة تُنفَّذ فقط عند الحاجة الفعلية (وليس بشكل دوري متكرر)،
 * لذلك استهلاك البطارية يبقى ضئيلًا جدًا:
 * 1) تعيد جدولة تنبيهات الأسبوع القادم.
 * 2) تُحدّث كل نسخ الودجت المثبّتة على الشاشة الرئيسية.
 */
class RefreshWidgetWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val repo = MessageRepository.getInstance(applicationContext)
        val messages = repo.getAllOnce()

        AlarmScheduler.rescheduleAll(applicationContext, messages)
        ActiveMessageWidget.updateAll(applicationContext)

        return Result.success()
    }

    companion object {
        const val TAG_RESCHEDULE = "reschedule_and_refresh"
    }
}
