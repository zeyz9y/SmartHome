package com.example.akllev.ui.theme

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import androidx.compose.material3.MaterialTheme as M3

data class Spacing(
    val xs: androidx.compose.ui.unit.Dp = 4.dp,
    val s:  androidx.compose.ui.unit.Dp = 8.dp,
    val m:  androidx.compose.ui.unit.Dp = 12.dp,
    val l:  androidx.compose.ui.unit.Dp = 16.dp,
    val xl: androidx.compose.ui.unit.Dp = 24.dp,
    val xxl: androidx.compose.ui.unit.Dp = 32.dp
)

val LocalSpacing = compositionLocalOf { Spacing() }

// Kolay erişim: MaterialTheme.spacing
val M3.spacing: Spacing
    @Composable get() = LocalSpacing.current
