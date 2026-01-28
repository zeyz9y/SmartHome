package com.example.akllev.ui.components.feedback

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Bağlantı durumunu gösteren küçük "pill".
 * Not: "Bağlı değil" durumunda görünmez (senin tercihine göre).
 */
@Composable
fun ConnectionStatusPillInline(
    connected: Boolean,
    busy: Boolean,
    modifier: Modifier = Modifier
) {
    val (text, bg, fg) = when {
        busy -> Triple(
            "Bağlanılıyor...",
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.onSurfaceVariant
        )
        connected -> Triple(
            "Bağlı",
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.onPrimary
        )
        else -> return // 'Bağlı değil' göstermiyoruz
    }

    AssistChip(
        modifier = modifier,
        onClick = { /* no-op */ },
        label = { Text(text) },
        leadingIcon = { Icon(Icons.Filled.Bluetooth, contentDescription = null) },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = bg,
            labelColor = fg,
            leadingIconContentColor = fg
        )
    )
}
