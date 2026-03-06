package com.sobercompanion.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sobercompanion.ui.theme.AppBorder
import com.sobercompanion.ui.theme.AppSurface
import com.sobercompanion.ui.theme.AppTextSecondary
import com.sobercompanion.ui.theme.NotoSerifKr
import kotlinx.coroutines.delay

private val lineGradient = listOf(Color(0xFFBEB8B0), Color(0xFF9E9890))

@Composable
fun ComfortMessageCard(
    show: Boolean,
    message: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(show) {
        if (show) {
            visible = true
            delay(3_000)
            if (visible) {
                visible = false
                delay(400)
                onDismiss()
            }
        } else {
            visible = false
        }
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(350)) + slideInVertically(tween(350)) { -it / 3 },
        exit  = fadeOut(tween(400)),
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    if (visible) {
                        visible = false
                        onDismiss()
                    }
                },
            shape     = RoundedCornerShape(16.dp),
            colors    = CardDefaults.cardColors(containerColor = AppSurface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            border    = BorderStroke(1.dp, AppBorder),
        ) {
            // IntrinsicSize.Min: 좌측 선이 텍스트 높이에 맞춰 자동으로 늘어남
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
            ) {
                // 좌측 얇은 세로 그라디언트 선
                Spacer(
                    modifier = Modifier
                        .width(2.5.dp)
                        .fillMaxHeight()
                        .drawBehind {
                            drawRect(
                                brush = Brush.verticalGradient(
                                    colors = lineGradient,
                                    startY = 0f,
                                    endY   = size.height
                                )
                            )
                        }
                )

                // 메시지 텍스트
                Text(
                    text       = message,
                    modifier   = Modifier
                        .weight(1f)
                        .padding(start = 15.dp, end = 18.dp, top = 16.dp, bottom = 16.dp),
                    fontFamily = NotoSerifKr,
                    fontWeight = FontWeight.Normal,
                    fontSize   = 13.5.sp,
                    color      = AppTextSecondary,
                    lineHeight = 23.5.sp,   // 13.5 × 1.75
                    letterSpacing = (-0.1).sp,
                )
            }
        }
    }
}
