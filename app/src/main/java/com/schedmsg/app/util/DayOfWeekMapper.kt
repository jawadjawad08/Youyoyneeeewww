package com.schedmsg.app.util

import java.time.DayOfWeek

/**
 * نستخدم ترميزًا ثابتًا للأيام مستقلاً عن الـ Locale:
 * 1=الأحد, 2=الإثنين, 3=الثلاثاء, 4=الأربعاء, 5=الخميس, 6=الجمعة, 7=السبت
 * هذا يطابق ترتيب الأسبوع العربي/الشرق أوسطي المعتاد (يبدأ بالأحد).
 */
object DayOfWeekMapper {

    val ARABIC_DAY_NAMES = listOf(
        1 to "الأحد",
        2 to "الإثنين",
        3 to "الثلاثاء",
        4 to "الأربعاء",
        5 to "الخميس",
        6 to "الجمعة",
        7 to "السبت"
    )

    val ARABIC_DAY_SHORT = mapOf(
        1 to "أحد",
        2 to "إثنين",
        3 to "ثلاثاء",
        4 to "أربعاء",
        5 to "خميس",
        6 to "جمعة",
        7 to "سبت"
    )

    fun fromJavaDayOfWeek(day: DayOfWeek): Int = when (day) {
        DayOfWeek.SUNDAY -> 1
        DayOfWeek.MONDAY -> 2
        DayOfWeek.TUESDAY -> 3
        DayOfWeek.WEDNESDAY -> 4
        DayOfWeek.THURSDAY -> 5
        DayOfWeek.FRIDAY -> 6
        DayOfWeek.SATURDAY -> 7
    }

    fun nameOf(code: Int): String = ARABIC_DAY_NAMES.firstOrNull { it.first == code }?.second ?: ""

    fun shortNameOf(code: Int): String = ARABIC_DAY_SHORT[code] ?: ""

    /** يحوّل قائمة أيام مختارة إلى نص وصفي مختصر وذكي، مثال: "من السبت إلى الخميس" أو "الجمعة، السبت" */
    fun describeDays(selected: List<Int>): String {
        if (selected.isEmpty()) return ""
        if (selected.size == 7) return "كل أيام الأسبوع"
        val sorted = selected.sorted()

        // تحقق إذا كانت الأيام تشكل مدى متتاليًا (بشكل دائري عبر الأسبوع)
        for (startIdx in sorted.indices) {
            val start = sorted[startIdx]
            val seq = mutableListOf(start)
            var current = start
            while (seq.size < sorted.size) {
                current = if (current == 7) 1 else current + 1
                seq.add(current)
            }
            if (seq.toSet() == sorted.toSet() && isConsecutive(seq)) {
                return if (seq.size == sorted.size) {
                    "من ${nameOf(seq.first())} إلى ${nameOf(seq.last())}"
                } else ""
            }
        }
        return sorted.joinToString("، ") { shortNameOf(it) }
    }

    private fun isConsecutive(seq: List<Int>): Boolean {
        for (i in 1 until seq.size) {
            val prev = seq[i - 1]
            val expectedNext = if (prev == 7) 1 else prev + 1
            if (seq[i] != expectedNext) return false
        }
        return true
    }
}
