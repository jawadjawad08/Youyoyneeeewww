package com.schedmsg.app.util

import com.schedmsg.app.data.ScheduledMessage
import java.time.LocalDateTime

/**
 * يحدد الرسالة النشطة حاليًا من بين مجموعة الرسائل المجدولة، ويتعامل بشكل صحيح
 * مع الحالات التي يمتد فيها المدى الزمني عبر منتصف الليل (مثال: 22:00 -> 02:00).
 */
object ActiveMessageResolver {

    data class Result(
        val active: ScheduledMessage?,
        /** الوقت (بالدقائق منذ منتصف الليل ضمن "اليوم المرجعي") الذي تنتهي عنده الرسالة النشطة، لعرضه للمستخدم */
        val activeEndsAtMinutes: Int?
    )

    fun findActive(messages: List<ScheduledMessage>, now: LocalDateTime = LocalDateTime.now()): Result {
        val todayCode = DayOfWeekMapper.fromJavaDayOfWeek(now.dayOfWeek)
        val yesterdayCode = if (todayCode == 1) 7 else todayCode - 1
        val nowMinutes = now.hour * 60 + now.minute

        val enabled = messages.filter { it.isEnabled }

        // الحالة 1: رسالة عادية (لا تعبر منتصف الليل) يومها هو اليوم الحالي
        val normalToday = enabled.filter { it.startMinutes < it.endMinutes && todayCode in it.daysList() }
        val matchNormal = normalToday.firstOrNull { nowMinutes in it.startMinutes until it.endMinutes }
        if (matchNormal != null) {
            return Result(matchNormal, matchNormal.endMinutes)
        }

        // الحالة 2: رسالة تعبر منتصف الليل وبدأت اليوم (يومها اليوم، والوقت بعد البداية أو قبل منتصف الليل)
        val crossingToday = enabled.filter { it.startMinutes >= it.endMinutes && todayCode in it.daysList() }
        val matchCrossingStartedToday = crossingToday.firstOrNull { nowMinutes >= it.startMinutes }
        if (matchCrossingStartedToday != null) {
            // تنتهي بعد منتصف الليل، أي بعد إضافة 1440 دقيقة
            return Result(matchCrossingStartedToday, matchCrossingStartedToday.endMinutes + 1440)
        }

        // الحالة 3: رسالة تعبر منتصف الليل وبدأت الأمس، ولا زلنا ضمن الجزء الصباحي منها اليوم
        val crossingFromYesterday = enabled.filter { it.startMinutes >= it.endMinutes && yesterdayCode in it.daysList() }
        val matchCrossingFromYesterday = crossingFromYesterday.firstOrNull { nowMinutes < it.endMinutes }
        if (matchCrossingFromYesterday != null) {
            return Result(matchCrossingFromYesterday, matchCrossingFromYesterday.endMinutes)
        }

        return Result(null, null)
    }

    /** يعيد قائمة الرسائل القادمة (غير النشطة حاليًا) مرتّبة بحسب أقرب وقت بداية، لعرضها في الجدول */
    fun upcoming(messages: List<ScheduledMessage>, activeId: Long?): List<ScheduledMessage> {
        return messages
            .filter { it.id != activeId }
            .sortedBy { it.startMinutes }
    }
}
