package com.schedmsg.app.util

import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * يحسب الوقت الحالي في مدينتين محددتين (عمّان ودمشق) باستخدام مناطق زمنية
 * قياسية مدمجة في نظام أندرويد (java.time / tzdata)، بدون أي حاجة للإنترنت.
 */
object WorldClock {

    enum class City(val zoneId: String, val arabicName: String, val flagRes: Int) {
        AMMAN("Asia/Amman", "الأردن", com.schedmsg.app.R.drawable.flag_jordan),
        DAMASCUS("Asia/Damascus", "سوريا", com.schedmsg.app.R.drawable.flag_syria)
    }

    data class CityTime(
        val city: City,
        val timeText: String,
        val periodText: String,
        val dateText: String
    )

    fun now(city: City): CityTime {
        val zone = ZoneId.of(city.zoneId)
        val zdt = ZonedDateTime.now(zone)
        val timeText = zdt.format(DateTimeFormatter.ofPattern("h:mm"))
        val periodText = if (zdt.hour < 12) "ص" else "م"
        val dateText = zdt.format(DateTimeFormatter.ofPattern("d MMMM", Locale("ar")))
        return CityTime(city, timeText, periodText, dateText)
    }
}
