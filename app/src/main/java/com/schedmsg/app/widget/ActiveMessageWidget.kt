package com.schedmsg.app.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalSize
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.color.ColorProvider
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import com.schedmsg.app.MainActivity
import com.schedmsg.app.R
import com.schedmsg.app.data.MessageRepository
import com.schedmsg.app.util.ActiveMessageResolver
import com.schedmsg.app.util.TimeFormat
import com.schedmsg.app.util.WorldClock
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * ويدجت "ياسو المزة": يعرض الرسالة النشطة، الساعة الحالية، ووقت الأردن وسوريا.
 * يتكيف مع 3 نطاقات مقاسات مختلفة عبر SizeMode.Responsive:
 * - صغير: الرسالة النشطة فقط (مساحة محدودة).
 * - متوسط: الرسالة النشطة + الساعة المحلية.
 * - كبير: كل شيء — الرسالة، الساعة، ووقت الدولتين بعلميهما.
 */
class ActiveMessageWidget : GlanceAppWidget() {

    override val sizeMode = SizeMode.Responsive(
        setOf(
            DpSize(120.dp, 60.dp),   // صغير: 2x1 تقريبًا
            DpSize(250.dp, 110.dp),  // متوسط: 4x2 تقريبًا
            DpSize(250.dp, 180.dp)   // كبير: 4x3 أو أكبر
        )
    )

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val repo = MessageRepository.getInstance(context)
        val messages = repo.getAllOnce()
        val now = LocalDateTime.now()
        val result = ActiveMessageResolver.findActive(messages, now)
        val amman = WorldClock.now(WorldClock.City.AMMAN)
        val damascus = WorldClock.now(WorldClock.City.DAMASCUS)
        val localTime = now.format(DateTimeFormatter.ofPattern("h:mm"))
        val localPeriod = if (now.hour < 12) "ص" else "م"

        provideContent {
            val size = LocalSize.current
            WidgetRoot(
                size = size,
                activeText = result.active?.text,
                endsAtLabel = result.activeEndsAtMinutes?.let { TimeFormat.minutesToArabicTime(it) },
                localTime = localTime,
                localPeriod = localPeriod,
                amman = amman,
                damascus = damascus
            )
        }
    }

    companion object {
        suspend fun updateAll(context: Context) {
            val manager = GlanceAppWidgetManager(context)
            val widget = ActiveMessageWidget()
            val ids = manager.getGlanceIds(ActiveMessageWidget::class.java)
            ids.forEach { id -> widget.update(context, id) }
        }
    }
}

private val CardBackground = ColorProvider(day = Color(0xE61A0B2E), night = Color(0xE61A0B2E))
private val GoldText = ColorProvider(day = Color(0xFFF0BE64), night = Color(0xFFF0BE64))
private val WhiteText = ColorProvider(day = Color(0xFFFFF6ED), night = Color(0xFFFFF6ED))
private val MutedText = ColorProvider(day = Color(0xFFCFC0DE), night = Color(0xFFCFC0DE))

@Composable
private fun WidgetRoot(
    size: DpSize,
    activeText: String?,
    endsAtLabel: String?,
    localTime: String,
    localPeriod: String,
    amman: WorldClock.CityTime,
    damascus: WorldClock.CityTime
) {
    val isLarge = size.height >= 150.dp
    val isMedium = size.height >= 90.dp && !isLarge

    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(CardBackground)
            .cornerRadius(26.dp)
            .padding(if (isLarge) 16.dp else 12.dp)
            .clickable(actionStartActivity<MainActivity>()),
        horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
        verticalAlignment = Alignment.Vertical.CenterVertically
    ) {
        // الرسالة النشطة أو "لا توجد رسالة حاليًا" — دومًا في المنتصف
        Column(
            modifier = GlanceModifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
            verticalAlignment = Alignment.Vertical.CenterVertically
        ) {
            if (activeText != null) {
                Text(
                    text = activeText,
                    style = TextStyle(
                        color = WhiteText,
                        fontSize = if (isLarge) 18.sp else 15.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    ),
                    maxLines = if (isLarge) 2 else 1
                )
                if (endsAtLabel != null && isLarge) {
                    Text(
                        text = "حتى $endsAtLabel",
                        style = TextStyle(
                            color = GoldText,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                    )
                }
            } else {
                Text(
                    text = "لا توجد رسالة حاليًا",
                    style = TextStyle(
                        color = MutedText,
                        fontSize = if (isLarge) 15.sp else 13.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    ),
                    maxLines = 2
                )
            }
        }

        if (isMedium || isLarge) {
            Spacer(modifier = GlanceModifier.height(6.dp))
            Text(
                text = "$localTime $localPeriod",
                style = TextStyle(
                    color = WhiteText,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            )
        }

        if (isLarge) {
            Spacer(modifier = GlanceModifier.height(10.dp))
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
                verticalAlignment = Alignment.Vertical.CenterVertically
            ) {
                CityChip(amman, R.drawable.flag_jordan)
                Spacer(modifier = GlanceModifier.width(16.dp))
                CityChip(damascus, R.drawable.flag_syria)
            }
        }
    }
}

@Composable
private fun CityChip(cityTime: WorldClock.CityTime, flagRes: Int) {
    Row(verticalAlignment = Alignment.Vertical.CenterVertically) {
        Image(
            provider = ImageProvider(flagRes),
            contentDescription = cityTime.city.arabicName,
            modifier = GlanceModifier.size(width = 20.dp, height = 14.dp)
        )
        Spacer(modifier = GlanceModifier.width(6.dp))
        Text(
            text = "${cityTime.timeText} ${cityTime.periodText}",
            style = TextStyle(
                color = WhiteText,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        )
    }
}
