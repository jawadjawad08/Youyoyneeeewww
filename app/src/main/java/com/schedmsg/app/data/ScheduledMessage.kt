package com.schedmsg.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * يمثل رسالة واحدة مجدولة.
 *
 * days: مجموعة أيام الأسبوع المخزّنة كنص مفصول بفواصل، مثال: "1,2,3,4,5"
 *       حيث 1=الأحد ... 7=السبت (متوافق مع java.time.DayOfWeek لكن مُحوَّل يدويًا
 *       لتفادي أي التباس، راجع util/DayOfWeekMapper.kt)
 * startMinutes / endMinutes: الوقت كعدد الدقائق منذ منتصف الليل (0..1439)
 *       يسمح هذا بحسابات بسيطة وسريعة بدون تحويلات معقدة.
 * إذا كانت endMinutes أصغر من أو تساوي startMinutes، فهذا يعني أن المدى
 * يمتد عبر منتصف الليل إلى اليوم التالي (مثال: 22:00 -> 02:00).
 */
@Entity(tableName = "scheduled_messages")
data class ScheduledMessage(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val text: String,
    val days: String, // "1,2,3,4,5,6,7"
    val startMinutes: Int,
    val endMinutes: Int,
    val isEnabled: Boolean = true
) {
    fun daysList(): List<Int> =
        days.split(",").mapNotNull { it.trim().toIntOrNull() }

    companion object {
        fun daysToString(days: List<Int>): String = days.sorted().joinToString(",")
    }
}
