package com.schedmsg.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.schedmsg.app.ui.theme.AccentViolet
import com.schedmsg.app.ui.theme.GlassBorder
import com.schedmsg.app.ui.theme.GlassSurface
import com.schedmsg.app.ui.theme.TextPrimary
import com.schedmsg.app.ui.theme.TextSecondary
import com.schedmsg.app.util.DayOfWeekMapper

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
fun DaySelector(
    selectedDays: Set<Int>,
    onToggleDay: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        DayOfWeekMapper.ARABIC_DAY_SHORT.forEach { (code, label) ->
            val isSelected = code in selectedDays
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) AccentViolet else GlassSurface)
                    .clickable { onToggleDay(code) }
                    .wrapContentSize(Alignment.Center),
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge,
                    color = if (isSelected) TextPrimary else TextSecondary
                )
            }
        }
    }
}
