package com.schedmsg.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.schedmsg.app.data.ScheduledMessage
import com.schedmsg.app.ui.components.ConfirmDeleteDialog
import com.schedmsg.app.ui.components.DaySelector
import com.schedmsg.app.ui.components.GlassCard
import com.schedmsg.app.ui.components.ModernTimePicker
import com.schedmsg.app.ui.theme.AppBackgroundBrush
import com.schedmsg.app.ui.theme.DangerRed
import com.schedmsg.app.ui.theme.GoldAccent
import com.schedmsg.app.ui.theme.NightPurpleDeep
import com.schedmsg.app.ui.theme.TextPrimary
import com.schedmsg.app.ui.theme.TextSecondary
import com.schedmsg.app.util.TimeFormat

@Composable
fun AddEditMessageScreen(
    existing: ScheduledMessage?,
    onSave: (id: Long?, text: String, days: List<Int>, startMinutes: Int, endMinutes: Int) -> Unit,
    onDelete: ((ScheduledMessage) -> Unit)?,
    onBack: () -> Unit
) {
    var text by remember { mutableStateOf(existing?.text ?: "") }
    var selectedDays by remember {
        mutableStateOf(existing?.daysList()?.toSet() ?: setOf(1, 2, 3, 4, 5, 6, 7))
    }

    val initialStart = existing?.startMinutes ?: (8 * 60)
    val initialEnd = existing?.endMinutes ?: (17 * 60)

    var startMinutes by remember { mutableIntStateOf(initialStart) }
    var endMinutes by remember { mutableIntStateOf(initialEnd) }

    var showDeleteConfirm by remember { mutableStateOf(false) }

    val canSave = text.isNotBlank() && selectedDays.isNotEmpty()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackgroundBrush)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(28.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowForward, contentDescription = "رجوع", tint = TextPrimary)
                }
                Spacer(modifier = Modifier.size(4.dp))
                Text(
                    text = if (existing == null) "إضافة رسالة" else "تعديل الرسالة",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                if (existing != null && onDelete != null) {
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(onClick = { showDeleteConfirm = true }) {
                        Icon(Icons.Filled.Delete, contentDescription = "حذف", tint = DangerRed)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                // 1) اختيار الأيام
                SectionLabel("الأيام")
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    DaySelector(
                        selectedDays = selectedDays,
                        onToggleDay = { day ->
                            selectedDays = if (day in selectedDays) {
                                selectedDays - day
                            } else {
                                selectedDays + day
                            }
                        }
                    )
                }

                // 2) وقت البداية
                SectionLabel("وقت البداية")
                ModernTimePicker(
                    initialHour24 = startMinutes / 60,
                    initialMinute = startMinutes % 60,
                    onTimeChanged = { h, m -> startMinutes = TimeFormat.hourMinuteToMinutes(h, m) }
                )

                // 3) وقت النهاية
                SectionLabel("وقت النهاية")
                ModernTimePicker(
                    initialHour24 = endMinutes / 60,
                    initialMinute = endMinutes % 60,
                    onTimeChanged = { h, m -> endMinutes = TimeFormat.hourMinuteToMinutes(h, m) }
                )

                // 4) نص الرسالة
                SectionLabel("نص الرسالة")
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    placeholder = { Text("اكتب الرسالة هنا...", color = TextSecondary) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = GoldAccent,
                        unfocusedBorderColor = TextSecondary.copy(alpha = 0.4f),
                        cursorColor = GoldAccent
                    ),
                    shape = RoundedCornerShape(18.dp)
                )

                Spacer(modifier = Modifier.height(4.dp))

                // 5) زر الحفظ
                Button(
                    onClick = {
                        onSave(existing?.id, text.trim(), selectedDays.toList(), startMinutes, endMinutes)
                    },
                    enabled = canSave,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldAccent)
                ) {
                    Text("حفظ", style = MaterialTheme.typography.titleMedium, color = NightPurpleDeep, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    if (showDeleteConfirm && existing != null && onDelete != null) {
        ConfirmDeleteDialog(
            message = "سيتم حذف رسالة \"${existing.text}\" نهائيًا. لا يمكن التراجع عن هذا الإجراء.",
            onConfirm = {
                showDeleteConfirm = false
                onDelete(existing)
            },
            onDismiss = { showDeleteConfirm = false }
        )
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        color = TextSecondary
    )
}
