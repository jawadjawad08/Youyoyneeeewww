package com.schedmsg.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.SnapLayoutInfoProvider
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.schedmsg.app.ui.theme.GoldAccent
import com.schedmsg.app.ui.theme.GoldAccentBright
import com.schedmsg.app.ui.theme.NightPurpleDeep
import com.schedmsg.app.ui.theme.TextPrimary
import com.schedmsg.app.ui.theme.TextSecondary

/**
 * منتقي وقت عصري بأسلوب "العجلة الدوارة" (Wheel Picker) بدل قرص الساعة التقليدي.
 * ثلاث عجلات: الساعة (12 ساعة)، الدقيقة، وص/م — بتصميم زجاجي مع تمييز العنصر المختار
 * بشريط ذهبي متوهج ثابت في المنتصف، والأرقام تمرّ خلاله بالتمرير.
 */
@Composable
fun ModernTimePicker(
    initialHour24: Int,
    initialMinute: Int,
    onTimeChanged: (hour24: Int, minute: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val isAmInit = if (initialHour24 < 12) 0 else 1
    val hour12Init = if (initialHour24 % 12 == 0) 12 else initialHour24 % 12

    var hour12 by remember { mutableIntStateOf(hour12Init) }
    var minute by remember { mutableIntStateOf(initialMinute) }
    var isAm by remember { mutableIntStateOf(isAmInit) }

    fun emit() {
        val h24 = when {
            isAm == 0 && hour12 == 12 -> 0
            isAm == 0 -> hour12
            isAm == 1 && hour12 == 12 -> 12
            else -> hour12 + 12
        }
        onTimeChanged(h24, minute)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(220.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(NightPurpleDeep.copy(alpha = 0.55f), NightPurpleDeep.copy(alpha = 0.75f))
                )
            )
    ) {
        // شريط التحديد المركزي المتوهج (ثابت، لا يتحرك)
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .height(52.dp)
                .padding(horizontal = 12.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            GoldAccent.copy(alpha = 0.22f),
                            GoldAccentBright.copy(alpha = 0.30f),
                            GoldAccent.copy(alpha = 0.22f)
                        )
                    )
                )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            WheelColumn(
                items = (1..12).toList(),
                selectedValue = hour12,
                onSettled = { hour12 = it; emit() },
                label = { it.toString() },
                modifier = Modifier.width(64.dp)
            )
            Text(
                text = ":",
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
            WheelColumn(
                items = (0..59).toList(),
                selectedValue = minute,
                onSettled = { minute = it; emit() },
                label = { it.toString().padStart(2, '0') },
                modifier = Modifier.width(64.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))
            WheelColumn(
                items = listOf(0, 1),
                selectedValue = isAm,
                onSettled = { isAm = it; emit() },
                label = { if (it == 0) "ص" else "م" },
                modifier = Modifier.width(56.dp)
            )
        }
    }
}

private val ITEM_HEIGHT = 52.dp

/**
 * عمود عجلة واحد: يعرض القيم فوق وتحت العنصر المختار، مع حشوة وهمية بمقدار عنصر واحد
 * أعلى وأسفل القائمة الفعلية، بحيث يستطيع أول وآخر عنصر الوصول لمنتصف الإطار المرئي.
 * onSettled يُستدعى فقط بعد استقرار التمرير (وليس أثناء السحب) لتفادي قفزات غير مقصودة.
 */
@Composable
private fun WheelColumn(
    items: List<Int>,
    selectedValue: Int,
    onSettled: (Int) -> Unit,
    label: (Int) -> String,
    modifier: Modifier = Modifier
) {
    val paddingCount = 1
    val initialIndex = items.indexOf(selectedValue).coerceAtLeast(0)
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)
    val snapLayoutInfoProvider = remember(listState) { SnapLayoutInfoProvider(listState) }
    val flingBehavior = rememberSnapFlingBehavior(snapLayoutInfoProvider)

    // نراقب استقرار التمرير: عندما تتوقف الحركة (isScrollInProgress == false)
    // نحسب العنصر الأقرب للمنتصف ونبلّغه للخارج.
    LaunchedEffect(listState) {
        snapshotFlow { listState.isScrollInProgress }
            .collect { scrolling ->
                if (!scrolling) {
                    val layoutInfo = listState.layoutInfo
                    if (layoutInfo.visibleItemsInfo.isEmpty()) return@collect
                    val viewportCenter =
                        (layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset) / 2
                    val closest = layoutInfo.visibleItemsInfo.minByOrNull { info ->
                        kotlin.math.abs((info.offset + info.size / 2) - viewportCenter)
                    }
                    val realIndex = (closest?.index ?: initialIndex) - paddingCount
                    val clamped = realIndex.coerceIn(0, items.size - 1)
                    onSettled(items[clamped])
                }
            }
    }

    LazyColumn(
        state = listState,
        modifier = modifier.height(ITEM_HEIGHT * 3),
        flingBehavior = flingBehavior,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(paddingCount) { Box(modifier = Modifier.height(ITEM_HEIGHT)) }
        items(items.size) { idx ->
            val value = items[idx]
            val isSelected = value == selectedValue
            Box(
                modifier = Modifier
                    .height(ITEM_HEIGHT)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label(value),
                    style = if (isSelected) MaterialTheme.typography.headlineMedium else MaterialTheme.typography.titleMedium,
                    color = if (isSelected) TextPrimary else TextSecondary,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier.alpha(if (isSelected) 1f else 0.55f)
                )
            }
        }
        items(paddingCount) { Box(modifier = Modifier.height(ITEM_HEIGHT)) }
    }
}
