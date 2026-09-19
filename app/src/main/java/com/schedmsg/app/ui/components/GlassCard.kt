package com.schedmsg.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.schedmsg.app.ui.theme.GlassBorder
import com.schedmsg.app.ui.theme.GlassSurface

/**
 * بطاقة بأسلوب Glassmorphism: خلفية شبه شفافة، حدود ناعمة مضيئة، وزوايا دائرية.
 * تُستخدم كعنصر أساسي متكرر في كل شاشات التطبيق للحفاظ على اتساق التصميم.
 *
 * accentBorderBrush: إن تم تمريره، يُستخدم كحدّ متدرّج مميز بدل الحد الزجاجي العادي،
 * لإبراز بطاقات مهمة مثل الرسالة النشطة حاليًا.
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Int = 24,
    contentPadding: Int = 20,
    accentBorderBrush: Brush? = null,
    content: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius.dp)
    Box(
        modifier = modifier
            .clip(shape)
            .background(
                Brush.linearGradient(
                    colors = listOf(GlassSurface, GlassSurface.copy(alpha = 0.12f))
                )
            )
            .then(
                if (accentBorderBrush != null) {
                    Modifier.border(1.5.dp, accentBorderBrush, shape)
                } else {
                    Modifier.border(1.dp, GlassBorder, shape)
                }
            )
            .padding(contentPadding.dp)
    ) {
        content()
    }
}
