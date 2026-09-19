package com.schedmsg.app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.schedmsg.app.ui.theme.GoldAccentBright
import com.schedmsg.app.ui.theme.TextPrimary
import com.schedmsg.app.ui.theme.TextSecondary
import com.schedmsg.app.util.WorldClock

/**
 * صف من بطاقتين زجاجيتين يعرضان الوقت الحالي في الأردن وسوريا،
 * كل بطاقة تحمل علم الدولة الحقيقي ووقتها بشكل واضح وأنيق.
 */
@Composable
fun WorldClocksRow(modifier: Modifier = Modifier, refreshKey: Any? = null) {
    // قراءة refreshKey تجبر Compose على إعادة الحساب عند تغيّر الوقت في الشاشة الرئيسية
    val amman = remember(refreshKey) { WorldClock.now(WorldClock.City.AMMAN) }
    val damascus = remember(refreshKey) { WorldClock.now(WorldClock.City.DAMASCUS) }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        CityTimeCard(cityTime = amman, modifier = Modifier.weight(1f))
        CityTimeCard(cityTime = damascus, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun CityTimeCard(cityTime: WorldClock.CityTime, modifier: Modifier = Modifier) {
    GlassCard(modifier = modifier, contentPadding = 14, cornerRadius = 20) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Image(
                    painter = painterResource(id = cityTime.city.flagRes),
                    contentDescription = cityTime.city.arabicName,
                    modifier = Modifier
                        .size(width = 26.dp, height = 18.dp)
                        .clip(RoundedCornerShape(4.dp))
                )
                Text(
                    text = cityTime.city.arabicName,
                    style = MaterialTheme.typography.labelLarge,
                    color = TextSecondary
                )
            }
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = cityTime.timeText,
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = cityTime.periodText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = GoldAccentBright
                )
            }
            Text(
                text = cityTime.dateText,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }
    }
}
