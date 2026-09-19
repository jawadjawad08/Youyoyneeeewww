package com.schedmsg.app.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.schedmsg.app.data.ScheduledMessage
import com.schedmsg.app.ui.MainUiState
import com.schedmsg.app.ui.components.GlassCard
import com.schedmsg.app.ui.components.MessageRow
import com.schedmsg.app.ui.components.WorldClocksRow
import com.schedmsg.app.ui.theme.AppBackgroundBrush
import com.schedmsg.app.ui.theme.GlassSurfaceStrong
import com.schedmsg.app.ui.theme.GoldAccent
import com.schedmsg.app.ui.theme.HeroGradientBrush
import com.schedmsg.app.ui.theme.NightPurpleDeep
import com.schedmsg.app.ui.theme.TextPrimary
import com.schedmsg.app.ui.theme.TextSecondary
import com.schedmsg.app.util.ActiveMessageResolver
import com.schedmsg.app.util.TimeFormat
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun HomeScreen(
    state: MainUiState,
    onAddClick: () -> Unit,
    onMessageClick: (ScheduledMessage) -> Unit,
    onToggleEnabled: (ScheduledMessage) -> Unit,
    onIconPickerClick: () -> Unit
) {
    Scaffold(
        containerColor = Color.Transparent,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddClick,
                containerColor = GoldAccent,
                contentColor = NightPurpleDeep
            ) {
                Icon(Icons.Filled.Add, contentDescription = null)
                Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                Text("إضافة رسالة", fontWeight = FontWeight.Bold)
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AppBackgroundBrush)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    top = padding.calculateTopPadding() + 20.dp,
                    bottom = padding.calculateBottomPadding() + 100.dp,
                    start = 20.dp,
                    end = 20.dp
                ),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                item { BrandHeader(onIconPickerClick = onIconPickerClick) }
                item { ClockHeader(state) }
                item { WorldClocksRow(refreshKey = state.now) }
                item { ActiveMessageHero(state) }
                item {
                    Text(
                        "الرسائل المجدولة",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextSecondary
                    )
                }
                if (state.messages.isEmpty()) {
                    item { EmptyMessagesHint() }
                } else {
                    val upcoming = ActiveMessageResolver.upcoming(state.messages, state.activeMessage?.id)
                    val activeMsg = state.activeMessage
                    val ordered: List<ScheduledMessage> = if (activeMsg != null) {
                        listOf(activeMsg) + upcoming
                    } else {
                        upcoming
                    }
                    items(ordered, key = { it.id }) { msg ->
                        MessageRow(
                            message = msg,
                            onClick = { onMessageClick(msg) },
                            onToggleEnabled = { onToggleEnabled(msg) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BrandHeader(onIconPickerClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "ياسو المزة",
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary,
            fontWeight = FontWeight.Bold
        )
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(GlassSurfaceStrong)
                .clickable { onIconPickerClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Palette,
                contentDescription = "تغيير أيقونة التطبيق",
                tint = GoldAccent,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
private fun ClockHeader(state: MainUiState) {
    val timeText = state.now.format(DateTimeFormatter.ofPattern("h:mm"))
    val period = if (state.now.hour < 12) "صباحًا" else "مساءً"
    val dayName = com.schedmsg.app.util.DayOfWeekMapper.nameOf(
        com.schedmsg.app.util.DayOfWeekMapper.fromJavaDayOfWeek(state.now.dayOfWeek)
    )
    val dateText = state.now.format(DateTimeFormatter.ofPattern("d MMMM yyyy", Locale("ar")))

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Text(
            text = timeText,
            style = MaterialTheme.typography.headlineLarge,
            color = TextPrimary,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = period,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "$dayName، $dateText",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
    }
}

@Composable
private fun ActiveMessageHero(state: MainUiState) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        cornerRadius = 28,
        accentBorderBrush = HeroGradientBrush
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AnimatedContent(
                targetState = state.activeMessage,
                transitionSpec = { fadeIn(animationSpec = tween(400)) togetherWith fadeOut(animationSpec = tween(250)) },
                modifier = Modifier.fillMaxSize(),
                label = "active_message"
            ) { active ->
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    if (active != null) {
                        Text(
                            text = active.text,
                            style = MaterialTheme.typography.headlineMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            maxLines = 3
                        )
                        state.activeEndsAtMinutes?.let { end ->
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "حتى ${TimeFormat.minutesToArabicTime(end)}",
                                style = MaterialTheme.typography.bodyLarge,
                                color = TextSecondary
                            )
                        }
                    } else {
                        Text(
                            text = "لا توجد رسالة حاليًا",
                            style = MaterialTheme.typography.titleLarge,
                            color = TextSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyMessagesHint() {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Text(
                "لا توجد رسائل بعد",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                "اضغط على \"إضافة رسالة\" لإنشاء أول جدول",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}
