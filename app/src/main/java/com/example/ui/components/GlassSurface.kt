package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.model.GlassEffect
import com.example.ui.theme.LocalGlassEffect

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(12.dp),
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val glassEffect = LocalGlassEffect.current
    val surfaceColor = MaterialTheme.colorScheme.surface

    val (bgBrush, borderStroke) = when (glassEffect) {
        GlassEffect.STANDARD -> {
            Brush.verticalGradient(
                listOf(surfaceColor, surfaceColor)
            ) to BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
        }
        GlassEffect.SOFT_GLASS -> {
            Brush.verticalGradient(
                listOf(
                    surfaceColor.copy(alpha = 0.70f),
                    surfaceColor.copy(alpha = 0.55f)
                )
            ) to BorderStroke(1.dp, Color.White.copy(alpha = 0.14f))
        }
        GlassEffect.FROSTED_GLASS -> {
            Brush.verticalGradient(
                listOf(
                    surfaceColor.copy(alpha = 0.45f),
                    surfaceColor.copy(alpha = 0.25f)
                )
            ) to BorderStroke(1.dp, Color.White.copy(alpha = 0.24f))
        }
    }

    Surface(
        modifier = modifier
            .shadow(
                elevation = if (glassEffect == GlassEffect.STANDARD) 2.dp else 6.dp,
                shape = shape
            ),
        shape = shape,
        color = Color.Transparent,
        border = borderStroke,
        onClick = onClick ?: {}
    ) {
        Box(
            modifier = Modifier
                .background(bgBrush)
        ) {
            content()
        }
    }
}
