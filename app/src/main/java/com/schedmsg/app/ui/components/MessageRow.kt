package com.schedmsg.app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.schedmsg.app.data.ScheduledMessage
import com.schedmsg.app.ui.theme.GoldAccent
import com.schedmsg.app.ui.theme.TextPrimary
import com.schedmsg.app.ui.theme.TextSecondary
import com.schedmsg.app.util.DayOfWeekMapper
import com.schedmsg.app.util.TimeFormat

@Composable
fun MessageRow(
    message: ScheduledMessage,
    onClick: () -> Unit,
    onToggleEnabled: () -> Unit
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        contentPadding = 16,
        cornerRadius = 20
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (message.isEnabled) TextPrimary else TextSecondary,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                )
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 4.dp))
                Text(
                    text = "${DayOfWeekMapper.describeDays(message.daysList())} · ${TimeFormat.minutesToArabicTime(message.startMinutes)} - ${TimeFormat.minutesToArabicTime(message.endMinutes)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    maxLines = 1
                )
            }
            Switch(
                checked = message.isEnabled,
                onCheckedChange = { onToggleEnabled() },
                colors = SwitchDefaults.colors(checkedTrackColor = GoldAccent)
            )
            Icon(
                imageVector = Icons.Filled.ChevronLeft,
                contentDescription = null,
                tint = TextSecondary
            )
        }
    }
}
