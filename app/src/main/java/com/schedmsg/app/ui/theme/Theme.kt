package com.schedmsg.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// ============ هوية "ياسو المزة" البصرية ============
// إلهام: زهرة الياسمين البيضاء + سماء الليل البنفسجية + توهج ذهبي دافئ
val NightPurpleDeep = Color(0xFF1A0B2E)
val NightPurpleMid = Color(0xFF3B1656)
val MagentaGlow = Color(0xFF7A2C6E)
val WarmRoseEnd = Color(0xFF3A1030)

val JasmineWhite = Color(0xFFFFF6ED)
val GoldAccent = Color(0xFFF0BE64)
val GoldAccentBright = Color(0xFFFFD98A)
val OrchidAccent = Color(0xFFD874C7)
val SkyAccent = Color(0xFF7FD9E8)

val GlassSurface = Color(0x33FFFFFF)
val GlassSurfaceStrong = Color(0x52FFFFFF)
val GlassBorder = Color(0x55FFFFFF)
val TextPrimary = Color(0xFFFFF6ED)
val TextSecondary = Color(0xFFCFC0DE)
val DangerRed = Color(0xFFFF6B81)
val SuccessGreen = Color(0xFF00A652)

// خلفية التطبيق: تدرج ليلي عميق بلمسة ماجنتا دافئة، يوحي بالفخامة والأناقة
val AppBackgroundBrush = Brush.verticalGradient(
    colors = listOf(NightPurpleDeep, NightPurpleMid, MagentaGlow, WarmRoseEnd)
)

// تدرج مميز للعناصر البارزة (البطاقة النشطة، الأزرار الرئيسية)
val HeroGradientBrush = Brush.linearGradient(
    colors = listOf(OrchidAccent, MagentaGlow, GoldAccent)
)

val AccentViolet = OrchidAccent
val AccentBlue = SkyAccent

private val DarkColors = darkColorScheme(
    primary = OrchidAccent,
    secondary = GoldAccent,
    background = NightPurpleDeep,
    surface = GlassSurface,
    onPrimary = TextPrimary,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    error = DangerRed
)

@Composable
fun ScheduledMessagesTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColors,
        typography = AppTypography,
        content = content
    )
}
