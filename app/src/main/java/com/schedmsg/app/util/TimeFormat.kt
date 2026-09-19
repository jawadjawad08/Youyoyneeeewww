package com.schedmsg.app.util

object TimeFormat {

    /** يحوّل الدقائق منذ منتصف الليل إلى نص عربي مثل "٨:٠٠ ص" */
    fun minutesToArabicTime(totalMinutes: Int): String {
        val normalized = ((totalMinutes % 1440) + 1440) % 1440
        var hour24 = normalized / 60
        val minute = normalized % 60
        val isAm = hour24 < 12
        var hour12 = hour24 % 12
        if (hour12 == 0) hour12 = 12
        val period = if (isAm) "ص" else "م"
        val minuteStr = minute.toString().padStart(2, '0')
        return "$hour12:$minuteStr $period"
    }

    fun hourMinuteToMinutes(hour: Int, minute: Int): Int = hour * 60 + minute
}
