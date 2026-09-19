package com.schedmsg.app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.schedmsg.app.R
import com.schedmsg.app.ui.components.GlassCard
import com.schedmsg.app.ui.theme.AppBackgroundBrush
import com.schedmsg.app.ui.theme.GoldAccent
import com.schedmsg.app.ui.theme.TextPrimary
import com.schedmsg.app.ui.theme.TextSecondary
import com.schedmsg.app.util.AppIconManager

@Composable
fun IconPickerScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var selected by remember { mutableStateOf(AppIconManager.getCurrentIcon(context)) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackgroundBrush)
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
            Spacer(modifier = Modifier.height(28.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowForward, contentDescription = "رجوع", tint = TextPrimary)
                }
                Text(
                    "أيقونة التطبيق",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "اختر الشكل الذي يعجبك — سيظهر فورًا على شاشتك الرئيسية",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                modifier = Modifier.padding(start = 4.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))

            AppIconManager.IconOption.entries.forEach { option ->
                IconOptionRow(
                    option = option,
                    isSelected = option == selected,
                    onSelect = {
                        selected = option
                        AppIconManager.setIcon(context, option)
                    }
                )
                Spacer(modifier = Modifier.height(14.dp))
            }
        }
    }
}

private fun iconDrawableFor(option: AppIconManager.IconOption): Int = when (option) {
    AppIconManager.IconOption.JASMINE -> R.mipmap.ic_launcher_jasmine
    AppIconManager.IconOption.MOON -> R.mipmap.ic_launcher_moon
    AppIconManager.IconOption.HEART -> R.mipmap.ic_launcher_heart
}

@Composable
private fun IconOptionRow(
    option: AppIconManager.IconOption,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() },
        contentPadding = 16,
        cornerRadius = 22
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(
                            width = if (isSelected) 2.dp else 0.dp,
                            color = if (isSelected) GoldAccent else Color.Transparent,
                            shape = RoundedCornerShape(16.dp)
                        )
                ) {
                    Image(
                        painter = painterResource(id = iconDrawableFor(option)),
                        contentDescription = option.displayName,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = option.displayName,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary
                )
            }
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(GoldAccent),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.Check,
                        contentDescription = "مُختارة",
                        tint = Color(0xFF2D124A),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
